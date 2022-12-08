package com.teamof4.mogu.controller;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.teamof4.mogu.constants.SortStatus;
import com.teamof4.mogu.dto.LikeDto;
import com.teamof4.mogu.dto.PostDto;
import com.teamof4.mogu.dto.PostDto.SaveRequest;
import com.teamof4.mogu.dto.ReplyDto.SuperRequest;
import com.teamof4.mogu.exception.user.UserNotLoginedException;
import com.teamof4.mogu.security.TokenProvider;
import com.teamof4.mogu.security.WithMockCustomUser;
import com.teamof4.mogu.service.PostService;
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


import static com.teamof4.mogu.constants.SortStatus.DEFAULT;
import static com.teamof4.mogu.constants.SortStatus.LIKES;
import static com.teamof4.mogu.dto.PostDto.*;
import static com.teamof4.mogu.dto.PostDto.Response;
import static com.teamof4.mogu.dto.ReplyDto.*;
import static org.mockito.BDDMockito.*;
import static org.mockito.Mockito.doReturn;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
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


    @Nested
    @DisplayName("커뮤니티 게시글 조회")
    class selectPostsTest {

        @Test
        @WithMockCustomUser
        @DisplayName("[성공] 커뮤니티 게시글 전체 조회")
        void selectList() throws Exception {

            Pageable pageable = PageRequest.of(0, 10);

            Page<Response> posts = postService.getPostList(1L, pageable, 1L, DEFAULT);

            doReturn(posts).when(postService).getPostList(anyLong(), eq(pageable), anyLong(), any(SortStatus.class));

            mockMvc.perform(
                            get("/posts/list/1")
                                .param("page", "0")
                                .param("size", "10"))
                    .andDo(print())
                    .andExpect(status().isOk());
        }

        @Test
        @WithMockCustomUser
        @DisplayName("[성공] 커뮤니티 게시글 전체 조회(좋아요 순)")
        void selectLikeDescPostList() throws Exception {

            Pageable pageable = PageRequest.of(0, 10);

            Page<Response> posts = postService.getPostList(1L, pageable, 1L, LIKES);

            doReturn(posts).when(postService).getPostList(anyLong(), eq(pageable), anyLong(), any(SortStatus.class));

            mockMvc.perform(
                            get("/posts/list/likes/1")
                                    .param("page", "0")
                                    .param("size", "10"))
                    .andDo(print())
                    .andExpect(status().isOk());
        }

        @Test
        @WithMockCustomUser
        @DisplayName("[성공] 커뮤니티 게시글 상세 조회")
        void selectPost() throws Exception {

            PostDto.Response response = postService.getPostDetails(1L, 1L);

            doReturn(response).when(postService).getPostDetails(anyLong(), anyLong());

            mockMvc.perform(
                            get("/posts/post/1"))
                    .andDo(print())
                    .andExpect(status().isOk());
        }
    }

    @Nested
    @DisplayName("커뮤니티 게시글 등록")
    class savePostTest {

        @Test
        @WithMockCustomUser
        @DisplayName("[성공] 이미지가 있을 때")
        void savePostWithImages_Success() throws Exception {

            doReturn(1L).when(postService).savePost(any(SaveRequest.class), anyLong());

            mockMvc.perform(
                            multipart("/posts/create")
                                    .file(getMultipartFile()).file(getMultipartFile())
                                    .contentType(MediaType.MULTIPART_FORM_DATA)
                                    .accept(MediaType.ALL)
                                    .characterEncoding(StandardCharsets.UTF_8)
                                    .param("categoryId", "1")
                                    .param("title", "제목입니다.")
                                    .param("content", "내용입니다."))
                    .andDo(print())
                    .andExpect(status().isOk());

        }

        @Test
        @WithMockCustomUser
        @DisplayName("[성공] 이미지가 없을 때")
        void savePostWithoutImages_Success() throws Exception {
            SaveRequest saveRequest = SaveRequest.builder()
                    .categoryId(1L)
                    .title("제목입니다.")
                    .content("제목입니다.").build();

            doReturn(1L).when(postService).savePost(eq(saveRequest), anyLong());

            mockMvc.perform(
                            multipart("/posts/create")
                                    .contentType(MediaType.MULTIPART_FORM_DATA)
                                    .accept(MediaType.ALL)
                                    .characterEncoding(StandardCharsets.UTF_8)
                                    .param("categoryId", "1")
                                    .param("title", "제목입니다.")
                                    .param("content", "내용입니다."))
                    .andDo(print())
                    .andExpect(status().isOk());

        }

        @Test
        @WithMockCustomUser
        @DisplayName("[실패] 필수 데이터가 없을 때")
        void savePostTestWithoutField_Fail() throws Exception {
            SaveRequest saveRequest = SaveRequest.builder()
                    .title("제목입니다.")
                    .content("제목입니다.").build();

            doThrow(NullPointerException.class).when(postService).savePost(eq(saveRequest), anyLong());

            mockMvc.perform(
                            multipart("/posts/create")
                                    .contentType(MediaType.MULTIPART_FORM_DATA)
                                    .accept(MediaType.ALL)
                                    .characterEncoding(StandardCharsets.UTF_8)
                                    .param("title", "제목입니다.")
                                    .param("content", "내용입니다."))
                    .andDo(print())
                    .andExpect(status().isBadRequest());

        }

        @Test
        @DisplayName("[실패] 로그인한 유저가 없을 때")
        void savePostTestWithoutLogin_Fail() throws Exception {

            doThrow(UserNotLoginedException.class).when(postService).savePost(any(SaveRequest.class), anyLong());

            mockMvc.perform(
                            multipart("/posts/create")
                                    .file(getMultipartFile()).file(getMultipartFile())
                                    .contentType(MediaType.MULTIPART_FORM_DATA)
                                    .accept(MediaType.ALL)
                                    .characterEncoding(StandardCharsets.UTF_8)
                                    .param("categoryId", "1")
                                    .param("title", "제목입니다.")
                                    .param("content", "내용입니다."))
                    .andDo(print())
                    .andExpect(status().is4xxClientError());

        }
    }

    @Nested
    @DisplayName("커뮤니티 게시글 수정")
    class updatePostTest {

        @Test
        @DisplayName("[성공] 이미지가 있을 때")
        void updatePostWithImages_Success() throws Exception {

            doReturn(1L).when(postService).updatePost(anyLong(), any(UpdateRequest.class));

            mockMvc.perform(
                            multipart("/posts/update/1")
                                    .contentType(MediaType.MULTIPART_FORM_DATA)
                                    .accept(MediaType.ALL)
                                    .characterEncoding(StandardCharsets.UTF_8)
                                    .param("title", "업데이트 제목입니다.")
                                    .param("content", "업데이트 내용입니다."))
                    .andDo(print())
                    .andExpect(status().isOk());
        }

        @Test
        @DisplayName("[성공] 이미지가 없을 때")
        void updatePostWithoutImages_Success() throws Exception {
            UpdateRequest request = UpdateRequest.builder()
                    .title("업데이트 제목입니다.")
                    .content("업데이트 내용입니다.").build();

            doReturn(1L).when(postService).updatePost(anyLong(), eq(request));

            mockMvc.perform(
                            multipart("/posts/update/1")
                                    .file(getMultipartFile())
                                    .contentType(MediaType.MULTIPART_FORM_DATA)
                                    .accept(MediaType.ALL)
                                    .characterEncoding(StandardCharsets.UTF_8)
                                    .param("title", "업데이트 제목입니다.")
                                    .param("content", "업데이트 내용입니다."))
                    .andDo(print())
                    .andExpect(status().isOk());
        }

        @Test
        @DisplayName("[실패] 필수 데이터가 없을 때")
        void updatePostWithoutField_Fail() throws Exception {
            UpdateRequest request = UpdateRequest.builder()
                    .content("업데이트 내용입니다.").build();

            doThrow(NullPointerException.class).when(postService).updatePost(anyLong(), eq(request));

            mockMvc.perform(
                            multipart("/posts/update/1")
                                    .contentType(MediaType.MULTIPART_FORM_DATA)
                                    .accept(MediaType.ALL)
                                    .characterEncoding(StandardCharsets.UTF_8)
                                    .param("content", "업데이트 내용입니다."))
                    .andDo(print())
                    .andExpect(status().isBadRequest());
        }
    }

    @Test
    @DisplayName("커뮤니티 게시글 삭제")
    void deletePostTest() throws Exception {

        doNothing().when(postService).deletePost(anyLong());

        mockMvc.perform(
                        post("/posts/delete/1"))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Nested
    @DisplayName("좋아요 등록 및 삭제 기능")
    class LikeTest {

        @Test
        @WithMockCustomUser
        @DisplayName("[성공]")
        void hitLike_Success() throws Exception {
            LikeDto likeDto = LikeDto.builder()
                    .likeStatus(true).count(1).build();

            doReturn(likeDto).when(postService).likeProcess(anyLong(), anyLong());

            mockMvc.perform(
                    post("/posts/like/1"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.likeStatus").value(true))
                    .andExpect(jsonPath("$.count").value("1"))
                    .andDo(print());;
        }

        @Test
        @DisplayName("[실패] 로그인한 유저가 없을 때")
        void hitLike_Fail() throws Exception {

            doThrow(UserNotLoginedException.class).when(postService).likeProcess(anyLong(), anyLong());

            mockMvc.perform(
                            post("/posts/like/1"))
                    .andExpect(status().is4xxClientError())
                    .andDo(print());
        }
    }

    @Nested
    @DisplayName("댓글 등록 기능")
    class ReplyTest {

        @Test
        @WithMockCustomUser
        @DisplayName("[성공] Super - 댓글 등록")
        void saveSuperReply() throws Exception {
            SuperRequest request = SuperRequest.builder()
                    .postId(1L)
                    .content("댓글 내용입니다.").build();

            doReturn(1L).when(postService).saveSuperReply(anyLong(), any(SuperRequest.class));

            mockMvc.perform(
                        post("/posts/reply/create/super")
                            .contentType(MediaType.APPLICATION_JSON)
                            .characterEncoding(StandardCharsets.UTF_8)
                            .content(objectMapper.writeValueAsString(request)))
                    .andDo(print())
                    .andExpect(status().isOk());
        }

        @Test
        @WithMockCustomUser
        @DisplayName("[성공] Sub - 댓글 등록")
        void saveSubReply() throws Exception {
            Request request = Request.builder()
                    .replyId(1L)
                    .content("댓글 내용입니다.").build();

            doReturn(1L).when(postService).saveSubReply(anyLong(), any(Request.class));

            mockMvc.perform(
                            post("/posts/reply/create/sub")
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .characterEncoding(StandardCharsets.UTF_8)
                                    .content(objectMapper.writeValueAsString(request)))
                    .andDo(print())
                    .andExpect(status().isOk());
        }

        @Test
        @WithMockCustomUser
        @DisplayName("[실패] Super - 필수 데이터가 없을 때")
        void saveSuperReplyWithoutField_fail() throws Exception {
            SuperRequest request = SuperRequest.builder()
                            .postId(1L).build();

            doThrow(NullPointerException.class).when(postService).saveSuperReply(anyLong(), eq(request));

            mockMvc.perform(
                            post("/posts/reply/create/super")
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .characterEncoding(StandardCharsets.UTF_8)
                                    .content(objectMapper.writeValueAsString(request)))
                    .andDo(print())
                    .andExpect(status().is4xxClientError());
        }

        @Test
        @WithMockCustomUser
        @DisplayName("[실패] Sub - 필수 데이터가 없을 때")
        void saveSubReplyWithoutField_fail() throws Exception {
            Request request = Request.builder()
                    .replyId(1L).build();

            doThrow(NullPointerException.class).when(postService).saveSubReply(anyLong(), eq(request));

            mockMvc.perform(
                            post("/posts/reply/create/sub")
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .characterEncoding(StandardCharsets.UTF_8)
                                    .content(objectMapper.writeValueAsString(request)))
                    .andDo(print())
                    .andExpect(status().is4xxClientError());
        }

        @Test
        @DisplayName("[실패] Super - 로그인한 유저가 없을 때")
        void saveSuperReplyWithoutLogin_Fail() throws Exception {

            doThrow(UserNotLoginedException.class).when(postService).saveSuperReply(anyLong(), any(SuperRequest.class));

            mockMvc.perform(
                            post("/posts/reply/create/super"))
                    .andDo(print())
                    .andExpect(status().is4xxClientError());
        }

        @Test
        @DisplayName("[실패] Sub - 로그인한 유저가 없을 때")
        void saveSubReplyWithoutLogin_Fail() throws Exception {

            doThrow(UserNotLoginedException.class).when(postService).saveSubReply(anyLong(), any(Request.class));

            mockMvc.perform(
                            post("/posts/reply/create/sub"))
                    .andDo(print())
                    .andExpect(status().is4xxClientError());
        }
    }

    @Test
    @DisplayName("댓글 수정 기능")
    void updateReplyTest() throws Exception {
        Request request = Request.builder()
                .replyId(1L)
                .content("업데이트 내용입니다.").build();

        doReturn(1L).when(postService).updateReply(any(Request.class));

        mockMvc.perform(
                        post("/posts/reply/update")
                                .contentType(MediaType.APPLICATION_JSON)
                                .characterEncoding(StandardCharsets.UTF_8)
                                .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("댓글 삭제 기능")
    void deleteReplyTest() throws Exception {

        doNothing().when(postService).deleteReply(anyLong());

        mockMvc.perform(
                        post("/posts/reply/delete/1"))
                .andDo(print())
                .andExpect(status().isOk());
    }

    private MockMultipartFile getMultipartFile() {
        return new MockMultipartFile(
                "multipartFiles", "filename.jpeg",
                "image/jpeg", "multipartFiles".getBytes());

    }
}