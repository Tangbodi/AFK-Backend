package com.example.demo.Controller;

import com.example.demo.Model.DTO.CommentDTO;
import com.example.demo.Service.Comments.CommentService;
import com.example.demo.Service.Games.GameGenreService;
import com.example.demo.Service.Posts.PostService;
import com.example.demo.Util.ApiResponse;
import com.example.demo.Util.GenreValidator;
import com.example.demo.Util.PostIdValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

@RestController
public class CommentsController {
    private static final Logger logger = LoggerFactory.getLogger(CommentsController.class);

    @Autowired
    private PostService postService;
    @Autowired
    private GameGenreService gameGenreService;
    @Autowired
    private CommentService commentService;
    @PostMapping("/post/{genreId}/{postId}/edit-comment")
    public ResponseEntity EditComment(HttpServletRequest request, @PathVariable Byte genreId, @PathVariable String postId, @RequestBody CommentDTO commentDTO) {
        if(!PostIdValidator.CheckPostId(postId) || !postService.GetPost(postId)){
            ApiResponse errorResponse = ApiResponse.error(404, "Post not found");
        } else if(!GenreValidator.CheckGenreId(genreId) || !gameGenreService.isGameGenreExist(genreId)){
            ApiResponse errorResponse = ApiResponse.error(404, "Genre not found");
        } else {
            //do nothing
        }
        commentService.SetComment(commentDTO);
        return ResponseEntity.ok().build();
    }
}
