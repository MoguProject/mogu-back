package com.teamof4.mogu.controller;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.teamof4.mogu.constants.SortStatus;
import com.teamof4.mogu.dto.PostDto.SaveRequest;
import com.teamof4.mogu.dto.ProjectStudyDto;
import com.teamof4.mogu.entity.Skill;
import com.teamof4.mogu.exception.user.UserNotLoginedException;
import com.teamof4.mogu.security.TokenProvider;
import com.teamof4.mogu.security.WithMockCustomUser;
import com.teamof4.mogu.service.PostService;
import com.teamof4.mogu.service.ProjectStudyService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static com.teamof4.mogu.constants.SortStatus.*;
import static com.teamof4.mogu.dto.PostDto.UpdateRequest;
import static org.mockito.BDDMockito.*;
import static org.mockito.Mockito.doReturn;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@WebMvcTest(ProjectStudyController.class)
@MockBean(JpaMetamodelMappingContext.class)
@AutoConfigureMockMvc(addFilters = false)
class ProjectStudyControllerTest {

    @Autowired
    private MockMvc mockMvc;

    ObjectMapper objectMapper = new ObjectMapper();

    @MockBean
    ProjectStudyService projectStudyService;

    @MockBean
    PostService postService;

    @MockBean
    TokenProvider tokenProvider;


    @Nested
    @DisplayName("스터디/프로젝트 검색")
    class searchPostsTest {

        @Test
        @WithMockCustomUser
        @DisplayName("[성공] 모집 여부 상관없을 때")
        void searchAllPostList_Success() throws Exception {

            Pageable pageable = PageRequest.of(0, 10);

            Page<ProjectStudyDto.Response> posts = projectStudyService.getSearchedList(1L, "검색어", 1L, pageable, ALL);

            doReturn(posts).when(projectStudyService).getSearchedList(anyLong(), anyString(), anyLong(), eq(pageable), any(SortStatus.class));

            mockMvc.perform(
                            get("/projectstudy/search/all/4")
                                .param("page", "0")
                                .param("size", "10")
                                .param("keyword", "검색어"))
                    .andDo(print())
                    .andExpect(status().isOk());

        }

        @Test
        @WithMockCustomUser
        @DisplayName("[성공] 모집 중일 때")
        void searchOpenedPostList_Success() throws Exception {

            Pageable pageable = PageRequest.of(0, 10);

            Page<ProjectStudyDto.Response> posts = projectStudyService.getSearchedList(1L, "검색어", 1L, pageable, OPENED);

            doReturn(posts).when(projectStudyService).getSearchedList(anyLong(), anyString(), anyLong(), eq(pageable), any(SortStatus.class));

            mockMvc.perform(
                            get("/projectstudy/search/opened/4")
                                    .param("page", "0")
                                    .param("size", "10")
                                    .param("keyword", "검색어"))
                    .andDo(print())
                    .andExpect(status().isOk());

        }
    }


    @Nested
    @DisplayName("스터디/프로젝트 게시글 조회")
    class selectPostsTest {

        @Test
        @DisplayName("[성공] 커뮤니티 게시글 전체 조회(모집 여부 X)")
        void selectAllPostList_Success() throws Exception {

            Pageable pageable = PageRequest.of(0, 10);

            Page<ProjectStudyDto.Response> posts = projectStudyService.getProjectStudyList(1L, pageable, 1L, ALL);

            doReturn(posts).when(projectStudyService).getProjectStudyList(anyLong(), eq(pageable), anyLong(), any(SortStatus.class));

            mockMvc.perform(
                            get("/projectstudy/list/all/4")
                                .param("page", "0")
                                .param("size", "10"))
                    .andDo(print())
                    .andExpect(status().isOk());
        }

        @Test
        @WithMockCustomUser
        @DisplayName("[성공] 커뮤니티 게시글 전체 조회(모집 중)")
        void selectOpenedPostList_Success() throws Exception {

            Pageable pageable = PageRequest.of(0, 10);

            Page<ProjectStudyDto.Response> posts = projectStudyService.getProjectStudyList(1L, pageable, 1L, OPENED);

            doReturn(posts).when(projectStudyService).getProjectStudyList(anyLong(), eq(pageable), anyLong(), any(SortStatus.class));

            mockMvc.perform(
                            get("/projectstudy/list/opened/4")
                                    .param("page", "0")
                                    .param("size", "10"))
                    .andDo(print())
                    .andExpect(status().isOk());
        }

        @Test
        @WithMockCustomUser
        @DisplayName("[성공] 커뮤니티 게시글 전체 조회(좋아요 순)")
        void selectLikeDescPostList_Success() throws Exception {

            Pageable pageable = PageRequest.of(0, 10);

            Page<ProjectStudyDto.Response> posts = projectStudyService.getProjectStudyList(1L, pageable, 1L, LIKES);

            doReturn(posts).when(projectStudyService).getProjectStudyList(anyLong(), eq(pageable), anyLong(), any(SortStatus.class));

            mockMvc.perform(
                            get("/projectstudy/list/likes/4")
                                    .param("page", "0")
                                    .param("size", "10"))
                    .andDo(print())
                    .andExpect(status().isOk());
        }

        @Test
        @WithMockCustomUser
        @DisplayName("[성공] 프로젝트/스터디 게시글 상세 조회")
        void selectPost() throws Exception {

            ProjectStudyDto.Response response = projectStudyService.getProjectStudyDetails(anyLong(), anyLong());

            doReturn(response).when(projectStudyService).getProjectStudyDetails(anyLong(), anyLong());

            mockMvc.perform(
                    get("/projectstudy/post/1"))
                    .andDo(print())
                    .andExpect(status().isOk());
        }
    }

    @Nested
    @DisplayName("프로젝트/스터디 게시글 등록")
    class savePostTest {

        @Test
        @WithMockCustomUser
        @DisplayName("[성공] 이미지가 있을 때")
        void saveProjectStudyWithImages_Success() throws Exception {

            doReturn(1L).when(projectStudyService).saveProjectStudy(any(SaveRequest.class), any(ProjectStudyDto.Request.class), anyLong());

            mockMvc.perform(
                            multipart("/projectstudy/create")
                                    .file(getMultipartFile()).file(getMultipartFile())
                                    .file(getProjectStudyDto())
                                    .param("categoryId", "1")
                                    .param("title", "제목입니다.")
                                    .param("content", "내용입니다.")
                                    .accept(MediaType.ALL))
                    .andDo(print())
                    .andExpect(status().isOk());

        }

        @Test
        @WithMockCustomUser
        @DisplayName("[성공] 이미지가 없을 때")
        void saveProjectStudyWithoutImages_Success() throws Exception {

            doReturn(1L).when(projectStudyService).saveProjectStudy(any(SaveRequest.class), any(ProjectStudyDto.Request.class), anyLong());

            mockMvc.perform(
                            multipart("/projectstudy/create")
                                    .file(getProjectStudyDto())
                                    .param("categoryId", "1")
                                    .param("title", "제목입니다.")
                                    .param("content", "내용입니다.")
                                    .accept(MediaType.ALL))
                    .andDo(print())
                    .andExpect(status().isOk());

        }

        @Test
        @DisplayName("[실패] 로그인한 유저가 없을 때")
        void saveProjectStudyWithoutLogin_Fail() throws Exception {

            doThrow(UserNotLoginedException.class).when(projectStudyService).saveProjectStudy(any(SaveRequest.class), any(ProjectStudyDto.Request.class), anyLong());

            mockMvc.perform(
                            multipart("/projectstudy/create")
                                    .file(getMultipartFile()).file(getMultipartFile())
                                    .file(getProjectStudyDto())
                                    .param("categoryId", "1")
                                    .param("title", "제목입니다.")
                                    .param("content", "내용입니다.")
                                    .accept(MediaType.ALL))
                    .andDo(print())
                    .andExpect(status().is4xxClientError());

        }

        @Test
        @WithMockCustomUser
        @DisplayName("[실패] 필수 데이터가 없을 때")
        void saveProjectStudyWithImages_Fail() throws Exception {

            doReturn(1L).when(projectStudyService).updateProjectStudy(anyLong(), any(UpdateRequest.class), any(ProjectStudyDto.Request.class));

            mockMvc.perform(
                            multipart("/projectstudy/create")
                                    .file(getProjectStudyDto())
                                    .param("content", "내용입니다.")
                                    .accept(MediaType.ALL))
                    .andDo(print())
                    .andExpect(status().isBadRequest());
        }
    }

    @Nested
    @DisplayName("프로젝트/스터디 게시글 수정")
    class updqtePostTest {

        @Test
        @WithMockCustomUser
        @DisplayName("[성공] 이미지가 있을 때")
        void updateProjectStudyWithImages_Success() throws Exception {

            doReturn(1L).when(projectStudyService).updateProjectStudy(anyLong(), any(UpdateRequest.class), any(ProjectStudyDto.Request.class));

            mockMvc.perform(
                            multipart("/projectstudy/update/1")
                                    .file(getMultipartFile()).file(getMultipartFile())
                                    .file(getProjectStudyDto())
                                    .param("title", "업데이트 제목입니다.")
                                    .param("content", "업데이트 내용입니다.")
                                    .accept(MediaType.ALL))
                    .andDo(print())
                    .andExpect(status().isOk());

        }

        @Test
        @WithMockCustomUser
        @DisplayName("[성공] 이미지가 없을 때")
        void updateProjectStudyWithoutImages_Success() throws Exception {

            doReturn(1L).when(projectStudyService).updateProjectStudy(anyLong(), any(UpdateRequest.class), any(ProjectStudyDto.Request.class));

            mockMvc.perform(
                            multipart("/projectstudy/update/1")
                                    .file(getProjectStudyDto())
                                    .param("title", "제목입니다.")
                                    .param("content", "내용입니다.")
                                    .accept(MediaType.ALL))
                    .andDo(print())
                    .andExpect(status().isOk());

        }

        @Test
        @WithMockCustomUser
        @DisplayName("[실패] 필수 데이터가 없을 때")
        void updateProjectStudyWithImages_Fail() throws Exception {

            doReturn(1L).when(projectStudyService).updateProjectStudy(anyLong(), any(UpdateRequest.class), any(ProjectStudyDto.Request.class));

            mockMvc.perform(
                            multipart("/projectstudy/update/1")
                                    .file(getProjectStudyDto())
                                    .param("content", "내용입니다.")
                                    .accept(MediaType.ALL))
                    .andDo(print())
                    .andExpect(status().isBadRequest());
        }
    }

    private ProjectStudyDto.Request getRequest() {
        List<Skill> skills = new ArrayList<>();
        Skill skill = new Skill(1L, "Java");
        skills.add(skill);

        return new ProjectStudyDto.Request("온라인", "해당없음", "6개월 이상",
                4, skills, "카카오톡 오픈채팅", "링크",
                LocalDate.of(2022, 12, 31), true);
    }

    private MockMultipartFile getMultipartFile() {
        return new MockMultipartFile(
                "multipartFiles", "filename.jpeg",
                "image/jpeg", "multipartFiles".getBytes());

    }

    private MockMultipartFile getProjectStudyDto() throws JsonProcessingException {
        return new MockMultipartFile("projectStudyDto",
                        "projectStudyDto",
                        "application/json",
                        objectMapper.registerModule(new JavaTimeModule()).writeValueAsString(getRequest()).getBytes(StandardCharsets.UTF_8));

    }
}