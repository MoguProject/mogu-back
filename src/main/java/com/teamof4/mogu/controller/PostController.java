package com.teamof4.mogu.controller;

import com.teamof4.mogu.dto.PostDto;
import com.teamof4.mogu.service.PostService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/posts")
@Api(tags = {"02. Post API"})
public class PostController {

    private final PostService postService;

    /**
     * 1(CM_TEAM), 2(CM_PERSONAL), 3(CM_LOUNGE)
     * 위의 카테고리 아이디로 조회할 때만 사용한다.
     */
    @GetMapping("/list/{categoryId}")
    @ApiOperation(value = "커뮤니티 게시글 전체 조회", notes = "카테고리 별, 생성일 기준 내림차 순으로 출력한다.")
    public ResponseEntity<List<PostDto.Response>> getPostList(@PathVariable Long categoryId) {
        return ResponseEntity.ok(postService.getPostList(categoryId));
    }

    @GetMapping("/post/{id}")
    @ApiOperation(value = "커뮤니티 게시글 상세 조회")
    public ResponseEntity<PostDto.Response> getPost(@PathVariable Long id,
                                                    @AuthenticationPrincipal Long currentUserId) {
        return ResponseEntity.ok(postService.getPostDetails(id, currentUserId));
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
