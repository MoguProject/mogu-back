package com.teamof4.mogu.service;

import com.teamof4.mogu.dto.ImageDto;
import com.teamof4.mogu.entity.Image;
import com.teamof4.mogu.exception.image.FailedImageUploadException;
import com.teamof4.mogu.repository.ImageRepository;
import com.teamof4.mogu.util.aws.AwsS3Service;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import static com.teamof4.mogu.constants.DefaultImageConstants.DEFAULT_PROFILE_IMAGE_ID;

@Service
@RequiredArgsConstructor
public class ImageService {

    private final ImageRepository imageRepository;
    private final AwsS3Service awsS3Service;

    @Transactional
    public Image saveProfileImage(MultipartFile profileImage) {
        if (profileImage.isEmpty()) {
            return imageRepository.findById(DEFAULT_PROFILE_IMAGE_ID)
                    .orElseThrow(() -> new FailedImageUploadException("기본 이미지를 찾지 못했습니다."));
        }
        String imageUrl = awsS3Service.uploadProfileImage(profileImage);
        Image image = ImageDto.of(imageUrl);

        return imageRepository.save(image);
    }

    @Transactional
    public Image updateProfileImage(MultipartFile profileImage) {
        String imageUrl = awsS3Service.uploadProfileImage(profileImage);
        Image image = ImageDto.of(imageUrl);

        return imageRepository.save(image);
    }

}
