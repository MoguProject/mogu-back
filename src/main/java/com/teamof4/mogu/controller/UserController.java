package com.teamof4.mogu.controller;

import com.teamof4.mogu.dto.UserDto.SaveRequest;
import com.teamof4.mogu.service.UserService;
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
public class UserController {

    private final UserService userService;

    @PostMapping("/create")
    public ResponseEntity<Void> createUser(@Valid @RequestPart SaveRequest requestDto,
                                           @RequestPart MultipartFile profileImage) {
        userService.save(requestDto, profileImage);

        return CREATED;
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest requestDto) {
        LoginResponse loginResponse = userService.login(requestDto);

        return ResponseEntity.ok(loginResponse);
    }

    @GetMapping("/mypage")
    public ResponseEntity<UserInfoResponse> getMyPageInformation(
            @AuthenticationPrincipal Long userId) {
        UserInfoResponse userInfoResponse = userService.getMyPageInformation(userId);

        return ResponseEntity.ok(userInfoResponse);
    }

    @PutMapping("/update")
    public ResponseEntity<Void> update(@Valid @RequestPart UpdateRequest requestDto,
                                       @RequestPart MultipartFile profileImage,
                                       @AuthenticationPrincipal Long userId) {
        userService.update(requestDto, profileImage, userId);

        return OK;
    }

    @DeleteMapping("/delete")
    public ResponseEntity<Void> delete(@RequestBody DeleteRequest requestDto,
                                       @AuthenticationPrincipal Long userId) {
        userService.delete(requestDto, userId);

        return OK;
    }


}