package com.example.demo.Service.Posts;

import com.example.demo.Mapper.Repository.PostImageRepository;
import com.example.demo.Model.DTO.GetPostDTO;
import com.example.demo.Model.DTO.PostDTO;
import com.example.demo.Model.Entity.PostImage;
import com.example.demo.Service.Redis.RedisPostService;
import com.example.demo.Util.TimestampCreator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.transaction.Transactional;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Queue;


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

    @Transactional
    public void SavePostImage(List<MultipartFile> imageFiles, PostDTO postDTO) throws IOException {
        logger.info("Saving PostImage: {}");
        try {
            //traverse imageFiles
            for (int i=0; i< imageFiles.size(); i++) {
                //create image id for each image
                MultipartFile imageFile = imageFiles.get(i);
                String imageId = TimestampCreator.CreateTimestamp();
                logger.info("Created PostImage Id: {}", imageId);
                PostImage postImage = new PostImage();
                postImage.setId(imageId);
                postImage.setPostId(postDTO.getPostId());

                String imageType = imageFile.getContentType().substring(6,imageFile.getContentType().length());
                String imageName = imageId + "." + imageType;
                postImage.setImageType(imageType);
                postImage.setImagePath(TOMCAT_POST_IMAGE_PATH + imageName);
                postImage.setImageUrl(POST_IMAGE_URL + imageName);
                byte[] imageData = imageFile.getBytes();
                postImage.setCreatedAt(postDTO.getCreatedAt());
                postImage.setModifiedAt(postDTO.getCreatedAt());
                postImageRepository.save(postImage);
                logger.info("Saved PostImage: {}", postImage);
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
            }
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    public List<Map<Short,Object>> findAllImageURLByPostId(GetPostDTO getPostDTO){
        logger.info("Finding all images by post id");
        try{
            List<Map<Short,Object>> postImages = postImageRepository.findAllImageURLByPostId(getPostDTO.getPostId());
            if(!postImages.isEmpty()){
                logger.info("Found all images by post id");
                return postImages;
            }else{
                logger.info("No images found by post id");
                return Collections.emptyList();
            }
        } catch (Exception e){
            logger.error("Failed to find all images by post id", e);
            return Collections.emptyList();
        }
    }
}
