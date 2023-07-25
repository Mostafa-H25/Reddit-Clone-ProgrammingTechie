package com.programmingtechie.redditclone.controller;

import com.programmingtechie.redditclone.dto.CommentDto;
import com.programmingtechie.redditclone.service.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/comments/")
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;

    @PostMapping
    public ResponseEntity<Void> createComment(@RequestBody CommentDto commentDto){
        commentService.save(commentDto);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @GetMapping("/by-post/{id}")
    public ResponseEntity<List<CommentDto>> getAllCommentsByPost(@PathVariable("id") Long postId){
        return ResponseEntity.status(HttpStatus.OK).body(commentService.getAllCommentsByPost(postId));
    }

    @GetMapping("/by-user/{name}")
    public ResponseEntity<List<CommentDto>> getAllCommentsByPost(@PathVariable("name") String username){
        return ResponseEntity.status(HttpStatus.OK).body(commentService.getAllCommentsByUser(username));
    }

}
