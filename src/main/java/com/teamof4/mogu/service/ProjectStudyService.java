package com.teamof4.mogu.service;

import com.teamof4.mogu.constants.SortStatus;
import com.teamof4.mogu.dto.PostDto;
import com.teamof4.mogu.dto.ProjectStudyDto;
import com.teamof4.mogu.entity.*;
import com.teamof4.mogu.exception.post.ProjectStudyNotFoundException;
import com.teamof4.mogu.repository.PostRepository;
import com.teamof4.mogu.repository.PostSkillRepository;
import com.teamof4.mogu.repository.ProjectStudyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static com.teamof4.mogu.constants.SortStatus.ALL;
import static com.teamof4.mogu.constants.SortStatus.OPENED;

@Service
@RequiredArgsConstructor
public class ProjectStudyService {

    private final PostRepository postRepository;
    private final ProjectStudyRepository projectStudyRepository;
    private final PostSkillRepository postSkillRepository;
    private final PostService postService;

    public Page<ProjectStudyDto.Response> getSearchedList(Long categoryId, String keyword, Long currentUserId,
                                                             Pageable pageable, SortStatus status) {
        Category category = postService.getCategory(categoryId);

        Page<ProjectStudy> projectStudies =
                projectStudyRepository.findAllByTitleAndContentContainingIgnoreCase(keyword, category, pageable);

        Page<ProjectStudyDto.Response> projectStudyDtoList = new PageImpl<>(Collections.emptyList());
        if (status.equals(ALL)) {
            projectStudyDtoList = getResponses(projectStudies, currentUserId);
        } else if (status.equals(OPENED)) {
            projectStudyDtoList = getOpenedPageImpl(getResponses(projectStudies, currentUserId));
        }

        return projectStudyDtoList;
    }

    public Page<ProjectStudyDto.Response> getProjectStudyList(Long categoryId, Pageable pageable,
                                                              Long currentUserId, SortStatus status) {

        Category category = postService.getCategory(categoryId);
        Page<ProjectStudy> projectStudies = projectStudyRepository.findAll(category, pageable);

        Page<ProjectStudyDto.Response> projectStudyDtoList = new PageImpl<>(Collections.emptyList());

        switch (status) {
            case ALL:
                projectStudyDtoList = getResponses(projectStudies, currentUserId);
                break;
            case OPENED:
                projectStudyDtoList = getOpenedPageImpl(getResponses(projectStudies, currentUserId));
                break;
            case LIKES:
                projectStudies = projectStudyRepository.findAllLikesDesc(category, pageable);
                projectStudyDtoList = getOpenedPageImpl(getResponses(projectStudies, currentUserId));
                break;
            case DEFAULT:
                break;
        }

        return projectStudyDtoList;
    }

    public ProjectStudyDto.Response getProjectStudyDetails(Long postId, Long currentUserId) {
        Post post = postService.getPost(postId);

        if (!post.getUser().getId().equals(currentUserId)) {
            post.addViewCount(post.getView());
            postRepository.save(post);
        }

        ProjectStudy projectStudy = getProjectStudy(post.getId());

        return ProjectStudyDto.Response.builder()
                .post(post)
                .projectStudy(projectStudy)
                .images(postService.getImages(postId))
                .isLiked(postService.isLikedByCurrentUser(currentUserId, post)).build();

    }

    @Transactional
    public Long saveProjectStudy(PostDto.SaveRequest postDTO,
                                 ProjectStudyDto.Request projectStudyDTO, Long currentUserId) {

        Long postId = postService.savePost(postDTO, currentUserId);
        Post post = postService.getPost(postId);

        ProjectStudy projectStudy = projectStudyDTO.toEntity(post);
        projectStudyRepository.save(projectStudy);

        savePostSkill(projectStudyDTO.getSkills(), projectStudy);

        return projectStudy.getId();
    }

    @Transactional
    public Long updateProjectStudy(Long postId, PostDto.UpdateRequest postDTO,
                                   ProjectStudyDto.Request projectStudyDTO) {

        postService.updatePost(postId, postDTO);

        ProjectStudy projectStudy = getProjectStudy(postId);
        projectStudy.updateProjectStudy(projectStudyDTO);

        projectStudyRepository.save(projectStudy);

        return projectStudy.getId();
    }

    private ProjectStudy getProjectStudy(Long postId) {
        return projectStudyRepository.findByPost(postService.getPost(postId))
                .orElseThrow(() -> new ProjectStudyNotFoundException("프로젝트/스터디 상세 정보가 존재하지 않습니다."));
    }

    private void savePostSkill(List<Skill> skills, ProjectStudy projectStudy) {
        for (Skill skill : skills) {
            PostSkill postSkill = PostSkill.builder()
                    .projectStudy(projectStudy)
                    .skill(skill).build();
            postSkillRepository.save(postSkill);
        }
    }

    private Page<ProjectStudyDto.Response> getResponses(Page<ProjectStudy> projectStudies, Long currentUserId) {
        List<ProjectStudyDto.Response> responseList = new ArrayList<>();

        for (ProjectStudy post : projectStudies) {
            List<Image> images = postService.getImages(post.getPost().getId());

            ProjectStudyDto.Response response = ProjectStudyDto.Response.builder()
                    .post(post.getPost())
                    .projectStudy(post)
                    .images(images)
                    .isLiked(postService.isLikedByCurrentUser(currentUserId, post.getPost())).build();

            responseList.add(response);
        }
        return new PageImpl<>(responseList);
    }

    private PageImpl<ProjectStudyDto.Response> getOpenedPageImpl(Page<ProjectStudyDto.Response> projectStudies) {
        return new PageImpl<>(projectStudies.stream()
                .filter(ProjectStudyDto.Response::isOpenStatus)
                .collect(Collectors.toList()));
    }
}
