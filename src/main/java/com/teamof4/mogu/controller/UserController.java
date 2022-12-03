package com.teamof4.mogu.controller;

import com.teamof4.mogu.dto.PostDto;
import com.teamof4.mogu.dto.UserDto;
import com.teamof4.mogu.dto.UserDto.SaveRequest;
import com.teamof4.mogu.service.PostService;
import com.teamof4.mogu.service.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;

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
    private final PostService postService;

    @PostMapping("/create")
    @ApiOperation(value = "회원 등록")
    public ResponseEntity<Void> createUser(@Valid @RequestBody SaveRequest requestDto) {
        userService.save(requestDto);

        return CREATED;
    }

    @PostMapping("/login")
    @ApiOperation(value = "로그인한 유저 토큰 반환")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest requestDto) {
        LoginResponse loginResponse = userService.login(requestDto);

        return ResponseEntity.ok(loginResponse);
    }

    @GetMapping("/login/info")
    @ApiOperation(value = "로그인 정보 반환")
    public ResponseEntity<LoginInfoResponse> getLoginInformation(@AuthenticationPrincipal Long userId) {
        LoginInfoResponse loginInfoResponse = userService.getLoginInformation(userId);

        return ResponseEntity.ok(loginInfoResponse);
    }

    @GetMapping("/mypage")
    @ApiOperation(value = "마이페이지 회원정보 반환")
    public ResponseEntity<UserInfoResponse> getMyPageInformation(
            @AuthenticationPrincipal Long userId) {
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
    public ResponseEntity<Void> updatePassword(
            @Valid @RequestBody UpdatePasswordRequest requestDto,
            @AuthenticationPrincipal Long userId) {
        userService.updatePassword(requestDto, userId);

        return OK;
    }

    @DeleteMapping("/delete")
    @ApiOperation(value = "회원탈퇴")
    public ResponseEntity<Void> delete(@Valid @RequestBody DeleteRequest requestDto,
                                       @AuthenticationPrincipal Long userId) {
        userService.delete(requestDto, userId);

        return OK;
    }

    @PostMapping("/email/certificate")
    @ApiOperation(value = "인증메일 발송")
    public ResponseEntity<String> certificateEmail(
            @Valid @RequestBody EmailCertificationRequest requestDto) {
        String certificationCode = userService.certificateByEmail(requestDto);

        return ResponseEntity.ok(certificationCode);
    }

    @PostMapping("/email/create/new-password")
    @ApiOperation(value = "새 랜덤 비밀번호 생성")
    public ResponseEntity<Void> createNewPassword(
            @Valid @RequestBody CreatePasswordRequest requestDto) {
        userService.createNewPassword(requestDto);

        return OK;
    }

    @GetMapping("/mypage/post/like")
    @ApiOperation(value = "마이페이지 내가 좋아요 한 게시물 리스트")
    public ResponseEntity<Page<PostDto.Response>> getMyPostsByLiked(
            @PageableDefault Pageable pageable,
            @AuthenticationPrincipal Long userId) {
        Page<PostDto.Response> pageResponse = userService.getMyPostsByLiked(userId, pageable);

        return ResponseEntity.ok(pageResponse);
    }
}