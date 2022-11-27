package com.teamof4.mogu.controller;

import com.teamof4.mogu.dto.PostDTO;
import com.teamof4.mogu.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/posts")
public class PostController {

    private final PostService postService;

    @GetMapping("/post")
    public ResponseEntity<List<PostDTO.ListResponse>> getPostList() {
        return ResponseEntity.ok(postService.getPostList());
    }

    @GetMapping("/post/{id}")
    public ResponseEntity<PostDTO.Response> getPost(@PathVariable Long id) {
        return ResponseEntity.ok(postService.getPostDetails(id));
    }

    @PostMapping("/create")
    public ResponseEntity<Long> savePost(PostDTO.SaveRequest dto) {
        return ResponseEntity.ok(postService.savePost(dto));
    }

    @PostMapping("/update/{postId}")
    public ResponseEntity<Long> updatePost(@PathVariable Long postId, PostDTO.UpdateRequest dto) {
        return ResponseEntity.ok(postService.updatePost(postId, dto));
    }

    @PostMapping("/delete/{postId}")
    public ResponseEntity<Void> deletePost(@PathVariable Long postId) {
        postService.deletePost(postId);
        return ResponseEntity.ok().build();
    }
}
