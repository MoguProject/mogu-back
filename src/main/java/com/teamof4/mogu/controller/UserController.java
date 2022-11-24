package com.teamof4.mogu.controller;

import com.teamof4.mogu.dto.UserDto;
import com.teamof4.mogu.dto.UserDto.SaveRequest;
import com.teamof4.mogu.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;

import static com.teamof4.mogu.constants.ResponseConstants.CREATED;

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
}