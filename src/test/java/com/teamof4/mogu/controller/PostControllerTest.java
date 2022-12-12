package com.teamof4.mogu.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.teamof4.mogu.constants.SortStatus;
import com.teamof4.mogu.dto.LikeDto;
import com.teamof4.mogu.dto.PostDto;
import com.teamof4.mogu.dto.ReplyDto;
import com.teamof4.mogu.entity.Category;
import com.teamof4.mogu.entity.Image;
import com.teamof4.mogu.entity.Post;
import com.teamof4.mogu.entity.User;
import com.teamof4.mogu.exception.user.UserNotLoginedException;
import com.teamof4.mogu.security.TokenProvider;
import com.teamof4.mogu.service.PostService;
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
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import java.nio.charset.StandardCharsets;
import java.util.Collections;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@WebMvcTest(PostController.class)
@MockBean(JpaMetamodelMappingContext.class)
@AutoConfigureMockMvc(addFilters = false)
class PostControllerTest {

    @Autowired
    private MockMvc mockMvc;

    ObjectMapper objectMapper = new ObjectMapper();

    @MockBean
    PostService postService;

    @MockBean
    TokenProvider tokenProvider;

    Image testImage;

    User testUser;

    Category testCategory;

    Post testPost;


    @BeforeEach
    void beforeEach() {
        testImage = Image.builder()
                .id(1L)
                .imageUrl("https://frost0807.s3.ap-northeast-2.amazonaws.com/static/1669398559870dogey.jpg").build();

        testUser = User.builder()
                .id(1L)
                .name("김모구")
                .nickname("모구")
                .email("kimmogu@mogu.com")
                .password("mogu1234!")
                .phone("01034778654")
                .image(testImage)
                .build();

        testCategory = new Category(1L, "팀 프로젝트");

        testPost = Post.builder()
                .id(1L)
                .category(testCategory)
                .user(testUser)
                .title("제목입니다.")
                .content("내용입니다.")
                .isDeleted(false)
                .likes(Collections.emptyList())
                .build();
    }

    @Nested
    @DisplayName("커뮤니티 게시글 조회 기능")
    class selectPostsTest {

        @Test
        @WithMockCustomUser
        @DisplayName("[성공] 커뮤니티 게시글 전체 조회")
        void selectList() throws Exception {

            Pageable pageable = PageRequest.of(0, 10);

            Page<PostDto.Response> posts = new PageImpl<>(Collections.emptyList());

            given(postService.getPostList(testCategory.getId(), pageable, testUser.getId(), SortStatus.DEFAULT))
                    .willReturn(posts);

            mockMvc.perform(
                    get("/posts/list/1"))
                    .andDo(print())
                    .andExpect(status().isOk());

            verify(postService).getPostList(anyLong(), any(Pageable.class), anyLong(), eq(SortStatus.DEFAULT));
        }

        @Test
        @WithMockCustomUser
        @DisplayName("[성공] 커뮤니티 게시글 전체 조회(좋아요 순)")
        void selectPostsLikeDescList() throws Exception {

            Pageable pageable = PageRequest.of(0, 10);

            Page<PostDto.Response> posts = new PageImpl<>(Collections.emptyList());

            given(postService.getPostList(testCategory.getId(), pageable, testUser.getId(), SortStatus.LIKES))
                    .willReturn(posts);

            mockMvc.perform(
                            get("/posts/list/likes/1"))
                    .andDo(print())
                    .andExpect(status().isOk());

            verify(postService).getPostList(anyLong(), any(Pageable.class), anyLong(), eq(SortStatus.LIKES));
        }

        @Test
        @WithMockCustomUser
        @DisplayName("[성공] 커뮤니티 게시글 상세 조회")
        void selectPostDetails() throws Exception {

            given(postService.getPostDetails(testPost.getId(), testUser.getId()))
                    .willReturn(getResponse());

            mockMvc.perform(
                    get("/posts/post/1"))
                    .andDo(print())
                    .andExpect(status().isOk());

            verify(postService).getPostDetails(anyLong(), anyLong());
        }
    }

    @Nested
    @DisplayName("커뮤니티 게시글 등록 기능")
    class savePostTest {

        @Test
        @WithMockCustomUser
        @DisplayName("[성공] 로그인 한 유저가 필수 데이터로 요청했을 때")
        void savePost_Success() throws Exception {

            given(postService.savePost(getSaveRequest(), testUser.getId()))
                    .willReturn(1L);

            mockMvc.perform(
                            post("/posts/create")
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .characterEncoding(StandardCharsets.UTF_8)
                                    .content(objectMapper.writeValueAsString(getSaveRequest())))
                    .andDo(print())
                    .andExpect(status().isOk());

            verify(postService).savePost(any(PostDto.SaveRequest.class), anyLong());
        }

        @Test
        @DisplayName("[실패] 로그인을 하지 않은 유저일 때")
        void savePostWithoutLogin_Fail() throws Exception {

            given(postService.savePost(getSaveRequest(), testUser.getId()))
                    .willThrow(UserNotLoginedException.class);

            mockMvc.perform(
                            post("/posts/create")
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .characterEncoding(StandardCharsets.UTF_8)
                                    .content(objectMapper.writeValueAsString(getSaveRequest())))
                    .andDo(print())
                    .andExpect(status().is4xxClientError());

            verify(postService, never()).savePost(any(PostDto.SaveRequest.class), anyLong());
        }

        @Test
        @WithMockCustomUser
        @DisplayName("[실패] 필수 데이터가 없을 때")
        void savePostWithoutField_Fail() throws Exception {

            PostDto.SaveRequest dto = PostDto.SaveRequest.builder()
                    .categoryId(1L)
                    .content("내용입니다.").build();

            given(postService.savePost(dto, testUser.getId()))
                    .willThrow(NullPointerException.class);

            mockMvc.perform(
                            post("/posts/create")
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .characterEncoding(StandardCharsets.UTF_8)
                                    .content(objectMapper.writeValueAsString(dto)))
                    .andDo(print())
                    .andExpect(status().isBadRequest());

            verify(postService, never()).savePost(any(PostDto.SaveRequest.class), anyLong());
        }

    }

    @Nested
    @DisplayName("커뮤니티 게시글 수정 기능")
    class updatePostTest {

        @Test
        @WithMockCustomUser
        @DisplayName("[성공] 로그인 한 유저가 필수 데이터로 요청했을 때")
        void updatePost_Success() throws Exception {

            given(postService.updatePost(testPost.getId(), getUpdateRequest(), testUser.getId()))
                    .willReturn(1L);

            mockMvc.perform(
                            post("/posts/update/1")
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .characterEncoding(StandardCharsets.UTF_8)
                                    .content(objectMapper.writeValueAsString(getUpdateRequest())))
                    .andDo(print())
                    .andExpect(status().isOk());

            verify(postService).updatePost(anyLong(), any(PostDto.UpdateRequest.class), anyLong());
        }

        @Test
        @WithMockCustomUser
        @DisplayName("[실패] 필수 데이터가 없을 때")
        void updatePostWithoutLogin_Fail() throws Exception {

            PostDto.UpdateRequest dto = PostDto.UpdateRequest.builder()
                    .content("내용입니다.").build();

            given(postService.updatePost(testPost.getId(), dto, testUser.getId()))
                    .willThrow(NullPointerException.class);

            mockMvc.perform(
                            post("/posts/update/1")
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .characterEncoding(StandardCharsets.UTF_8)
                                    .content(objectMapper.writeValueAsString(dto)))
                    .andDo(print())
                    .andExpect(status().isBadRequest());

            verify(postService, never()).updatePost(anyLong(), any(PostDto.UpdateRequest.class), anyLong());
        }
    }

    @Test
    @WithMockCustomUser
    @DisplayName("[성공] 커뮤니티 게시글 삭제")
    void deletePost() throws Exception {

        willDoNothing().given(postService).deletePost(testPost.getId(), testUser.getId());

        mockMvc.perform(
                        post("/posts/delete/1"))
                .andDo(print())
                .andExpect(status().isOk());

        verify(postService).deletePost(anyLong(), anyLong());
    }

    @Nested
    @DisplayName("좋아요 기능")
    class likeTest {

        @Test
        @WithMockCustomUser
        @DisplayName("[성공] 로그인 한 유저가 요청했을 때")
        void hitLike_Success() throws Exception {

            given(postService.likeProcess(testPost.getId(), testUser.getId()))
                    .willReturn(getLikes());

            mockMvc.perform(
                    post("/posts/like/1"))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.likeStatus").value(true))
                    .andExpect(jsonPath("$.count").value(1));

            verify(postService).likeProcess(anyLong(), anyLong());
        }

        @Test
        @DisplayName("[실패] 로그인을 하지 않은 유저일 때")
        void hitLikeWithoutLogin_Fail() throws Exception {

            given(postService.likeProcess(testPost.getId(), null))
                    .willThrow(UserNotLoginedException.class);

            mockMvc.perform(
                            post("/posts/like/1"))
                    .andDo(print())
                    .andExpect(status().is4xxClientError());

            verify(postService, never()).likeProcess(anyLong(), anyLong());

        }
    }

    @Nested
    @DisplayName("[Super] 댓글 등록 기능")
    class SuperReplyTest {

        @Test
        @WithMockCustomUser
        @DisplayName("[성공] 로그인 한 유저가 필수 데이터로 요청했을 때")
        void saveSuperReply_Success() throws Exception {

            given(postService.saveSuperReply(testUser.getId(), getSuperRequest()))
                    .willReturn(1L);

            mockMvc.perform(
                            post("/posts/reply/create/super")
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .characterEncoding(StandardCharsets.UTF_8)
                                    .content(objectMapper.writeValueAsString(getSuperRequest())))
                    .andDo(print())
                    .andExpect(status().isOk());

            verify(postService).saveSuperReply(anyLong(), any(ReplyDto.SuperRequest.class));
        }

        @Test
        @DisplayName("[실패] 로그인 하지 않았을 때")
        void saveSuperReplyWithoutLogin_Fail() throws Exception {

            given(postService.saveSuperReply(null, getSuperRequest()))
                    .willThrow(UserNotLoginedException.class);

            mockMvc.perform(
                            post("/posts/reply/create/super")
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .characterEncoding(StandardCharsets.UTF_8)
                                    .content(objectMapper.writeValueAsString(getSuperRequest())))
                    .andDo(print())
                    .andExpect(status().is4xxClientError());

            verify(postService, never()).saveSuperReply(anyLong(), any(ReplyDto.SuperRequest.class));
        }

        @Test
        @WithMockCustomUser
        @DisplayName("[실패] 필수 데이터가 없을 때")
        void saveSuperReplyWithoutField_Fail() throws Exception {

            ReplyDto.SuperRequest dto = ReplyDto.SuperRequest.builder()
                    .postId(1L).content("").build();

            given(postService.saveSuperReply(testUser.getId(), dto))
                    .willThrow(NullPointerException.class);

            mockMvc.perform(
                            post("/posts/reply/create/super")
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .characterEncoding(StandardCharsets.UTF_8)
                                    .content(objectMapper.writeValueAsString(dto)))
                    .andDo(print())
                    .andExpect(status().isBadRequest());

            verify(postService, never()).saveSuperReply(anyLong(), any(ReplyDto.SuperRequest.class));
        }
    }

    @Nested
    @DisplayName("[Sub] 댓글 등록 기능")
    class SubReplyTest {

        @Test
        @WithMockCustomUser
        @DisplayName("[성공] 로그인 한 유저가 필수 데이터로 요청했을 때")
        void saveSubReply_Success() throws Exception {

            given(postService.saveSubReply(testUser.getId(), getReplyRequest()))
                    .willReturn(1L);

            mockMvc.perform(
                            post("/posts/reply/create/sub")
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .characterEncoding(StandardCharsets.UTF_8)
                                    .content(objectMapper.writeValueAsString(getReplyRequest())))
                    .andDo(print())
                    .andExpect(status().isOk());

            verify(postService).saveSubReply(anyLong(), any(ReplyDto.Request.class));
        }

        @Test
        @DisplayName("[실패] 로그인 하지 않았을 때")
        void saveSubReplyWithoutLogin_Fail() throws Exception {

            given(postService.saveSubReply(null, getReplyRequest()))
                    .willThrow(UserNotLoginedException.class);

            mockMvc.perform(
                            post("/posts/reply/create/sub")
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .characterEncoding(StandardCharsets.UTF_8)
                                    .content(objectMapper.writeValueAsString(getReplyRequest())))
                    .andDo(print())
                    .andExpect(status().is4xxClientError());

            verify(postService, never()).saveSubReply(anyLong(), any(ReplyDto.Request.class));
        }

        @Test
        @WithMockCustomUser
        @DisplayName("[실패] 필수 데이터가 없을 때")
        void saveSubReplyWithoutField_Fail() throws Exception {

            ReplyDto.Request dto = ReplyDto.Request.builder()
                    .replyId(1L).content("").build();

            given(postService.saveSubReply(testUser.getId(), dto))
                    .willThrow(NullPointerException.class);

            mockMvc.perform(
                            post("/posts/reply/create/sub")
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .characterEncoding(StandardCharsets.UTF_8)
                                    .content(objectMapper.writeValueAsString(dto)))
                    .andDo(print())
                    .andExpect(status().isBadRequest());

            verify(postService, never()).saveSubReply(anyLong(), any(ReplyDto.Request.class));
        }
    }

    @Test
    @WithMockCustomUser
    @DisplayName("댓글 수정 기능")
    void updateReplyTest() throws Exception {

        given(postService.updateReply(getReplyRequest(), testUser.getId()))
                .willReturn(1L);

        mockMvc.perform(
                post("/posts/reply/update")
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .content(objectMapper.writeValueAsString(getReplyRequest())))
                .andDo(print())
                .andExpect(status().isOk());

        verify(postService).updateReply(any(ReplyDto.Request.class), anyLong());
    }

    @Test
    @WithMockCustomUser
    @DisplayName("댓글 삭제 기능")
    void deleteReply() throws Exception {

        willDoNothing().given(postService).deleteReply(1L, testUser.getId());

        mockMvc.perform(
                post("/posts/reply/delete/1"))
                .andDo(print())
                .andExpect(status().isOk());

        verify(postService).deleteReply(anyLong(), anyLong());
    }

    private PostDto.SaveRequest getSaveRequest() {
        return PostDto.SaveRequest.builder()
                .categoryId(1L)
                .title("제목입니다.")
                .content("내용입니다.").build();
    }

    private PostDto.UpdateRequest getUpdateRequest() {
        return PostDto.UpdateRequest.builder()
                .title("제목입니다.")
                .content("내용입니다.").build();
    }

    private PostDto.Response getResponse() {
        return PostDto.Response.builder()
                .post(testPost)
                .replies(Collections.emptyList())
                .isLiked(true).build();
    }

    private LikeDto getLikes() {
        return LikeDto.builder()
                .likeStatus(true)
                .count(1).build();
    }

    private ReplyDto.SuperRequest getSuperRequest() {
        return ReplyDto.SuperRequest.builder()
                .postId(testPost.getId())
                .content("댓글 내용입니다.").build();
    }

    private ReplyDto.Request getReplyRequest() {
        return ReplyDto.Request.builder()
                .replyId(1L)
                .content("답댓글 내용입니다.").build();
    }
}
