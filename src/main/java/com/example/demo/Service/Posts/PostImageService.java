package com.example.demo.Service.Posts;

import com.example.demo.Mapper.Repository.PostImageRepository;
import com.example.demo.Model.DTO.GetPostDTO;
import com.example.demo.Model.DTO.PostDTO;
import com.example.demo.Model.Entity.PostImage;
import com.example.demo.Service.Redis.RedisPostService;
import com.example.demo.Util.Snowflake;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.transaction.Transactional;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;


@Service
public class PostImageService {
    private static final Logger logger = LoggerFactory.getLogger(PostImageService.class);
    private static final String TOMCAT_POST_IMAGE_PATH = "/opt/tomcat2/webapps/IMAGE/POST/";
    private static final String POST_IMAGE_URL = "31.220.21.110:81/IMAGE/POST/";
    private static final String NGINX_POST_IMAGE_PATH = "/usr/local/nginx2/html/IMAGE/POST/";
    @Autowired
    private PostImageRepository postImageRepository;
    @Autowired
    private RedisPostService redisPostService;


    @Async("MultiExecutor")
    @Transactional(rollbackOn = Exception.class)
    public void SavePostImage(PostDTO postDTO) {
        logger.info("Saving PostImage: {}");
        List<String> imageNameList = postDTO.getPostImageNameList();
        try {
            //traverse imageFiles
            for (int i = 0; i < imageNameList.size(); i++) {
                //create image id for each image
                String imageName = imageNameList.get(i);
                logger.info("ImageName: {}", imageName);
                logger.info("Create PostImage");
                PostImage postImage = new PostImage();
                Long imageId = Long.valueOf(imageName.substring(0, imageName.indexOf(".")));
                logger.info("ImageId: {}", imageId);
                postImage.setId(imageId);
                postImage.setPostId(postDTO.getPostId());
                String imageType = imageName.substring(imageName.indexOf(".") + 1, imageName.length());
                postImage.setImageType(imageType);
                postImage.setImagePath(TOMCAT_POST_IMAGE_PATH + imageName);
                postImage.setImageUrl(POST_IMAGE_URL + imageName);
                postImage.setCreatedAt(postDTO.getCreatedAt());
                postImage.setModifiedAt(postDTO.getCreatedAt());
                postImageRepository.save(postImage);
                logger.info("Saved PostImage: {}", postImage);
            }
        } catch (Exception e) {
            logger.error("Failed to save PostImage", e.getMessage(), e);
            throw new RuntimeException("Failed to save PostImage " + e);
        }
    }

    public List<String> SavePostImageToServer(List<MultipartFile> images) throws IOException {
        logger.info("Saving PostImage to server");
        try {
            List<String> postImageNameList = new ArrayList<>();
            for (int i = 0; i < images.size(); i++) {
                //create image id for each image
                long imageId = Snowflake.generateUniqueId();
                MultipartFile image = images.get(i);
                //parse image data and type
                byte[] imageData = image.getBytes();
                String imageType = image.getContentType().substring(6, image.getContentType().length());
                //create image name
                String imageName = imageId + "." + imageType;
                logger.info("ImageName: {}", imageName);
                logger.info("Saving PostImage to Tomcat and Nginx");
                Path Tomcat_imagePath = Paths.get(TOMCAT_POST_IMAGE_PATH, imageName);
                Path Nginx_imagePath = Paths.get(NGINX_POST_IMAGE_PATH, imageName);
                FileOutputStream fos_tomcat = new FileOutputStream(Tomcat_imagePath.toFile());
                FileOutputStream fos_nginx = new FileOutputStream(Nginx_imagePath.toFile());
                fos_tomcat.write(imageData);
                fos_nginx.write(imageData);
                fos_tomcat.close();
                fos_nginx.close();
                logger.info("Saved PostImage to Tomcat and Nginx");
                //add image url
                postImageNameList.add(imageName);
            }
            return postImageNameList;
        } catch (IOException e) {
            logger.error("Failed to save PostImage to server", e.getMessage(), e);
            throw new IOException("Failed to save PostImage to server " + e);
        }

    }

    public List<Map<Short, Object>> findAllImageURLsByPostId(GetPostDTO getPostDTO) {
        logger.info("Finding all images by post id");
        try {
            List<Map<Short, Object>> postImages = postImageRepository.findAllImageURLByPostId(getPostDTO.getPostId());
            if (!postImages.isEmpty()) {
                logger.info("Found all images by post id");
                return postImages;
            } else {
                logger.info("No images found by post id");
                return Collections.emptyList();
            }
        } catch (Exception e) {
            logger.error("Failed to find all images by post id", e.getMessage(), e);
            return Collections.emptyList();
        }
    }
}
