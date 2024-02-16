package com.TaskManager.Controller;

import com.TaskManager.Model.Comment;
import com.TaskManager.Services.CommentService;
import com.TaskManager.UtilityClasses.ApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(path = "api/v1/comment")
public class CommentController {

    @Autowired
    private CommentService commentService;

    @PostMapping
    public ResponseEntity<ApiResponse<Comment>> makeComment(@RequestBody Comment comment, @RequestHeader("Authorization") String header){
        ApiResponse<Comment> res = commentService.makeComment(comment,header.substring(7));
        return new ResponseEntity<>(res, HttpStatusCode.valueOf(res.getStatusCode()));
    }

    @GetMapping(path = "/{taskId}")
    public ResponseEntity<ApiResponse<List<Comment>>> seeComments(@PathVariable Long taskId, @RequestHeader("Authorization") String header){
        ApiResponse<List<Comment>> res = commentService.seeComments(taskId,header.substring(7));
        return new ResponseEntity<>(res,HttpStatusCode.valueOf(res.getStatusCode()));
    }

}
