package com.teamof4.mogu.service;

import com.teamof4.mogu.constants.SortStatus;
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
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static com.teamof4.mogu.constants.DefaultImageConstants.DEFAULT_POST_IMAGE_ID;
import static com.teamof4.mogu.constants.SortStatus.ALL;
import static com.teamof4.mogu.constants.SortStatus.OPENED;
import static com.teamof4.mogu.dto.PostDto.*;
import static com.teamof4.mogu.dto.ProjectStudyDto.*;

@Service
@RequiredArgsConstructor
public class ProjectStudyService {

    private final PostRepository postRepository;
    private final ProjectStudyRepository projectStudyRepository;
    private final PostSkillRepository postSkillRepository;
    private final PostService postService;
    private final ImageService imageService;


    public Page<ProjectStudyDto.Response> getSearchedList(Long categoryId, String keyword, Long currentUserId,
                                                             Pageable pageable, SortStatus status) {
        Category category = postService.getCategory(categoryId);

        Page<ProjectStudy> projectStudies =
                projectStudyRepository.findAllByTitleAndContentContainingIgnoreCase(keyword, category, pageable);

        List<ProjectStudyDto.Response> projectStudyDtoList = new ArrayList<>();

        if (status.equals(ALL)) {
            projectStudyDtoList = entityToListDto(projectStudies, currentUserId);
        } else if (status.equals(OPENED)) {
            projectStudyDtoList = getOpenedPostList(entityToListDto(projectStudies, currentUserId));
        }

        return new PageImpl<>(projectStudyDtoList, pageable, projectStudies.getTotalElements());
    }

    public Page<ProjectStudyDto.Response> getProjectStudyList(Long categoryId, Pageable pageable,
                                                              Long currentUserId, SortStatus status) {

        Category category = postService.getCategory(categoryId);
        Page<ProjectStudy> projectStudies = projectStudyRepository.findAll(category, pageable);

        List<ProjectStudyDto.Response> projectStudyDtoList = new ArrayList<>();

        switch (status) {
            case ALL:
                projectStudyDtoList = entityToListDto(projectStudies, currentUserId);
                break;
            case OPENED:
                projectStudyDtoList = getOpenedPostList(entityToListDto(projectStudies, currentUserId));
                break;
            case LIKES:
                projectStudies = projectStudyRepository.findAllLikesDesc(category, pageable);
                projectStudyDtoList = getOpenedPostList(entityToListDto(projectStudies, currentUserId));
                break;
            case DEFAULT:
                break;
        }

        return new PageImpl<>(projectStudyDtoList, pageable, projectStudies.getTotalElements());
    }

    public ProjectStudyDto.Response getProjectStudyDetails(Long postId, Long currentUserId) {
        Post post = postService.getPost(postId);

        if (!post.getUser().getId().equals(currentUserId)) {
            post.addViewCount(post.getView());
            postRepository.save(post);
        }

        ProjectStudy projectStudy = getProjectStudy(post.getId());

        List<Reply> replyList = postService.getReplies(post);

        return ProjectStudyDto.Response.builder()
                .post(post)
                .projectStudy(projectStudy)
                .replies(postService.replyConvertToDto(replyList))
                .isLiked(postService.isLikedByCurrentUser(currentUserId, post)).build();

    }

    @Transactional
    public Long saveProjectStudy(SaveRequest postDTO, Request projectStudyDTO,
                                 MultipartFile multipartFile, Long currentUserId) {

        Long postId = postService.savePost(postDTO, currentUserId);
        Post post = postService.getPost(postId);

        ProjectStudy projectStudy = projectStudyDTO.toEntity(post);

        Image image = getImage(multipartFile);
        projectStudy.setImage(image);

        projectStudyRepository.save(projectStudy);

        savePostSkill(projectStudyDTO.getSkills(), projectStudy);

        return projectStudy.getId();
    }

    @Transactional
    public Long updateProjectStudy(Long postId, UpdateRequest postDTO,
                                   Request projectStudyDTO, MultipartFile multipartFile,
                                   Long currentUserId) {

        postService.updatePost(postId, postDTO, currentUserId);

        ProjectStudy projectStudy = getProjectStudy(postId);

        if (!multipartFile.isEmpty()) {
            Image image = imageService.savePostImage(multipartFile);
            projectStudy.setImage(image);
        }

        projectStudy.updateProjectStudy(projectStudyDTO);

        projectStudyRepository.save(projectStudy);

        return projectStudy.getId();
    }

    private ProjectStudy getProjectStudy(Long postId) {
        return projectStudyRepository.findByPost(postService.getPost(postId))
                .orElseThrow(() -> new ProjectStudyNotFoundException("프로젝트/스터디 상세 정보가 존재하지 않습니다."));
    }

    private Image getImage(MultipartFile multipartFile) {
        Image image;

        if (multipartFile == null) {
            image = imageService.getImageById(DEFAULT_POST_IMAGE_ID);
        } else {
            image = imageService.savePostImage(multipartFile);
        }

        return image;
    }

    private void savePostSkill(List<Skill> skills, ProjectStudy projectStudy) {
        for (Skill skill : skills) {
            PostSkill postSkill = PostSkill.builder()
                    .projectStudy(projectStudy)
                    .skill(skill).build();
            postSkillRepository.save(postSkill);
        }
    }

    private List<ProjectStudyDto.Response> entityToListDto(Page<ProjectStudy> projectStudies, Long currentUserId) {

        return projectStudies.stream()
                .map(post -> ProjectStudyDto.Response.builder()
                        .post(post.getPost())
                        .projectStudy(post)
                        .isLiked(postService.isLikedByCurrentUser(currentUserId, post.getPost())).build())
                .collect(Collectors.toList());
    }

    private List<ProjectStudyDto.Response> getOpenedPostList(List<ProjectStudyDto.Response> projectStudies) {
        return projectStudies.stream()
                .filter(ProjectStudyDto.Response::isOpenStatus)
                .collect(Collectors.toList());
    }
}
