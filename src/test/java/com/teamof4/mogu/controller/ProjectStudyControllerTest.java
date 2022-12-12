package com.teamof4.mogu.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.teamof4.mogu.dto.PostDto;
import com.teamof4.mogu.dto.PostDto.SaveRequest;
import com.teamof4.mogu.dto.PostDto.UpdateRequest;
import com.teamof4.mogu.dto.ProjectStudyDto.Request;
import com.teamof4.mogu.entity.*;
import com.teamof4.mogu.exception.user.UserNotLoginedException;
import com.teamof4.mogu.security.TokenProvider;
import com.teamof4.mogu.service.ProjectStudyService;
import com.teamof4.mogu.util.WithMockCustomUser;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static com.teamof4.mogu.constants.SortStatus.*;
import static com.teamof4.mogu.constants.SortStatus.ALL;
import static com.teamof4.mogu.dto.ProjectStudyDto.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@ExtendWith(SpringExtension.class)
@WebMvcTest(ProjectStudyController.class)
@MockBean(JpaMetamodelMappingContext.class)
@AutoConfigureMockMvc(addFilters = false)
public class ProjectStudyControllerTest {

    @Autowired
    private MockMvc mockMvc;

    ObjectMapper objectMapper = new ObjectMapper();

    @MockBean
    ProjectStudyService projectStudyService;

    @MockBean
    TokenProvider tokenProvider;

    Page<Response> posts = new PageImpl<>(Collections.emptyList());
    Image image;
    User user;
    Category category;
    Post post;
    ProjectStudy projectStudy;
    Skill skill;
    Pageable pageable;


    @BeforeEach
    void beforeEach() {
        image = Image.builder()
                .id(1L)
                .imageUrl("https://frost0807.s3.ap-northeast-2.amazonaws.com/static/1669398559870dogey.jpg").build();

        user = User.builder()
                .id(1L)
                .name("김모구")
                .nickname("모구")
                .email("kimmogu@mogu.com")
                .password("mogu1234!")
                .phone("01034778654")
                .image(image)
                .build();

        category = new Category(1L, "팀 프로젝트");

        post = Post.builder()
                .id(1L)
                .category(category)
                .user(user)
                .title("제목입니다.")
                .content("내용입니다.")
                .isDeleted(false)
                .likes(Collections.emptyList())
                .build();

        Skill skill = new Skill(1L, "Java");

        List<PostSkill> skills = new ArrayList<>();

        PostSkill postSkill = PostSkill.builder()
                .id(1L)
                .skill(skill).build();
        skills.add(postSkill);

        projectStudy = ProjectStudy.builder()
                .id(1L)
                .image(image)
                .post(post)
                .contactMethod("카카오톡 오픈채팅")
                .contactInfo("link")
                .preferredMethod("온라인")
                .region("해당없음")
                .memberCount(6)
                .period("6개월")
                .openStatus(true)
                .postSkills(skills)
                .startAt(LocalDate.now())
                .build();

        pageable = PageRequest.of(0, 10);
    }

    @Nested
    @DisplayName("스터디/프로젝트 검색")
    class searchPostsTest {

        @Test
        @WithMockCustomUser
        @DisplayName("[성공] 모집 여부 상관없을 때")
        void searchAllPostList_Success() throws Exception {

            given(projectStudyService
                    .getSearchedList(category.getId(), "java", user.getId(), pageable, ALL)
                    ).willReturn(posts);

            mockMvc.perform(
                            get("/projectstudy/search/all/4")
                                    .param("keyword", "java"))
                    .andDo(print())
                    .andExpect(status().isOk());

            verify(projectStudyService).getSearchedList(anyLong(), anyString(), anyLong(), any(Pageable.class), eq(ALL));

        }

        @Test
        @WithMockCustomUser
        @DisplayName("[성공] 모집 중일 때")
        void searchOpenedPostList_Success() throws Exception {

            given(projectStudyService
                    .getSearchedList(category.getId(), "java", user.getId(), pageable, OPENED)
                    ).willReturn(posts);

            mockMvc.perform(
                            get("/projectstudy/search/opened/4")
                                    .param("keyword", "java"))
                    .andDo(print())
                    .andExpect(status().isOk());

            verify(projectStudyService)
                    .getSearchedList(anyLong(), anyString(), anyLong(), any(Pageable.class), eq(OPENED));

        }
    }

    @Nested
    @DisplayName("스터디프로젝트 게시글 조회")
    class selectPostsTest {

        @Test
        @WithMockCustomUser
        @DisplayName("[성공] 커뮤니티 게시글 전체 조회(모집 여부 X)")
        void selectAllPostList_Success() throws Exception {

            given(projectStudyService.getProjectStudyList(category.getId(), pageable, user.getId(), ALL))
                    .willReturn(posts);

            mockMvc.perform(
                            get("/projectstudy/list/all/4"))
                    .andDo(print())
                    .andExpect(status().isOk());

            verify(projectStudyService).getProjectStudyList(anyLong(), any(Pageable.class), anyLong(), eq(ALL));
        }

        @Test
        @WithMockCustomUser
        @DisplayName("[성공] 커뮤니티 게시글 전체 조회(모집 중)")
        void selectOpenedPostList_Success() throws Exception {

            given(projectStudyService.getProjectStudyList(category.getId(), pageable, user.getId(), OPENED))
                    .willReturn(posts);

            mockMvc.perform(
                            get("/projectstudy/list/opened/4"))
                    .andDo(print())
                    .andExpect(status().isOk());

            verify(projectStudyService).getProjectStudyList(anyLong(), any(Pageable.class), anyLong(), eq(OPENED));
        }

        @Test
        @WithMockCustomUser
        @DisplayName("[성공] 커뮤니티 게시글 좋아요 순 전체 조회(모집 여부 X)")
        void selectAllLikeDescPostList_Success() throws Exception {

            given(projectStudyService.getProjectStudyLikesList(category.getId(), pageable, user.getId(), ALL))
                    .willReturn(posts);

            mockMvc.perform(
                            get("/projectstudy/list/all/likes/4"))
                    .andDo(print())
                    .andExpect(status().isOk());

            verify(projectStudyService).getProjectStudyLikesList(anyLong(), any(Pageable.class), anyLong(), eq(ALL));
        }

        @Test
        @WithMockCustomUser
        @DisplayName("[성공] 커뮤니티 게시글 좋아요 순 전체 조회(모집 중)")
        void selectOpenedLikeDescPostList_Success() throws Exception {

            given(projectStudyService.getProjectStudyLikesList(category.getId(), pageable, user.getId(), OPENED))
                    .willReturn(posts);

            mockMvc.perform(
                            get("/projectstudy/list/opened/likes/4"))
                    .andDo(print())
                    .andExpect(status().isOk());

            verify(projectStudyService).getProjectStudyLikesList(anyLong(), any(Pageable.class), anyLong(), eq(OPENED));
        }

        @Test
        @WithMockCustomUser
        @DisplayName("[성공] 프로젝트스터디 게시글 상세 조회")
        void selectPost() throws Exception {

            Response response = Response.builder()
                            .post(post)
                            .projectStudy(projectStudy)
                            .isLiked(true).build();

            given(projectStudyService.getProjectStudyDetails(post.getId(), user.getId()))
                    .willReturn(response);

            mockMvc.perform(
                            get("/projectstudy/post/1"))
                    .andDo(print())
                    .andExpect(status().isOk());

            verify(projectStudyService).getProjectStudyDetails(anyLong(), anyLong());
        }
    }

     @Nested
     @DisplayName("프로젝트/스터디 게시글 등록")
     class savePostTest {

        @Test
        @WithMockCustomUser
        @DisplayName("[성공] 로그인 한 유저가 필수 데이터로 요청할 때")
        void saveProjectStudy_Success() throws Exception {

            given(projectStudyService.saveProjectStudy(getPostSaveRequestDto(), getProjectStudyDto(), getMultipartFile(), user.getId()))
                    .willReturn(1L);

            mockMvc.perform(
                            multipart("/projectstudy/create")
                                    .file(getMultipartFile())
                                    .file(getPostSaveRequestFile(getPostSaveRequestDto()))
                                    .file(getProjectStudyFile()))
                    .andDo(print())
                    .andExpect(status().isOk());

            verify(projectStudyService).saveProjectStudy(
                    any(SaveRequest.class), any(Request.class), any(MultipartFile.class), anyLong());

        }

        @Test
        @WithMockCustomUser
        @DisplayName("[성공] 이미지가 없을 때")
        void saveProjectStudyWithoutImage_Success() throws Exception {

            given(projectStudyService.saveProjectStudy(getPostSaveRequestDto(), getProjectStudyDto(), null, user.getId()))
                    .willReturn(1L);

            mockMvc.perform(
                            multipart("/projectstudy/create")
                                    .file(getPostSaveRequestFile(getPostSaveRequestDto()))
                                    .file(getProjectStudyFile()))
                    .andDo(print())
                    .andExpect(status().isOk());

            verify(projectStudyService).saveProjectStudy(
                    any(SaveRequest.class), any(Request.class), eq(null), anyLong());
        }

        @Test
        @DisplayName("[실패] 로그인한 유저가 없을 때")
        void saveProjectStudyWithoutLogin_Fail() throws Exception {

            given(projectStudyService.saveProjectStudy(getPostSaveRequestDto(), getProjectStudyDto(), getMultipartFile(), null))
                    .willThrow(UserNotLoginedException.class);

            mockMvc.perform(
                            multipart("/projectstudy/create")
                                    .file(getMultipartFile())
                                    .file(getPostSaveRequestFile(getPostSaveRequestDto()))
                                    .file(getProjectStudyFile()))
                    .andDo(print())
                    .andExpect(status().is4xxClientError());

            verify(projectStudyService, never()).saveProjectStudy(
                    any(SaveRequest.class), any(Request.class), any(MultipartFile.class), eq(null));

        }

        @Test
        @WithMockCustomUser
        @DisplayName("[실패] 필수 데이터가 없을 때")
        void saveProjectStudyWithoutField_Fail() throws Exception {

            PostDto.SaveRequest request = SaveRequest.builder()
                    .categoryId(1L)
                    .title("")
                    .content("내용입니다.").build();

            given(projectStudyService.saveProjectStudy(getPostSaveRequestDto(), getProjectStudyDto(), getMultipartFile(), user.getId()))
                    .willThrow(NullPointerException.class);

            mockMvc.perform(
                            multipart("/projectstudy/create")
                                    .file(getMultipartFile())
                                    .file(getPostSaveRequestFile(request))
                                    .file(getProjectStudyFile()))
                    .andDo(print())
                    .andExpect(status().isBadRequest());

            verify(projectStudyService, never()).saveProjectStudy(
                    any(SaveRequest.class), any(Request.class), any(MultipartFile.class), anyLong());

        }
    }

    @Nested
    @DisplayName("프로젝트/스터디 게시글 수정")
    class updatePostTest {

        @Test
        @WithMockCustomUser
        @DisplayName("[성공] 로그인 한 유저가 필수 데이터로 요청할 때")
        void updateProjectStudy_Success() throws Exception {

            given(projectStudyService.updateProjectStudy(post.getId(), getPostUpdateRequestDto(), getProjectStudyDto(), getMultipartFile(), user.getId()))
                    .willReturn(1L);

            mockMvc.perform(
                            multipart("/projectstudy/update/1")
                                    .file(getMultipartFile())
                                    .file(getPostUpdateRequestFile(getPostUpdateRequestDto()))
                                    .file(getProjectStudyFile()))
                    .andDo(print())
                    .andExpect(status().isOk());

            verify(projectStudyService).updateProjectStudy(anyLong(), any(UpdateRequest.class), any(Request.class), any(MultipartFile.class), anyLong());

        }

        @Test
        @WithMockCustomUser
        @DisplayName("[성공] 이미지가 없을 때")
        void updateProjectStudyWithoutImages_Success() throws Exception {

            given(projectStudyService.updateProjectStudy(post.getId(), getPostUpdateRequestDto(), getProjectStudyDto(), null, user.getId()))
                    .willReturn(1L);

            mockMvc.perform(
                            multipart("/projectstudy/update/1")
                                    .file(getPostUpdateRequestFile(getPostUpdateRequestDto()))
                                    .file(getProjectStudyFile()))
                    .andDo(print())
                    .andExpect(status().isOk());

            verify(projectStudyService).updateProjectStudy(anyLong(), any(UpdateRequest.class), any(Request.class), eq(null), anyLong());


        }

        @Test
        @WithMockCustomUser
        @DisplayName("[실패] 필수 데이터가 없을 때")
        void updateProjectStudyWithImages_Fail() throws Exception {

            UpdateRequest updateDto = UpdateRequest.builder()
                    .title("")
                    .content("업데이트 내용입니다.").build();

            given(projectStudyService.updateProjectStudy(post.getId(), updateDto, getProjectStudyDto(), null, user.getId()))
                    .willReturn(1L);

            mockMvc.perform(
                            multipart("/projectstudy/update/1")
                                    .file(getPostUpdateRequestFile(updateDto))
                                    .file(getProjectStudyFile()))
                    .andDo(print())
                    .andExpect(status().isBadRequest());

            verify(projectStudyService, never()).updateProjectStudy(
                    anyLong(), any(UpdateRequest.class), any(Request.class), eq(null), anyLong());

        }
    }

    private Request getProjectStudyDto() {
        List<Skill> skillList = new ArrayList<>();
        skillList.add(skill);

        return new Request(
                "온라인",
                "해당없음",
                "6개월",
                6,
                skillList,
                "카카오톡 오픈 채팅",
                "link",
                LocalDate.now(), true
        );
    }

    private MockMultipartFile getProjectStudyFile() throws Exception {
        return new MockMultipartFile(
                "projectStudyDto", "projectStudyDto", "application/json",
                objectMapper.registerModule(new JavaTimeModule())
                            .writeValueAsString(getProjectStudyDto()).getBytes());

    }

    private MockMultipartFile getMultipartFile() {
        return new MockMultipartFile(
                "multipartFile", "filename.jpeg",
                "image/jpeg", "multipartFile".getBytes());
    }

    private PostDto.SaveRequest getPostSaveRequestDto() {
        return SaveRequest.builder()
                .categoryId(1L)
                .title("제목입니다.")
                .content("내용입니다.").build();
    }

    private PostDto.UpdateRequest getPostUpdateRequestDto() {
        return UpdateRequest.builder()
                .title("업데이트 제목입니다.")
                .content("업데이트 내용입니다.").build();
    }

    private MockMultipartFile getPostSaveRequestFile(SaveRequest dto) throws Exception {
        return new MockMultipartFile(
                "postDto", "postDto", "application/json",
                objectMapper.writeValueAsString(dto).getBytes());

    }

    private MockMultipartFile getPostUpdateRequestFile(UpdateRequest dto) throws Exception {
        return new MockMultipartFile(
                "postDto", "postDto", "application/json",
                objectMapper.writeValueAsString(dto).getBytes());

    }
}
