package com.example.demo.Service.News;

import com.example.demo.Constant.Enum.NewsEnum;
import com.example.demo.Mapper.Repository.NewsRepository;
import com.example.demo.Model.Entity.News;
import com.example.demo.Util.Snowflake;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
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

@Service
public class GameRantNewsService {
    private static final Logger logger = LoggerFactory.getLogger(GameRantNewsService.class);
    private static final String Source = "Game Rant";
    @Autowired
    private NewsRepository newsRepository;

    public void ProxyXML(Integer gameId) {
        logger.info("Starting proxy XML for gameId: {}",gameId);
        String rssFeedUrl = NewsEnum.GetRSSUrl(gameId);
        logger.info("rssFeedUrl: {}",rssFeedUrl);
        try{
            HttpClient httpClient = HttpClientBuilder.create().build();
            HttpGet httpGet = new HttpGet(rssFeedUrl);
            HttpResponse response = httpClient.execute(httpGet);
            if (response.getStatusLine().getStatusCode() == HttpStatus.OK.value()) {
                String rssFeed = EntityUtils.toString(response.getEntity(), "UTF-8");
                SaveNewsXML(rssFeed, gameId);
            }
        } catch (ClientProtocolException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    @Transactional
    public void SaveNewsXML(String rssFeed, Integer gameId) {
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
                String newsId = String.valueOf(Snowflake.generateUniqueId());
                logger.info("NewsId: {}",newsId);
                news.setId(newsId);
                news.setSource(Source);
                news.setGameId(gameId.shortValue());
                logger.info("GameId: {}",gameId);
                news.setCreatedAt(Instant.now());
                news.setModifiedAt(Instant.now());

                String title = item.getElementsByTagName("title").item(0).getTextContent();
                title = RemoveCDATA(title);
                logger.info("Title: {}" + title);
                news.setTitle(title);

                String link = item.getElementsByTagName("link").item(0).getTextContent();
                link = RemoveCDATA(link);
                logger.info("Link: {}" + link);
                news.setLink(link);

                String description = item.getElementsByTagName("description").item(0).getTextContent();
                description = RemoveCDATA(description).trim();

                logger.info("Parsed Description: {}" + description);
                news.setDescription(description);

                String content = item.getElementsByTagName("content:encoded").item(0).getTextContent();
                content = RemoveCDATA(content).trim();
                logger.info("Parsed Content: {}" + content);
                news.setContent(content);

                String pubDate = item.getElementsByTagName("pubDate").item(0).getTextContent();
                pubDate = ParsePubDate(pubDate);
                logger.info("Parsed_PubDate: {}" + pubDate);
                news.setPubDate(pubDate);

                String mediaContentUrl = item.getElementsByTagName("enclosure").item(0).getAttributes().getNamedItem("url").getTextContent();
                logger.info("MediaContentUrl: {}" + mediaContentUrl);
                news.setMediaContentUrl(mediaContentUrl);

                if(newsRepository.save(news)!= null){;
                    logger.info("News saved");
                }
                else{
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
        try{
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
    @Transactional
    public void DeleteNews(){
        logger.info("Deleting News");
        newsRepository.deleteAllNewsBySource(Source);
    }
}
