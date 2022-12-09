package com.teamof4.mogu.controller;

import com.teamof4.mogu.constants.CategoryNames;
import com.teamof4.mogu.dto.PostDto.MyPageResponse;
import com.teamof4.mogu.dto.UserDto.*;
import com.teamof4.mogu.service.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiModelProperty;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.util.List;

import static com.teamof4.mogu.constants.ResponseConstants.CREATED;
import static com.teamof4.mogu.constants.ResponseConstants.OK;
import static com.teamof4.mogu.dto.UserDto.*;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/user")
@Api(tags = {"01. User API"})
public class UserController {

    private final UserService userService;

    @PostMapping("/create")
    @ApiOperation(value = "회원 등록")
    public ResponseEntity<Void> createUser(@Valid @RequestBody SaveRequest requestDto) {
        userService.create(requestDto);

        return CREATED;
    }

    @PostMapping("/login")
    @ApiOperation(value = "로그인한 유저 토큰 반환")
    public ResponseEntity<Void> login(@Valid @RequestBody LoginRequest requestDto) {
        String token = userService.login(requestDto);
        ResponseCookie cookie =
                ResponseCookie.from("access-token", token)
                        .path("/")
                        .httpOnly(true)
                        .maxAge(12 * (60 * 60) + 9 * (60 * 60))
                        .build();

        return ResponseEntity.ok().header(HttpHeaders.SET_COOKIE, cookie.toString()).build();
    }

    @GetMapping("/logout")
    @ApiOperation(value = "사용자 로그아웃")
    public ResponseEntity<Void> logout(@AuthenticationPrincipal Long userId) {
        ResponseCookie cookie = ResponseCookie.from("access-token", null)
                .path("/")
                .maxAge(0)
                .build();

        return ResponseEntity.ok().header(HttpHeaders.SET_COOKIE, cookie.toString()).build();
    }

    @GetMapping("/login/info")
    @ApiOperation(value = "로그인 정보 반환")
    public ResponseEntity<LoginInfoResponse> getLoginInformation(@AuthenticationPrincipal Long userId) {
        LoginInfoResponse loginInfoResponse = userService.getLoginInformation(userId);

        return ResponseEntity.ok(loginInfoResponse);
    }

    @GetMapping("/mypage")
    @ApiOperation(value = "마이페이지 회원정보 반환")
    public ResponseEntity<UserInfoResponse> getMyPageInformation(@AuthenticationPrincipal Long userId) {
        UserInfoResponse userInfoResponse = userService.getMyPageInformation(userId);

        return ResponseEntity.ok(userInfoResponse);
    }

    @PutMapping("/update")
    @ApiOperation(value = "회원정보 수정")
    public ResponseEntity<Void> update(@Valid @RequestPart UpdateRequest requestDto,
                                       @RequestPart MultipartFile profileImage,
                                       @AuthenticationPrincipal Long userId) {
        userService.update(requestDto, profileImage, userId);

        return OK;
    }

    @PutMapping("/update/password")
    @ApiModelProperty(value = "비밀번호 변경")
    public ResponseEntity<Void> updatePassword(@Valid @RequestBody UpdatePasswordRequest requestDto,
                                               @AuthenticationPrincipal Long userId) {
        userService.updatePassword(requestDto, userId);

        return OK;
    }

    @PostMapping("/delete")
    @ApiOperation(value = "회원탈퇴")
    public ResponseEntity<Void> delete(@Valid @RequestBody DeleteRequest requestDto, @AuthenticationPrincipal Long userId) {
        userService.delete(requestDto, userId);

        return OK;
    }

    @PostMapping("/email/certificate")
    @ApiOperation(value = "인증메일 발송")
    public ResponseEntity<String> certificateEmail(@Valid @RequestBody EmailCertificationRequest requestDto) {
        String certificationCode = userService.certificateByEmail(requestDto);

        return ResponseEntity.ok(certificationCode);
    }

    @PostMapping("/email/create/new-password")
    @ApiOperation(value = "새 랜덤 비밀번호 생성")
    public ResponseEntity<Void> createNewPassword(@Valid @RequestBody CreatePasswordRequest requestDto) {
        userService.createNewPassword(requestDto);

        return OK;
    }

    @GetMapping("/mypage/post/project")
    @ApiOperation(value = "마이페이지 내가 모집중인 프로젝트")
    public ResponseEntity<List> getMyProjectPosts(@PageableDefault Pageable pageable,
                                                  @AuthenticationPrincipal Long userId) {
        List<MyPageResponse> response = userService.getMyParticipatingPosts(userId, pageable, CategoryNames.PROJECT);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/mypage/post/study")
    @ApiOperation(value = "마이페이지 내가 모집중인 스터디")
    public ResponseEntity<List<MyPageResponse>> getMyStudyPosts(@PageableDefault Pageable pageable,
                                                                @AuthenticationPrincipal Long userId) {
        List<MyPageResponse> response = userService.getMyParticipatingPosts(userId, pageable, CategoryNames.STUDY);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/mypage/post/liked")
    @ApiOperation(value = "마이페이지 내가 좋아요 한 게시물 리스트")
    public ResponseEntity<List<MyPageResponse>> getPostsILiked(@PageableDefault Pageable pageable,
                                                                @AuthenticationPrincipal Long userId) {
        List<MyPageResponse> response = userService.getPostsILiked(userId, pageable);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/mypage/post/replied")
    @ApiOperation(value = "마이페이지 내가 댓글 단 게시물 리스트")
    public ResponseEntity<List<MyPageResponse>> getPostsIReplied(@PageableDefault Pageable pageable,
                                                                 @AuthenticationPrincipal Long userId) {
        List<MyPageResponse> response = userService.getPostsIReplied(userId, pageable);

        return ResponseEntity.ok(response);
    }
}