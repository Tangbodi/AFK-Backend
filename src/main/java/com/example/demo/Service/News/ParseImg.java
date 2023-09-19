package com.example.demo.Service.News;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

@Service
public class ParseImg {
    private static final Logger logger = LoggerFactory.getLogger(ParseImg.class);

    public String ParseImage(String str) {
        logger.info("ParseImage:::");
        String imageSrc = "";
        Document document = Jsoup.parse(str);
        for (Element imageElement : document.select("img")) {
            imageSrc = imageElement.attr("src");
            break;
        }
        return imageSrc;
    }
}
