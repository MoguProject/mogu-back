package com.teamof4.mogu.controller;

import com.teamof4.mogu.dto.UserDto.SaveRequest;
import com.teamof4.mogu.service.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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

    @PostMapping("/create")
    @ApiOperation(value = "회원 등록")
    public ResponseEntity<Void> createUser(@Valid @RequestPart SaveRequest requestDto,
                                           @RequestPart MultipartFile profileImage) {
        userService.save(requestDto, profileImage);

        return CREATED;
    }

    @PostMapping("/login")
    @ApiOperation(value = "로그인")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest requestDto) {
        LoginResponse loginResponse = userService.login(requestDto);

        return ResponseEntity.ok(loginResponse);
    }

    @GetMapping("/mypage")
    @ApiOperation(value = "마이페이지 회원정보 출력")
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
}