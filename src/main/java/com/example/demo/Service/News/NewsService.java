package com.example.demo.Service.News;

import com.example.demo.Model.Entity.News;
import com.example.demo.Service.Redis.RedisNewsService;
import com.example.demo.Mapper.Repository.NewsRepository;
import com.example.demo.Model.VO.NewsVO;
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
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

@Service
public class NewsService {
    private static final Logger logger = LoggerFactory.getLogger(NewsService.class);
    private static final String GameSpot = "GameSpot";
    private static final String rssFeedUrl = "https://www.gamespot.com/feeds/mashup";
    @Autowired
    private NewsRepository newsRepository;
    @Autowired
    private RedisNewsService redisNewsService;
    public void ProxyXML() {
        logger.info("Proxying XML");
        try {
            HttpClient httpClient = HttpClientBuilder.create().build();
            HttpGet httpGet = new HttpGet(rssFeedUrl);
            HttpResponse response = httpClient.execute(httpGet);
            if (response.getStatusLine().getStatusCode() == HttpStatus.OK.value()) {
                String rssFeed = EntityUtils.toString(response.getEntity(), "UTF-8");
                SaveNewsXML(rssFeed);
            }
        } catch (ClientProtocolException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    @Transactional
    public void SaveNewsXML(String rssFeed) {
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
                news.setSource(GameSpot);
                news.setCreatedAt(Instant.now());
                news.setModifiedAt(Instant.now());
                String newsId = item.getElementsByTagName("guid").item(0).getTextContent();
                logger.info("NewsId: {}" + newsId);
                news.setId(newsId);

                String title = item.getElementsByTagName("title").item(0).getTextContent();
                logger.info("Title: {}" + title);
                news.setTitle(title);

                String link = item.getElementsByTagName("link").item(0).getTextContent();
                logger.info("Link: {}" + link);
                news.setLink(link);

                String description = item.getElementsByTagName("description").item(0).getTextContent();
                logger.info("Before_Description: {}" + description);
                description = GetDescriptionText(description);
                logger.info("Description: {}" + description);
                news.setDescription(description);

                String pubDate = item.getElementsByTagName("pubDate").item(0).getTextContent();
                logger.info("Before_PubDate: {}" + pubDate);
                pubDate = ParsePubDate(pubDate);
                logger.info("After_PubDate: {}" + pubDate);
                news.setPubDate(pubDate);

                String mediaContentUrl = item.getElementsByTagName("media:content").item(0).getAttributes().getNamedItem("url").getTextContent();
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
    public List<NewsVO> GetNews() {
        logger.info("Getting News");
        try{
            List<News> newsList = newsRepository.findNewsByPublishDate();
            if(!newsList.isEmpty()){
                logger.info("News found");
                return TransferToNewsVO(newsList);
            }
            else{
                logger.info("News not found");
                return Collections.emptyList();
            }
        } catch (Exception e) {
            logger.error("Failed to get News");
            return Collections.emptyList();
        }
    }
    private List<NewsVO> TransferToNewsVO(List<News> newsList){
        logger.info("Transferring to NewsVO");
        List<NewsVO> newsVOList = new ArrayList<>();
        try{
            for(News news : newsList){
                NewsVO newsVO = new NewsVO();
                newsVO.setNewsId(news.getId());
                newsVO.setTitle(news.getTitle());
                newsVO.setLink(news.getLink());
                newsVO.setDescription(news.getDescription());
                newsVO.setPubDate(news.getPubDate());
                newsVO.setMediaContentUrl(news.getMediaContentUrl());
                newsVOList.add(newsVO);
            }
            redisNewsService.UpdateNewsCache(newsVOList);
        } catch (Exception e) {
           logger.error("Error in transferring to NewsVO");
        }
        return newsVOList;
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
    private static String GetDescriptionText(String description) {
        logger.info("Parsing DescriptionText");
        // Remove CDATA section
        String cleanedDescription = description.replaceAll("<!\\[CDATA\\[.*?\\]\\]>", "");
        // Remove HTML tags
        cleanedDescription = Jsoup.parse(cleanedDescription).text();
        return cleanedDescription;
    }}
