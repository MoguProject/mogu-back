package com.teamof4.mogu.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.teamof4.mogu.constants.CategoryNames;
import com.teamof4.mogu.dto.PostDto.MyPageResponse;
import com.teamof4.mogu.exception.user.*;
import com.teamof4.mogu.security.TokenProvider;
import com.teamof4.mogu.service.UserService;
import com.teamof4.mogu.util.WithMockCustomUser;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
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
import org.springframework.security.core.context.SecurityContext;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.context.WebApplicationContext;

import java.util.ArrayList;

import static com.teamof4.mogu.dto.UserDto.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@ExtendWith(SpringExtension.class)
@WebMvcTest(UserController.class)
@MockBean(JpaMetamodelMappingContext.class)
@AutoConfigureMockMvc(addFilters = false)
public class UserControllerTest {

    @MockBean
    private UserService userService;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private TokenProvider tokenProvider;

    @Autowired
    private WebApplicationContext context;

    @MockBean
    SecurityContext securityContext;

    SaveRequest saveRequest;

    @BeforeEach
    public void setup() throws Exception {
        saveRequest = SaveRequest.builder()
                .email("junesuck1234@gmail.com")
                .password("1234qwer!")
                .name("?????????")
                .nickname("frost11")
                .phone("01098765432")
                .build();
    }

    @Test
    @DisplayName("???????????? - ?????? ??? ???????????? ???????????? ?????? ??? ?????? ??????")
    void sendCertificationEmail_Success() throws Exception {
        EmailCertificationRequest request = EmailCertificationRequest.builder()
                .email("junesuck99@gmail.com")
                .build();

        given(userService.certificateByEmail(any(EmailCertificationRequest.class))).willReturn("1q2w3e4r");

        mockMvc.perform(
                        post("/users/email/certificate")
                                .contentType(MediaType.APPLICATION_JSON)
                                .characterEncoding("UTF-8")
                                .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string("1q2w3e4r"));

        verify(userService).certificateByEmail(any(EmailCertificationRequest.class));
    }

    @Test
    @DisplayName("???????????? - ?????? ????????? ?????? ???????????? ?????? ??????")
    void createUser_Success() throws Exception {
        //given:Mock ????????? ?????? ???????????? ???????????? ????????? ???????????? ?????????
        doNothing().when(userService).create(any(SaveRequest.class));

        //andExpect : ???????????? ?????? ???????????? ???????????? ??? ?????? ?????????
        mockMvc.perform(
                        post("/users/create")
                                .contentType(MediaType.APPLICATION_JSON)
                                .characterEncoding("UTF-8")
                                .content(objectMapper.writeValueAsString(saveRequest)))
                .andDo(print())
                .andExpect(status().isCreated());

        verify(userService).create(any(SaveRequest.class));
    }

    @Test
    @DisplayName("???????????? - ????????? ???????????? ????????????")
    void createUser_Failure_Duplicated_Email() throws Exception {
        doThrow(new DuplicatedEmailException()).when(userService).create(any(SaveRequest.class));

        mockMvc.perform(
                        post("/users/create")
                                .contentType(MediaType.APPLICATION_JSON)
                                .characterEncoding("UTF-8")
                                .content(objectMapper.writeValueAsString(saveRequest)))
                .andDo(print())
                .andExpect(status().isConflict());

        verify(userService).create(any(SaveRequest.class));
    }

    @Test
    @DisplayName("???????????? - ????????? ??????????????? ????????????")
    void createUser_Failure_Duplicated_Nickname() throws Exception {
        doThrow(new DuplicatedNicknameException()).when(userService).create(any(SaveRequest.class));

        mockMvc.perform(
                        post("/users/create")
                                .contentType(MediaType.APPLICATION_JSON)
                                .characterEncoding("UTF-8")
                                .content(objectMapper.writeValueAsString(saveRequest)))
                .andDo(print())
                .andExpect(status().isConflict());

        verify(userService).create(any(SaveRequest.class));
    }

    @Test
    @DisplayName("???????????? - ????????? ???????????? ????????????")
    void createUser_Failure_Duplicated_Phone() throws Exception {
        doThrow(new DuplicatedPhoneException()).when(userService).create(any(SaveRequest.class));

        mockMvc.perform(
                        post("/users/create")
                                .contentType(MediaType.APPLICATION_JSON)
                                .characterEncoding("UTF-8")
                                .content(objectMapper.writeValueAsString(saveRequest)))
                .andDo(print())
                .andExpect(status().isConflict());

        verify(userService).create(any(SaveRequest.class));
    }

    @Test
    @DisplayName("????????? - ?????? ?????? ??????")
    void login_Success() throws Exception {
        LoginRequest loginRequest = LoginRequest.builder()
                .email("frost0807@gmail.com")
                .password("1234qwer!")
                .build();

        given(userService.login(any(LoginRequest.class)))
                .willReturn("eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiIxIiwiaWF0IjoxNjcwMzMwMDYxLCJleHAiOjE2NzAzNzMyNjF9.Knct2hDifWGO2c7ZSOSI79zKzIt8_eB3wnW7BiwmuJI");

        mockMvc.perform(
                        post("/users/login")
                                .contentType(MediaType.APPLICATION_JSON)
                                .characterEncoding("UTF-8")
                                .content(objectMapper.writeValueAsString(loginRequest)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(cookie().value("access-token", "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiIxIiwiaWF0IjoxNjcwMzMwMDYxLCJleHAiOjE2NzAzNzMyNjF9.Knct2hDifWGO2c7ZSOSI79zKzIt8_eB3wnW7BiwmuJI"));
    }

    @Test
    @DisplayName("??????????????? - ????????? ??? ???????????? ?????? ??????")
    @WithMockCustomUser
    void getMyPageInformation_Success() throws Exception {
        given(userService.getMyPageInformation(1L))
                .willReturn(MyInfoResponse.builder()
                        .email("email@email.com")
                        .nickname("frost")
                        .name("?????????")
                        .phone("01012341234")
                        .preferredMethod("????????????")
                        .region("??????")
                        .build()
                );

        mockMvc.perform(
                        get("/users/mypage")
                                .characterEncoding("utf-8"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nickname").value("frost"))
                .andExpect(jsonPath("$.email").value("email@email.com"))
                .andExpect(jsonPath("$.name").value("?????????"))
                .andExpect(jsonPath("$.phone").value("01012341234"))
                .andExpect(jsonPath("$.preferredMethod").value("????????????"))
                .andExpect(jsonPath("$.region").value("??????"));
    }

    @Test
    @DisplayName("???????????? ?????? - ???????????? ?????? ??????")
    @WithMockCustomUser
    void updatePassword_Success() throws Exception {
        UpdatePasswordRequest request = UpdatePasswordRequest.builder()
                .currentPassword("1234qwer!")
                .newPassword("1234qwer!@")
                .build();

        doNothing().when(userService).updatePassword(request, 1L);

        mockMvc.perform(
                        put("/users/update/password")
                                .characterEncoding("UTF-8")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isOk());

        verify(userService).updatePassword(any(UpdatePasswordRequest.class), any(Long.class));
    }

    @Test
    @DisplayName("???????????? ?????? - ?????? ??????????????? ???????????? ?????? ???????????? ?????? ??????")
    @WithMockCustomUser
    void updatePassword_Failure_WrongPassword() throws Exception {
        UpdatePasswordRequest request = UpdatePasswordRequest.builder()
                .currentPassword("1234qwer!")
                .newPassword("1234qwer!@")
                .build();

        doThrow(new WrongPasswordException()).when(userService).updatePassword(any(UpdatePasswordRequest.class), any(Long.class));

        mockMvc.perform(
                        put("/users/update/password")
                                .characterEncoding("UTF-8")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isUnauthorized());

        verify(userService).updatePassword(any(UpdatePasswordRequest.class), any(Long.class));
    }

    @Test
    @DisplayName("???????????? ?????? - ????????? ??????????????? ?????? ?????? ??????????????? ????????? ??? ?????? ??????")
    @WithMockCustomUser
    void updatePassword_Failure_AlreadyMyPassword() throws Exception {
        UpdatePasswordRequest request = UpdatePasswordRequest.builder()
                .currentPassword("1234qwer!")
                .newPassword("1234qwer!")
                .build();

        doThrow(new AlreadyMyPasswordException()).when(userService).updatePassword(any(UpdatePasswordRequest.class), any(Long.class));

        mockMvc.perform(
                        put("/users/update/password")
                                .characterEncoding("UTF-8")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isConflict());

        verify(userService).updatePassword(any(UpdatePasswordRequest.class), any(Long.class));
    }

    @Test
    @DisplayName("???????????? - ??????")
    @WithMockCustomUser
    void delete_Success() throws Exception {
        DeleteRequest requestDto = DeleteRequest.builder()
                .password("1234qwer")
                .build();

        doNothing().when(userService).delete(any(DeleteRequest.class), anyLong());

        mockMvc.perform(
                        post("/users/delete")
                                .characterEncoding("UTF-8")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(requestDto)))
                .andDo(print())
                .andExpect(status().isOk());

        verify(userService).delete(any(DeleteRequest.class), anyLong());
    }

    @Test
    @DisplayName("???????????? - ????????? ??????????????? ??????")
    @WithMockCustomUser
    void delete_Failure_WrongPassword() throws Exception {
        DeleteRequest requestDto = DeleteRequest.builder()
                .password("1234qwer")
                .build();

        doThrow(new WrongPasswordException()).when(userService).delete(any(DeleteRequest.class), anyLong());

        mockMvc.perform(
                        post("/users/delete")
                                .characterEncoding("UTF-8")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(requestDto)))
                .andDo(print())
                .andExpect(status().isUnauthorized());

        verify(userService).delete(any(DeleteRequest.class), anyLong());
    }

    @Test
    @DisplayName("???????????? ?????? - ??? ???????????? ????????? ??????")
    @WithMockCustomUser
    void createNewPassword_Success() throws Exception {
        CreatePasswordRequest request = CreatePasswordRequest.builder()
                .name("?????????")
                .email("junesuck99@gmail.com")
                .build();

        doNothing().when(userService).createNewPassword(any(CreatePasswordRequest.class));

        mockMvc.perform(post("/users/email/create/new-password")
                        .characterEncoding("UTF-8")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isOk());

        verify(userService).createNewPassword(any(CreatePasswordRequest.class));
    }

    @Test
    @DisplayName("???????????? ?????? - ????????? ???????????? ???????????? ???????????? ?????? ??????")
    void createNewPassword_Failure() throws Exception {
        CreatePasswordRequest request = CreatePasswordRequest.builder()
                .name("?????????")
                .email("junesuck99@gmail.com")
                .build();

        doThrow(new UserNotFoundException("???????????? ?????? ??????????????????"))
                .when(userService).createNewPassword(any(CreatePasswordRequest.class));

        mockMvc.perform(
                        post("/users/email/create/new-password")
                                .characterEncoding("UTF-8")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isNotFound());

        verify(userService).createNewPassword(any(CreatePasswordRequest.class));
    }

    @Test
    @DisplayName("??????????????? - ?????? ???????????? ???????????? ??????")
    @WithMockCustomUser
    void getMyProjectPosts_Success() throws Exception {
        Pageable pageable = PageRequest.of(0, 10);
        Page response = new PageImpl(new ArrayList());

        given(userService.getMyParticipatingPosts(1L, pageable, CategoryNames.PROJECT))
                .willReturn(response);

        mockMvc.perform(
                        get("/users/mypage/post/project")
                                .characterEncoding("UTF-8")
                                .param("size", "10")
                                .param("page", "0"))
                .andDo(print())
                .andExpect(status().isOk());

        verify(userService).getMyParticipatingPosts(anyLong(), any(Pageable.class), any(CategoryNames.class));
    }

    @Test
    @DisplayName("??????????????? - ?????? ???????????? ????????? ??????")
    @WithMockCustomUser
    void getMyStudyPosts_Success() throws Exception {
        Pageable pageable = PageRequest.of(0, 10);
        Page response = new PageImpl(new ArrayList());

        given(userService.getMyParticipatingPosts(1L, pageable, CategoryNames.STUDY))
                .willReturn(response);

        mockMvc.perform(
                        get("/users/mypage/post/study")
                                .characterEncoding("UTF-8")
                                .param("size", "10")
                                .param("page", "0"))
                .andDo(print())
                .andExpect(status().isOk());

        verify(userService).getMyParticipatingPosts(anyLong(), any(Pageable.class), any(CategoryNames.class));

    }

    @Test
    @DisplayName("??????????????? - ?????? ????????? ??? ????????? ?????????")
    @WithMockCustomUser
    void getPostsILiked_Success() throws Exception {
        Pageable pageable = PageRequest.of(0, 10);
        Page response = new PageImpl(new ArrayList());

        given(userService.getPostsILiked(1L, pageable))
                .willReturn(response);

        mockMvc.perform(
                        get("/users/mypage/post/liked")
                                .characterEncoding("UTF-8")
                                .param("size", "10")
                                .param("page", "0"))
                .andDo(print())
                .andExpect(status().isOk());

        verify(userService).getPostsILiked(anyLong(), any(Pageable.class));

    }

    @Test
    @DisplayName("??????????????? - ?????? ?????? ??? ????????? ?????????")
    @WithMockCustomUser
    void getPostsIReplied_Success() throws Exception {
        Pageable pageable = PageRequest.of(0, 10);
        Page response = new PageImpl(new ArrayList());

        given(userService.getPostsIReplied(1L, pageable))
                .willReturn(response);

        mockMvc.perform(
                        get("/users/mypage/post/replied")
                                .characterEncoding("UTF-8")
                                .param("size", "10")
                                .param("page", "0"))
                .andDo(print())
                .andExpect(status().isOk());

        verify(userService).getPostsIReplied(anyLong(), any(Pageable.class));

    }
}