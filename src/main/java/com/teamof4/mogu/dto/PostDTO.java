package com.teamof4.mogu.dto;

import com.teamof4.mogu.entity.*;
import lombok.Builder;
import lombok.Getter;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;
import java.util.List;

public class PostDTO {

    @Getter
    @Builder
    public static class SaveRequest {

        private Long userId;

        @NotBlank(message = "카테고리를 선택해 주세요.")
        private Long categoryId;

        @NotBlank(message = "제목을 입력해 주세요.")
        private String title;

        @NotBlank(message = "내용을 입력해 주세요.")
        private String content;

        private List<MultipartFile> multipartFiles;

        public Post toEntity(User user, Category category) {
            return Post.builder()
                    .user(user)
                    .category(category)
                    .title(title)
                    .content(content).build();
        }

    }

    @Getter
    @Builder
    public static class UpdateRequest {

        @NotBlank(message = "제목을 입력해 주세요.")
        private String title;

        @NotBlank(message = "내용을 입력해 주세요.")
        private String content;

        private List<MultipartFile> multipartFiles;

    }

    @Getter
    public static class Response {
        private Long id;
        private Long userId;
        private Long categoryId;
        private String userNickname;
        private String categoryName;
        private String title;
        private String content;
        private int view;

        private List<Image> imageList;
        private LocalDateTime createAt;
        private LocalDateTime updateAt;

        @Builder
        public Response(Post post, List<Image> images) {
            this.id = post.getId();
            this.userId = post.getUser().getId();
            this.categoryId = post.getCategory().getId();
            this.userNickname = post.getUser().getNickname();
            this.categoryName = post.getCategory().getCategoryName();
            this.title = post.getTitle();
            this.content = post.getContent();
            this.view = post.getView();
            this.imageList = images;
            this.createAt = post.getCreatedAt();
            this.updateAt = post.getUpdatedAt();
        }
    }

    @Getter
    public static class ListResponse {
        private Long id;
        private Long userId;
        private Long categoryId;
        private String userNickname;
        private String categoryName;
        private String title;
        private int view;
        private LocalDateTime createAt;
        private LocalDateTime updateAt;

        @Builder
        public ListResponse(Post post) {
            this.id = post.getId();
            this.userId = post.getUser().getId();
            this.categoryId = post.getCategory().getId();
            this.userNickname = post.getUser().getNickname();
            this.categoryName = post.getCategory().getCategoryName();
            this.title = post.getTitle();
            this.view = post.getView();
            this.createAt = post.getCreatedAt();
            this.updateAt = post.getUpdatedAt();
        }
    }
}
