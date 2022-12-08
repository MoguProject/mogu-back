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
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.http.MediaType;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.test.context.ActiveProfiles;
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
@ActiveProfiles("local")
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
                .name("최준석")
                .nickname("frost11")
                .phone("01098765432")
                .build();
    }

    @Test
    @DisplayName("회원가입 - 가입 전 이메일로 인증코드 발송 후 코드 반환")
    void sendCertificationEmail_Success() throws Exception {
        EmailCertificationRequest request = EmailCertificationRequest.builder()
                .email("junesuck99@gmail.com")
                .build();

        given(userService.certificateByEmail(any(EmailCertificationRequest.class))).willReturn("1q2w3e4r");

        mockMvc.perform(
                        post("/user/email/certificate")
                                .contentType(MediaType.APPLICATION_JSON)
                                .characterEncoding("UTF-8")
                                .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string("1q2w3e4r"));

        verify(userService).certificateByEmail(any(EmailCertificationRequest.class));
    }

    @Test
    @DisplayName("회원가입 - 모든 유효성 검사 통과하면 가입 완료")
    void createUser_Success() throws Exception {
        //given:Mock 객체가 특정 상황에서 해야하는 행위를 정의하는 메소드
        doNothing().when(userService).create(any(SaveRequest.class));

        //andExpect : 기대하는 값이 나왔는지 체크해볼 수 있는 메소드
        mockMvc.perform(
                        post("/user/create")
                                .contentType(MediaType.APPLICATION_JSON)
                                .characterEncoding("UTF-8")
                                .content(objectMapper.writeValueAsString(saveRequest)))
                .andDo(print())
                .andExpect(status().isCreated());

        verify(userService).create(any(SaveRequest.class));
    }

    @Test
    @DisplayName("회원가입 - 중복된 이메일로 가입실패")
    void createUser_Failure_Duplicated_Email() throws Exception {
        doThrow(new DuplicatedEmailException()).when(userService).create(any(SaveRequest.class));

        mockMvc.perform(
                        post("/user/create")
                                .contentType(MediaType.APPLICATION_JSON)
                                .characterEncoding("UTF-8")
                                .content(objectMapper.writeValueAsString(saveRequest)))
                .andDo(print())
                .andExpect(status().isConflict());

        verify(userService).create(any(SaveRequest.class));
    }

    @Test
    @DisplayName("회원가입 - 중복된 닉네임으로 가입실패")
    void createUser_Failure_Duplicated_Nickname() throws Exception {
        doThrow(new DuplicatedNicknameException()).when(userService).create(any(SaveRequest.class));

        mockMvc.perform(
                        post("/user/create")
                                .contentType(MediaType.APPLICATION_JSON)
                                .characterEncoding("UTF-8")
                                .content(objectMapper.writeValueAsString(saveRequest)))
                .andDo(print())
                .andExpect(status().isConflict());

        verify(userService).create(any(SaveRequest.class));
    }

    @Test
    @DisplayName("회원가입 - 중복된 연락처로 가입실패")
    void createUser_Failure_Duplicated_Phone() throws Exception {
        doThrow(new DuplicatedPhoneException()).when(userService).create(any(SaveRequest.class));

        mockMvc.perform(
                        post("/user/create")
                                .contentType(MediaType.APPLICATION_JSON)
                                .characterEncoding("UTF-8")
                                .content(objectMapper.writeValueAsString(saveRequest)))
                .andDo(print())
                .andExpect(status().isConflict());

        verify(userService).create(any(SaveRequest.class));
    }

    @Test
    @DisplayName("로그인 - 유저 토큰 반환")
    void login_Success() throws Exception {
        LoginRequest loginRequest = LoginRequest.builder()
                .email("frost0807@gmail.com")
                .password("1234qwer!")
                .build();

        given(userService.login(any(LoginRequest.class)))
                .willReturn("eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiIxIiwiaWF0IjoxNjcwMzMwMDYxLCJleHAiOjE2NzAzNzMyNjF9.Knct2hDifWGO2c7ZSOSI79zKzIt8_eB3wnW7BiwmuJI");

        mockMvc.perform(
                        post("/user/login")
                                .contentType(MediaType.APPLICATION_JSON)
                                .characterEncoding("UTF-8")
                                .content(objectMapper.writeValueAsString(loginRequest)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(cookie().value("access-token", "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiIxIiwiaWF0IjoxNjcwMzMwMDYxLCJleHAiOjE2NzAzNzMyNjF9.Knct2hDifWGO2c7ZSOSI79zKzIt8_eB3wnW7BiwmuJI"));
    }

    @Test
    @DisplayName("마이페이지 - 로그인 한 회원정보 반환 성공")
    @WithMockCustomUser
    void getMyPageInformation_Success() throws Exception {
        given(userService.getMyPageInformation(1L))
                .willReturn(UserInfoResponse.builder()
                        .email("email@email.com")
                        .nickname("frost")
                        .name("최준석")
                        .phone("01012341234")
                        .preferredMethod("오프라인")
                        .region("서울")
                        .build()
                );

        mockMvc.perform(
                        get("/user/mypage")
                                .characterEncoding("utf-8"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nickname").value("frost"))
                .andExpect(jsonPath("$.email").value("email@email.com"))
                .andExpect(jsonPath("$.name").value("최준석"))
                .andExpect(jsonPath("$.phone").value("01012341234"))
                .andExpect(jsonPath("$.preferredMethod").value("오프라인"))
                .andExpect(jsonPath("$.region").value("서울"));
    }

    @Test
    @DisplayName("회원정보 수정 - 비밀번호 수정 성공")
    @WithMockCustomUser
    void updatePassword_Success() throws Exception {
        UpdatePasswordRequest request = UpdatePasswordRequest.builder()
                .currentPassword("1234qwer!")
                .newPassword("1234qwer!@")
                .build();

        doNothing().when(userService).updatePassword(request, 1L);

        mockMvc.perform(
                        put("/user/update/password")
                                .characterEncoding("UTF-8")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isOk());

        verify(userService).updatePassword(any(UpdatePasswordRequest.class), any(Long.class));
    }

    @Test
    @DisplayName("회원정보 수정 - 기존 비밀번호가 일치하지 않아 비밀번호 수정 실패")
    @WithMockCustomUser
    void updatePassword_Failure_WrongPassword() throws Exception {
        UpdatePasswordRequest request = UpdatePasswordRequest.builder()
                .currentPassword("1234qwer!")
                .newPassword("1234qwer!@")
                .build();

        doThrow(new WrongPasswordException()).when(userService).updatePassword(any(UpdatePasswordRequest.class), any(Long.class));

        mockMvc.perform(
                        put("/user/update/password")
                                .characterEncoding("UTF-8")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isUnauthorized());

        verify(userService).updatePassword(any(UpdatePasswordRequest.class), any(Long.class));
    }

    @Test
    @DisplayName("회원정보 수정 - 새로운 비밀번호가 이미 기존 비밀번호와 동일할 때 수정 실패")
    @WithMockCustomUser
    void updatePassword_Failure_AlreadyMyPassword() throws Exception {
        UpdatePasswordRequest request = UpdatePasswordRequest.builder()
                .currentPassword("1234qwer!")
                .newPassword("1234qwer!")
                .build();

        doThrow(new AlreadyMyPasswordException()).when(userService).updatePassword(any(UpdatePasswordRequest.class), any(Long.class));

        mockMvc.perform(
                        put("/user/update/password")
                                .characterEncoding("UTF-8")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isConflict());

        verify(userService).updatePassword(any(UpdatePasswordRequest.class), any(Long.class));
    }

    @Test
    @DisplayName("회원탈퇴 - 성공")
    @WithMockCustomUser
    void delete_Success() throws Exception {
        DeleteRequest requestDto = DeleteRequest.builder()
                .password("1234qwer")
                .build();

        doNothing().when(userService).delete(any(DeleteRequest.class), anyLong());

        mockMvc.perform(
                        post("/user/delete")
                                .characterEncoding("UTF-8")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(requestDto)))
                .andDo(print())
                .andExpect(status().isOk());

        verify(userService).delete(any(DeleteRequest.class), anyLong());
    }

    @Test
    @DisplayName("회원탈퇴 - 잘못된 비밀번호로 실패")
    @WithMockCustomUser
    void delete_Failure_WrongPassword() throws Exception {
        DeleteRequest requestDto = DeleteRequest.builder()
                .password("1234qwer")
                .build();

        doThrow(new WrongPasswordException()).when(userService).delete(any(DeleteRequest.class), anyLong());

        mockMvc.perform(
                        post("/user/delete")
                                .characterEncoding("UTF-8")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(requestDto)))
                .andDo(print())
                .andExpect(status().isUnauthorized());

        verify(userService).delete(any(DeleteRequest.class), anyLong());
    }

    @Test
    @DisplayName("비밀번호 찾기 - 새 비밀번호 생성에 성공")
    @WithMockCustomUser
    void createNewPassword_Success() throws Exception {
        CreatePasswordRequest request = CreatePasswordRequest.builder()
                .name("최준석")
                .email("junesuck99@gmail.com")
                .build();

        doNothing().when(userService).createNewPassword(any(CreatePasswordRequest.class));

        mockMvc.perform(post("/user/email/create/new-password")
                        .characterEncoding("UTF-8")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isOk());

        verify(userService).createNewPassword(any(CreatePasswordRequest.class));
    }

    @Test
    @DisplayName("비밀번호 찾기 - 이름과 이메일이 일치하는 사용자가 없어 실패")
    void createNewPassword_Failure() throws Exception {
        CreatePasswordRequest request = CreatePasswordRequest.builder()
                .name("최준석")
                .email("junesuck99@gmail.com")
                .build();

        doThrow(new UserNotFoundException("존재하지 않는 사용자입니다"))
                .when(userService).createNewPassword(any(CreatePasswordRequest.class));

        mockMvc.perform(
                        post("/user/email/create/new-password")
                                .characterEncoding("UTF-8")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isNotFound());

        verify(userService).createNewPassword(any(CreatePasswordRequest.class));
    }

    @Test
    @DisplayName("마이페이지 - 내가 모집중인 프로젝트 출력")
    @WithMockCustomUser
    void getMyProjectPosts_Success() throws Exception {
        Pageable pageable = PageRequest.of(0, 10);
        ArrayList<MyPageResponse> response = new ArrayList<>();
        response.add(MyPageResponse.builder().build());

        given(userService.getMyParticipatingPosts(1L, pageable, CategoryNames.PROJECT))
                .willReturn(response);

        mockMvc.perform(
                        get("/user/mypage/post/project")
                                .characterEncoding("UTF-8")
                                .param("size", "10")
                                .param("page", "0"))
                .andDo(print())
                .andExpect(status().isOk());

        verify(userService).getMyParticipatingPosts(anyLong(), any(Pageable.class), any(CategoryNames.class));
    }

    @Test
    @DisplayName("마이페이지 - 내가 모집중인 스터디 출력")
    @WithMockCustomUser
    void getMyStudyPosts_Success() throws Exception {
        Pageable pageable = PageRequest.of(0, 10);
        ArrayList<MyPageResponse> response = new ArrayList<>();
        response.add(MyPageResponse.builder().build());

        given(userService.getMyParticipatingPosts(1L, pageable, CategoryNames.STUDY))
                .willReturn(response);

        mockMvc.perform(
                        get("/user/mypage/post/study")
                                .characterEncoding("UTF-8")
                                .param("size", "10")
                                .param("page", "0"))
                .andDo(print())
                .andExpect(status().isOk());

        verify(userService).getMyParticipatingPosts(anyLong(), any(Pageable.class), any(CategoryNames.class));

    }

    @Test
    @DisplayName("마이페이지 - 내가 좋아요 한 게시물 리스트")
    @WithMockCustomUser
    void getPostsILiked_Success() throws Exception {
        Pageable pageable = PageRequest.of(0, 10);
        ArrayList<MyPageResponse> response = new ArrayList<>();
        response.add(MyPageResponse.builder().build());

        given(userService.getPostsILiked(1L, pageable))
                .willReturn(response);

        mockMvc.perform(
                        get("/user/mypage/post/liked")
                                .characterEncoding("UTF-8")
                                .param("size", "10")
                                .param("page", "0"))
                .andDo(print())
                .andExpect(status().isOk());

        verify(userService).getPostsILiked(anyLong(), any(Pageable.class));

    }

    @Test
    @DisplayName("마이페이지 - 내가 댓글 단 게시물 리스트")
    @WithMockCustomUser
    void getPostsIReplied_Success() throws Exception {
        Pageable pageable = PageRequest.of(0, 10);
        ArrayList<MyPageResponse> response = new ArrayList<>();
        response.add(MyPageResponse.builder().build());

        given(userService.getPostsIReplied(1L, pageable))
                .willReturn(response);

        mockMvc.perform(
                        get("/user/mypage/post/replied")
                                .characterEncoding("UTF-8")
                                .param("size", "10")
                                .param("page", "0"))
                .andDo(print())
                .andExpect(status().isOk());

        verify(userService).getPostsIReplied(anyLong(), any(Pageable.class));

    }
}