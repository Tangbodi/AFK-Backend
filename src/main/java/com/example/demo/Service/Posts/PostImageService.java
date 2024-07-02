package com.example.demo.Service.Posts;

import com.example.demo.Mapper.Repository.PostImageRepository;
import com.example.demo.Model.DTO.GetPostDTO;
import com.example.demo.Model.DTO.PostDTO;
import com.example.demo.Model.Entity.PostImage;
import com.example.demo.Util.Snowflake;
import net.coobird.thumbnailator.Thumbnails;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.transaction.Transactional;
import java.io.ByteArrayInputStream;
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
    private static final String TOMCAT_POST_IMAGE_PATH = "/opt/tomcat/webapps/IMAGE/POST/";
//    private static final String POST_IMAGE_URL = "http://31.220.21.110:8180/IMAGE/POST/";
private static final String POST_IMAGE_URL = "https://www.away-from-keyboard.com/IMAGE/POST/";
//    private static final String NGINX_POST_IMAGE_PATH = "/usr/local/nginx2/html/IMAGE/POST/";
    private static final int IMAGE_SIZE = 5 * 1024 * 1024;
    private static final String IMAGE_TYPE = "jpg";
    @Autowired
    private PostImageRepository postImageRepository;

    @Async("MultiExecutor")
    @Transactional
    public void SavePostImage(PostDTO postDTO) {
        logger.info("Saving PostImage: {}");
        List<String> imageNameList = postDTO.getPostImageNameList();
        logger.info("ImageNameList: {}", imageNameList);
        try {
            //traverse image name list
            for (int i = 0; i < imageNameList.size(); i++) {
                //create image id for each image
                String imageName = imageNameList.get(i);
                logger.info("ImageName: {}", imageName);
                logger.info("Create PostImage");
                PostImage postImage = new PostImage();
//                Long imageId = Long.valueOf(imageName.substring(0, imageName.indexOf(".")));
                Long imageId = Long.valueOf(imageName);
                logger.info("ImageId: {}", imageId);
                postImage.setId(imageId);
                postImage.setPostId(postDTO.getPostId());
//                String imageType = imageName.substring(imageName.indexOf(".") + 1, imageName.length());
                imageName = imageName+"."+IMAGE_TYPE;
                postImage.setImageType(IMAGE_TYPE);
                postImage.setImagePath(TOMCAT_POST_IMAGE_PATH + imageName);
                postImage.setImageUrl(POST_IMAGE_URL + imageName);
                postImage.setCreatedAt(postDTO.getCreatedAt());
                postImage.setModifiedAt(postDTO.getCreatedAt());
                postImageRepository.save(postImage);
                logger.info("Saved PostImage: {}");
            }
        } catch (Exception e) {
            logger.error("Failed to save PostImage", e.getMessage(), e);
            throw new RuntimeException("Failed to save PostImage " + e);
        }
    }

    public List<String> SavePostImageToServer( MultipartFile[] images) throws IOException {
        logger.info("Saving PostImage to server");
        try {
            List<String> postImageNameList = new ArrayList<>();
            for (MultipartFile image : images) {
                // Check if the uploaded file is an image and its size is within limit (e.g., 5MB)
                if (image.getContentType().startsWith("image/")&&image.getSize() <= IMAGE_SIZE) {
                    // Generate a unique image ID
                    long imageId = Snowflake.generateUniqueId();
                    // Get image data and type
                    byte[] imageData = image.getBytes();
                    logger.info("Image size: {}", image.getSize());
//                    String imageType = image.getContentType();
                    // Create image name
                    String imageName = Long.toString(imageId);
                    logger.info("ImageName: {}", imageName);
                    logger.info("Saving PostImage to Tomcat and Nginx");
                    // Define paths for Tomcat and Nginx
                    Path tomcatImagePath  = Paths.get(TOMCAT_POST_IMAGE_PATH, imageName);
//                    Path nginxImagePath  = Paths.get(TOMCAT_POST_IMAGE_PATH, imageName);
                    Thumbnails.of(new ByteArrayInputStream(imageData))
                            .scale(1f)
                            .outputQuality(0.8) // Adjust quality (0.0 to 1.0)
                            .outputFormat(IMAGE_TYPE)
                            .toFile(tomcatImagePath.toFile());
//                            .size(800, 600) // Set your desired resolution here
                    postImageNameList.add(imageName);
                    logger.info("PostImageNameList: {}", postImageNameList);
                    // Save image to Tomcat and Nginx
                    FileOutputStream fos_tomcat = new FileOutputStream(tomcatImagePath.toFile());
//                    FileOutputStream fos_nginx = new FileOutputStream(nginxImagePath.toFile());
                    fos_tomcat.write(imageData);
//                    fos_nginx.write(imageData);
                    fos_tomcat.close();
//                    fos_nginx.close();
                    logger.info("Saved PostImage to Tomcat");
                    //add image url
                } else {
                    logger.info("Image is not an image or size is too large");
                    return Collections.emptyList();
                }
            }
            return postImageNameList;
        } catch (IOException e) {
            logger.error("Failed to save PostImage to server", e.getMessage(), e);
            throw new IOException("Failed to save PostImage to server " + e);
        } catch (NullPointerException e) {
            logger.error("Failed to save PostImage to server", e.getMessage(), e);
            throw new NullPointerException("Failed to save PostImage to server " + e);
        }

    }

    public List<Map<String, Object>> findAllImageURLsByPostId(GetPostDTO getPostDTO) {
        logger.info("Finding all images by post id");
        try {
            List<Map<String, Object>> postImages = postImageRepository.findAllImageURLByPostId(getPostDTO.getPostId());
            if (!postImages.isEmpty()) {
                logger.info("Found all images by post id");
                return postImages;
            } else {
                logger.info("No image found by post id");
                return Collections.emptyList();
            }
        } catch (Exception e) {
            logger.error("Failed to find all images by post id", e.getMessage(), e);
            return Collections.emptyList();
        }
    }
}
