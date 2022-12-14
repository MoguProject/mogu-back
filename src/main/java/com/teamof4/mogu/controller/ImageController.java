package com.teamof4.mogu.controller;

import com.teamof4.mogu.constants.ResponseConstants;
import com.teamof4.mogu.entity.Image;
import com.teamof4.mogu.exception.user.UserNotLoginedException;
import com.teamof4.mogu.service.ImageService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
@RequestMapping("/image")
@Api(tags = {"04. Image API"})
public class ImageController {

    private final ImageService imageService;

    @PostMapping("/upload")
    @ApiOperation(value = "에디터 이미지 등록")
    public ResponseEntity<String> saveImage(@RequestPart MultipartFile multipartFile,
                                            @AuthenticationPrincipal Long userId) {
        if (userId == null) {
            throw new UserNotLoginedException();
        }

        Image image = imageService.savePostImage(multipartFile);
        return ResponseEntity.ok(image.getImageUrl());
    }

    @PostMapping("/delete/{imageUrl}")
    @ApiOperation(value = "에디터 이미지 삭제")
    public ResponseEntity<Void> deleteImage(@PathVariable String imageUrl,
                                            @AuthenticationPrincipal Long userId) {
        if (userId == null) {
            throw new UserNotLoginedException();
        }

        imageService.deletePostImage(imageUrl);
        return ResponseEntity.ok().build();
    }

}