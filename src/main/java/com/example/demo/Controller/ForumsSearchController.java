package com.example.demo.Controller;

import com.example.demo.Enum.ReturnCode;
import com.example.demo.Model.DTO.ForumSearchDTO;
import com.example.demo.Model.VO.SearchPostVO;
import com.example.demo.Service.Posts.PostService;
import com.example.demo.Util.ApiResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class ForumsSearchController {
    Logger logger = LoggerFactory.getLogger(ForumsSearchController.class);
    @Autowired
    private PostService postService;

    @PostMapping("/search-forums")
    public ResponseEntity searchForumsByKeyword(@Validated @RequestBody ForumSearchDTO forumSearchDTO) {
        ApiResponse apiResponse;
        List<SearchPostVO> searchPostVOList = postService.SearchByKeyword(forumSearchDTO.getKeyword());
        if (!searchPostVOList.isEmpty()) {
            apiResponse = ApiResponse.success(searchPostVOList);
        } else {
            apiResponse = ApiResponse.error(ReturnCode.RC404.getCode() , "No search results found");
        }
        return ResponseEntity.status(apiResponse.getCode()).body(apiResponse);
    }

//    @PostMapping("/search/store")
//    public ResponseEntity searchMerchandiseByKeyword() {
//
//    }
}
