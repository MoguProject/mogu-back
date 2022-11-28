package com.teamof4.mogu.controller;

import com.teamof4.mogu.dto.PostDto;
import com.teamof4.mogu.service.PostService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/posts")
@Api(tags = {"02. Post API"})
public class PostController {

    private final PostService postService;

    @GetMapping("/post")
    @ApiOperation(value = "커뮤니티 게시글 전체 조회", notes = "생성일 기준 내림차순으로 출력")
    public ResponseEntity<List<PostDto.Response>> getPostList() {
        return ResponseEntity.ok(postService.getPostList());
    }

    @GetMapping("/post/{id}")
    @ApiOperation(value = "커뮤니티 게시글 상세 조회")
    public ResponseEntity<PostDto.Response> getPost(@PathVariable Long id) {
        return ResponseEntity.ok(postService.getPostDetails(id));
    }

    @PostMapping("/create")
    @ApiOperation(value = "커뮤니티 게시글 등록")
    public ResponseEntity<Long> savePost(@Valid PostDto.SaveRequest dto) {
        return ResponseEntity.ok(postService.savePost(dto));
    }

    @PostMapping("/update/{postId}")
    @ApiOperation(value = "커뮤니티 게시글 수정")
    public ResponseEntity<Long> updatePost(@PathVariable Long postId, @Valid PostDto.UpdateRequest dto) {
        return ResponseEntity.ok(postService.updatePost(postId, dto));
    }

    @PostMapping("/delete/{postId}")
    @ApiOperation(value = "커뮤니티 게시글 삭제")
    public ResponseEntity<Void> deletePost(@PathVariable Long postId) {
        postService.deletePost(postId);
        return ResponseEntity.ok().build();
    }
}
