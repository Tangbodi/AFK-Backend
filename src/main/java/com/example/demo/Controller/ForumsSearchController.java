package com.example.demo.Controller;

import com.example.demo.Constant.Enum.ReturnCode;
import com.example.demo.Model.DTO.ForumSearchDTO;
import com.example.demo.Model.VO.SearchPostVO;
import com.example.demo.Service.Posts.PostService;
import com.example.demo.Util.ApiResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
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
    public ResponseEntity SearchForumsByKeyword(@Validated @RequestBody ForumSearchDTO forumSearchDTO) {
        ApiResponse apiResponse;
        switch (forumSearchDTO.getType()) {
            case "store":
                apiResponse = ApiResponse.error(ReturnCode.RC200.getCode(), "No search results found");
                break;
            case "forum":
                List<SearchPostVO> searchPostVOList = postService.SearchByKeyword(forumSearchDTO.getKeyword());
                if (!searchPostVOList.isEmpty()) {
                    apiResponse = ApiResponse.success(searchPostVOList);
                } else {
                    apiResponse = ApiResponse.error(ReturnCode.RC200.getCode(), "No search results found");
                }
                break;
            default:
                apiResponse = ApiResponse.error(ReturnCode.RC200.getCode(), "Invalid type");
                break;
        }
        return ResponseEntity.status(apiResponse.getCode()).body(apiResponse);
    }

}
