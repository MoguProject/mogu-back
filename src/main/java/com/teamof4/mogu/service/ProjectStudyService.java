package com.teamof4.mogu.service;

import com.amazonaws.util.CollectionUtils;
import com.teamof4.mogu.dto.PostDto;
import com.teamof4.mogu.dto.ProjectStudyDto;
import com.teamof4.mogu.entity.*;
import com.teamof4.mogu.exception.post.ProjectStudyNotFoundException;
import com.teamof4.mogu.repository.PostRepository;
import com.teamof4.mogu.repository.PostSkillRepository;
import com.teamof4.mogu.repository.ProjectStudyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProjectStudyService {

    private final PostRepository postRepository;
    private final ProjectStudyRepository projectStudyRepository;
    private final PostSkillRepository postSkillRepository;
    private final PostService postService;

    public List<ProjectStudyDto.Response> getAllProjectStudyList(Long categoryId) {
        List<ProjectStudy> projectStudies = projectStudyRepository.findAll();

        return getResponses(projectStudies).stream()
                .filter(dto -> !dto.isDeleteStatus() && dto.getCategoryId().equals(categoryId))
                .collect(Collectors.toList());
    }

    public List<ProjectStudyDto.Response> getOpenedProjectStudyList(Long categoryId) {
        List<ProjectStudy> projectStudies = projectStudyRepository.findAll();

        return getResponses(projectStudies).stream()
                .filter(dto -> !dto.isDeleteStatus() && dto.isOpenStatus())
                .filter(dto -> dto.getCategoryId().equals(categoryId))
                .collect(Collectors.toList());
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
                .images(postService.getImages(postId)).build();

    }

    @Transactional
    public Long saveProjectStudy(PostDto.SaveRequest postDTO, ProjectStudyDto.Request projectStudyDTO) {

        Long postId = postService.savePost(postDTO);
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

    private List<ProjectStudyDto.Response> getResponses(List<ProjectStudy> projectStudies) {
        List<ProjectStudyDto.Response> responseList = new ArrayList<>();

        for (ProjectStudy post : projectStudies) {
            List<Image> images = postService.getImages(post.getPost().getId());

            ProjectStudyDto.Response response = ProjectStudyDto.Response.builder()
                    .post(post.getPost())
                    .projectStudy(post)
                    .images(images).build();

            responseList.add(response);
        }
        return responseList.stream()
                .sorted(Comparator.comparing(ProjectStudyDto.Response::getCreateAt).reversed())
                .collect(Collectors.toList());
    }
}
