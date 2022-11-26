package com.teamof4.mogu.service;

import com.teamof4.mogu.dto.ImageDto.SaveRequest;
import com.teamof4.mogu.entity.Image;
import com.teamof4.mogu.exception.image.FailedImageUploadException;
import com.teamof4.mogu.repository.ImageRepository;
import com.teamof4.mogu.util.aws.AwsS3Service;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@Service
@RequiredArgsConstructor
public class ImageService {

    private final ImageRepository imageRepository;
    private final AwsS3Service awsS3Service;

    @Transactional
    public Image saveProfileImage(MultipartFile profileImage) {
        if(profileImage.isEmpty()) {
            return imageRepository.findById(1L)
                    .orElseThrow(() -> new FailedImageUploadException("기본 이미지를 찾지 못했습니다."));
        }
        String imageUrl = awsS3Service.uploadProfileImage(profileImage);
        SaveRequest saveRequest = new SaveRequest(imageUrl);
        Image image = saveRequest.toEntity();

        return imageRepository.save(image);
    }
}
