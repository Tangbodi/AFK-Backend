package com.example.demo.Service.Comments;

import com.example.demo.Controller.PostsController;
import com.example.demo.Mapper.Repository.CommentRepository;
import com.example.demo.Service.IP.IpAddressService;
import com.example.demo.Service.MQ.MQSender;
import com.example.demo.Service.Message.MessageService;
import com.example.demo.Service.Posts.PostInfoService;
import com.example.demo.Service.Redis.RedisService;
import com.example.demo.Service.Replies.ReplyService;
import com.example.demo.Service.UserSettings.CommentOnPostMentionService;
import com.example.demo.Service.UserSettings.UserSettingService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Lazy;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
@RunWith(SpringRunner.class)
@SpringBootTest
class CommentServiceTest {
    @InjectMocks
    private CommentService commentService;

    @Mock
    private CommentRepository commentRepository;
    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        // Set up mock behavior for dependencies if needed
    }
    @Test
    void getAllCommentsByPostId() {
        Long postId = 7101919936771133440L;
        Long userId = 7099788373560266752L;
        List<Map<String, Object>> expectedComments = new ArrayList<>();

        Mockito.when(commentRepository.findCommentsByPostId(postId,userId)).thenReturn(expectedComments);
        List<Map<String, Object>> comments = commentService.GetAllCommentsByPostId(postId, userId);

        // Assert the result or perform assertions based on your requirements
        // For example:
//        assertEquals(expectedComments, comments);
        System.out.println(comments);
    }
}