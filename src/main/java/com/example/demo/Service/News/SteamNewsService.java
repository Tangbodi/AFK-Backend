package com.example.demo.Service.News;

import com.example.demo.Constant.Enum.NewsIconEnum;
import com.example.demo.Constant.Enum.NewsRSSEnum;
import com.example.demo.Mapper.Repository.NewsRepository;
import com.example.demo.Model.Entity.News;
import com.example.demo.Service.Redis.RedisNewsService;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.jsoup.Jsoup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.transaction.Transactional;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class SteamNewsService {
    private static final Logger logger = LoggerFactory.getLogger(SteamNewsService.class);
    private static final String Source = "Steam";
    private static final String SteamCommunity = "steamcommunity";
    private static final String SteamPowered = "steampowered";
    private static final int MaxLength = 255;
    @Autowired
    private NewsRepository newsRepository;
    @Autowired
    private RedisNewsService redisNewsService;
    @Autowired
    private ParseImg parseImg;

    public void ProxyXML(Integer gameId) {
        logger.info("Starting proxy XML for gameId: {}", gameId);
        String rssFeedUrl = NewsRSSEnum.GetRSSUrl(gameId);
        Integer res = 0;
        logger.info("rssFeedUrl: {}", rssFeedUrl);
        if (rssFeedUrl.contains(SteamCommunity)) {
            logger.info("SteamCommunity");
            res = 1;//detail
        } else if (rssFeedUrl.contains(SteamPowered)) {
            res = 2;//view
        }
        try {
            HttpClient httpClient = HttpClientBuilder.create().build();
            HttpGet httpGet = new HttpGet(rssFeedUrl);
            HttpResponse response = httpClient.execute(httpGet);
            if (response.getStatusLine().getStatusCode() == HttpStatus.OK.value()) {
                String rssFeed = EntityUtils.toString(response.getEntity(), "UTF-8");
                SaveNewsXML(rssFeed, gameId, res);
            }
        } catch (ClientProtocolException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Transactional
    public void SaveNewsXML(String rssFeed, Integer gameId, Integer res) {
        logger.info("Saving XML");
        try {
            ByteArrayInputStream inputStream = new ByteArrayInputStream(rssFeed.getBytes());
            DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
            Document document = documentBuilder.parse(inputStream);
            Element root = document.getDocumentElement();
            NodeList items = root.getElementsByTagName("item");
            for (int i = 0; i < items.getLength(); i++) {
                Element item = (Element) items.item(i);
                News news = new News();
                news.setSource(Source);
                news.setGameId(gameId.shortValue());
                logger.info("GameId: {}", gameId);
                news.setCreatedAt(Instant.now());
                news.setModifiedAt(Instant.now());

                String title = item.getElementsByTagName("title").item(0).getTextContent();
                logger.info("Title: {}" + title);
                news.setTitle(title);

                String link = item.getElementsByTagName("link").item(0).getTextContent();
                link = RemoveCDATA(link);
                logger.info("Link: {}" + link);
                String newsId = "";

                String content = item.getElementsByTagName("description").item(0).getTextContent();
                content = RemoveCDATA(content);
                logger.info("Parsed Description: {}" + content);
//                news.setDescription(description);
                news.setContent(content);
                String descriptionHTML = content.length() >= MaxLength ? content.substring(0, MaxLength) : content;
                String cleanedDescription = RemoveHTMLTags(descriptionHTML);
                news.setDescription(cleanedDescription);
                if (res == 1) {
                    Pattern pattern = Pattern.compile("/detail/(\\d+)$");
                    Matcher matcher = pattern.matcher(link);
                    if (matcher.find()) {
                        newsId = matcher.group(1);
                    }
                } else {
                    Pattern pattern = Pattern.compile("/view/(\\d+)$");
                    Matcher matcher = pattern.matcher(link);
                    if (matcher.find()) {
                        newsId = matcher.group(1);
                    }
                }
                String mediaContentUrl = "";
                NodeList enclosures = item.getElementsByTagName("enclosure");
                if (enclosures.getLength() > 0) {
                    mediaContentUrl = item.getElementsByTagName("enclosure").item(0).getAttributes().getNamedItem("url").getTextContent();
                } else {
                    mediaContentUrl = ParseImage(content);
                }
                logger.info("MediaContentUrl: {}" + mediaContentUrl);
                if (mediaContentUrl != null && !mediaContentUrl.isEmpty()) {
                    news.setMediaContentUrl(mediaContentUrl);
                } else {
                    news.setMediaContentUrl(NewsIconEnum.GetMediaUrl(gameId));
                }
                news.setId(newsId);
                news.setLink(link);

                String pubDate = item.getElementsByTagName("pubDate").item(0).getTextContent();
                pubDate = ParsePubDate(pubDate);
                logger.info("Parsed_PubDate: {}" + pubDate);
                news.setPubDate(pubDate);

                if (newsRepository.save(news) != null) {
                    logger.info("News saved");
                } else {
                    logger.info("News not saved");
                }
            }
        } catch (ParserConfigurationException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (SAXException e) {
            throw new RuntimeException(e);
        }
    }

    private static String ParsePubDate(String pubDate) {
        logger.info("Parsing PubDate");

        SimpleDateFormat inputFormat = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss Z");
        SimpleDateFormat outputFormat = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss");
        try {
            Date date = inputFormat.parse(pubDate);
            String formattedDate = outputFormat.format(date);
            logger.info("FormattedDate: {}" + formattedDate);
            return formattedDate;
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }

    private static String RemoveCDATA(String description) {
        logger.info("Removing CDATA");
        // Remove CDATA section
        String cleanedString = description.replaceAll("<!\\[CDATA\\[.*?\\]\\]>", "");
        // Remove HTML tags
//        cleanedDescription = Jsoup.parse(cleanedDescription).text();
        return cleanedString;
    }

    private static String RemoveHTMLTags(String description) {
        logger.info("Removing HTML Tags");
        // Remove CDATA section
        String cleanedDescription = description.replaceAll("<.*?>", "");
        if (cleanedDescription.contains("<")) {
            int index = cleanedDescription.indexOf("<");
            cleanedDescription = cleanedDescription.substring(0, index);
        }
        // Remove HTML tags
        return cleanedDescription;
    }

    private static String ParseImage(String str) {
        logger.info("ParseImage:::");
        String imageSrc = "";
        org.jsoup.nodes.Document document = Jsoup.parse(str);
        for (org.jsoup.nodes.Element imageElement : document.select("img")) {
            imageSrc = imageElement.attr("src");
            break;
        }
        return imageSrc;
    }
}
