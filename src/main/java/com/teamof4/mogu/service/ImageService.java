package com.teamof4.mogu.service;

import com.teamof4.mogu.dto.ImageDto;
import com.teamof4.mogu.dto.ImageDto.SaveRequest;
import com.teamof4.mogu.entity.Image;
import com.teamof4.mogu.exception.image.FailedImageUploadException;
import com.teamof4.mogu.repository.ImageRepository;
import com.teamof4.mogu.util.aws.AwsS3Service;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Service
@RequiredArgsConstructor
public class ImageService {

    private final ImageRepository imageRepository;
    private final AwsS3Service awsS3Service;

    public Image saveProfileImage(MultipartFile profileImage) {
        try {
            String imageUrl = awsS3Service.upload(profileImage, "static");

            Image image = SaveRequest.builder()
                    .imageUrl(imageUrl)
                    .build()
                    .toEntity();

            return imageRepository.save(image);

        } catch (IOException e) {
            throw new FailedImageUploadException();
        }
    }
}
