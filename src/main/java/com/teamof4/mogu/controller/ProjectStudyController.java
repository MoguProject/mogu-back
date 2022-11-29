package com.teamof4.mogu.controller;

import com.teamof4.mogu.dto.PostDto;
import com.teamof4.mogu.dto.ProjectStudyDto;
import com.teamof4.mogu.service.ProjectStudyService;
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
@RequestMapping("/projectstudy")
@Api(tags = {"03. Project/Study API"})
public class ProjectStudyController {

    private final ProjectStudyService projectStudyService;

    /**
     * 4(SIDE_PROJECT), 5(STUDY)
     * 위의 카테고리 아이디로 조회할 때만 사용한다.
     */
    @GetMapping("/list/all/{categoryId}")
    @ApiOperation(value = "프로젝트/스터디 게시글 전체 조회", notes = "모집 여부와 상관없이 카테고리 별, 생성일 기준 내림차 순으로 출력한다.")
    public ResponseEntity<List<ProjectStudyDto.Response>> getAllPostList(@PathVariable Long categoryId) {
        return ResponseEntity.ok(projectStudyService.getAllProjectStudyList(categoryId));
    }

    @GetMapping("/list/opened/{categoryId}")
    @ApiOperation(value = "프로젝트/스터디 게시글 전체 조회(모집 중)", notes = "모집 중인 게시글들만 카테고리 별, 생성일 기준 내림차 순으로 출력한다.")
    public ResponseEntity<List<ProjectStudyDto.Response>> getOpenedPostList(@PathVariable Long categoryId) {
        return ResponseEntity.ok(projectStudyService.getOpenedProjectStudyList(categoryId));
    }

    @GetMapping("/post/{postId}")
    @ApiOperation(value = "프로젝트/스터디 게시글 상세 조회")
    public ResponseEntity<ProjectStudyDto.Response> getPost(@PathVariable Long postId,
                                                            @AuthenticationPrincipal Long currentUserId) {
        return ResponseEntity.ok(projectStudyService.getProjectStudyDetails(postId, currentUserId));
    }

    @PostMapping("/create")
    @ApiOperation(value = "프로젝트/스터디 게시글 등록")
    public ResponseEntity<Long> saveProjectStudy(@Valid PostDto.SaveRequest postDto,
                                                 @Valid @RequestBody ProjectStudyDto.Request projectStudyDto) {
        return ResponseEntity.ok(projectStudyService.saveProjectStudy(postDto, projectStudyDto));
    }

    @PostMapping("/update/{postId}")
    @ApiOperation(value = "프로젝트/스터디 게시글 수정")
    public ResponseEntity<Long> updatePost(@PathVariable Long postId,
                                           @Valid PostDto.UpdateRequest postDto,
                                           @Valid @RequestBody ProjectStudyDto.Request projectStudyDto) {
        return ResponseEntity.ok(projectStudyService.updateProjectStudy(postId, postDto, projectStudyDto));
    }

}
