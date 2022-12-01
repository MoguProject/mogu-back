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

    private final int SPLIT_INDEX = 57;

    @Transactional
    public Image saveDefaultProfileImage() {
        return imageRepository.findById(DEFAULT_PROFILE_IMAGE_ID)
                .orElseThrow(() -> new FailedImageUploadException("기본 이미지를 찾지 못했습니다."));
    }

    @Transactional
    public Image updateProfileImage(MultipartFile profileImage) {
        String imageUrl = awsS3Service.uploadImage(profileImage);
        Image image = imageRepository.save(ImageDto.of(imageUrl));

        return image;
    }

    //프로필 이미지가 기본 이미지가 아닐경우에만 이미지 테이블 삭제
    @Transactional
    public void deleteProfileImage(Image image) {
        if (image.getId() != DEFAULT_PROFILE_IMAGE_ID) {
            deleteImage(image);
        }
    }

    public Image savePostImage(MultipartFile profileImage) {

        String imageUrl = awsS3Service.uploadImage(profileImage);
        Image image = Image.builder()
                .imageUrl(imageUrl)
                .build();

        return imageRepository.save(image);
    }


    @Transactional
    public void deleteImage(Image targetTmage) {
        String fileName = targetTmage.getImageUrl().substring(SPLIT_INDEX);
        awsS3Service.deleteImage(fileName);
        imageRepository.delete(targetTmage);
    }
}
