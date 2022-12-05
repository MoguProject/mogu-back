package com.teamof4.mogu.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.teamof4.mogu.dto.UserDto;
import com.teamof4.mogu.dto.UserDto.SaveRequest;
import com.teamof4.mogu.entity.User;
import com.teamof4.mogu.security.TokenProvider;
import com.teamof4.mogu.service.UserService;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockFilterConfig;
import org.springframework.security.config.BeanIds;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.filter.DelegatingFilterProxy;

import javax.servlet.http.Cookie;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


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

    @BeforeEach
    public void setup() throws Exception{
//        DelegatingFilterProxy delegatingFilterProxy = new DelegatingFilterProxy();
//        delegatingFilterProxy.init(
//                new MockFilterConfig(context.getServletContext(), BeanIds.SPRING_SECURITY_FILTER_CHAIN)
//        );
//        mockMvc = MockMvcBuilders
//                .webAppContextSetup(context)
//                .apply(springSecurity())
//                .build();
    }

    @Test
    @DisplayName("회원가입 - 이메일 인증코드 발송")
    void send_EmailCode_Success() throws Exception {

    }


    @Test
    @DisplayName("회원가입 - 모든 유효성 검사 통과하면 가입 완료")
    void createUser_Success() throws Exception {
        //given:Mock 객체가 특정 상황에서 해야하는 행위를 정의하는 메소드
        SaveRequest saveRequest = SaveRequest.builder()
                .email("junesuck1234@gmail.com")
                .password("1234qwer!")
                .name("최준석")
                .nickname("frost11")
                .phone("01098765432")
                .build();

        //andExpect : 기대하는 값이 나왔는지 체크해볼 수 있는 메소드
        mockMvc.perform(
                        post("/user/create")
                                .contentType(MediaType.APPLICATION_JSON)
                                .characterEncoding("UTF-8")
                                .content(objectMapper.writeValueAsString(saveRequest)))
                .andDo(print())
                .andExpect(status().isCreated());
    }

//    @Test
//    @DisplayName("회원가입 - 중복된 이메일로 가입실패")
//    void createUser_Failure_Duplicated_Email() throws Exception {
//        SaveRequest saveRequest = SaveRequest.builder()
//                .email("junesuck99@gmail.com")
//                .password("1234qwer!")
//                .name("최준석")
//                .nickname("frosty77")
//                .phone("01013572468")
//                .build();
//
//        mockMvc.perform(
//                        post("/user/create")
//                                .contentType(MediaType.APPLICATION_JSON)
//                                .characterEncoding("utf-8")
//                                .content(objectMapper.writeValueAsString(saveRequest)))
//                .andDo(print())
//                .andExpect(status().isConflict());
//    }
//
//    @Test
//    @DisplayName("마이페이지 - 로그인 한 회원정보 반환 성공")
//    void getMyPageInformation_Success() throws Exception {
//        Cookie cookie = new Cookie("access-token", "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiIxIiwiaWF0IjoxNjcwMjI5NjYwLCJleHAiOjE2NzAyNzI4NjB9.6dwlkDtDkV30-vTGdpWN7oV1I4MqkSGwAPz563DaopY");
//
//        given(userService.getMyPageInformation(1L)).willReturn(
//                UserDto.UserInfoResponse.builder().nickname("frost").email("email@email.com").build()
//        );
//
//        mockMvc.perform(
//                        get("/user/mypage")
//                                .cookie(cookie)
//                                .characterEncoding("utf-8")
//                                .contentType(MediaType.APPLICATION_JSON))
//                .andDo(print())
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.nickname").exists())
//                .andExpect(jsonPath("$.email").exists());
//    }
}