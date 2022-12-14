package com.teamof4.mogu.service;

import com.teamof4.mogu.dto.ImageDto;
import com.teamof4.mogu.entity.Image;
import com.teamof4.mogu.entity.ImagePost;
import com.teamof4.mogu.entity.User;
import com.teamof4.mogu.exception.image.ImageNotFoundException;
import com.teamof4.mogu.exception.user.UserNotMatchException;
import com.teamof4.mogu.repository.ImagePostRepository;
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
    private final ImagePostRepository imagePostRepository;
    private final AwsS3Service awsS3Service;
    private final PostService postService;

    private final int SPLIT_INDEX = 57;

    public Image getImageByImageUrl(String imageUrl) {
        return imageRepository.findByImageUrl(imageUrl)
                .orElseThrow(ImageNotFoundException::new);
    }

    public Image getImageById(Long imageId) {
        return imageRepository.findById(imageId)
                .orElseThrow(ImageNotFoundException::new);
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

    public Image savePostImage(MultipartFile postImage) {
        String imageUrl = awsS3Service.uploadImage(postImage);
        Image image = Image.builder()
                .imageUrl(imageUrl)
                .build();
        return imageRepository.save(image);
    }

    public Image saveImagesFromEditor(MultipartFile postImage, Long currentUserId) {

        Image image = savePostImage(postImage);

        saveImagePost(currentUserId, image);

        return image;
    }


    @Transactional
    public void deletePostImage(String imageUrl, Long currentUserId) {
        User user = postService.getUser(currentUserId);
        Image image = getImageByImageUrl(imageUrl);

        ImagePost imagePost = imagePostRepository.findByImage(image).orElseThrow(
                () -> new ImageNotFoundException("삭제할 수 있는 이미지가 없습니다."));

        if (!user.equals(imagePost.getUser())) {
            throw new UserNotMatchException("본인이 등록한 이미지만 삭제할 수 있습니다.");
        }

        imagePostRepository.delete(imagePost);
        deleteImage(image);
    }

    @Transactional
    public void deleteImage(Image targetImage) {
        String fileName = targetImage.getImageUrl().substring(SPLIT_INDEX);
        awsS3Service.deleteImage(fileName);
        imageRepository.delete(targetImage);
    }

    private void saveImagePost(Long currentUserId, Image image) {
        User user = postService.getUser(currentUserId);

        ImagePost imagePost = ImagePost.builder()
                .image(image)
                .user(user).build();

        imagePostRepository.save(imagePost);
    }
}
