package com.teamof4.mogu.dto;

import com.teamof4.mogu.entity.*;
import io.swagger.annotations.ApiModelProperty;
import io.swagger.annotations.ApiParam;
import lombok.Builder;
import lombok.Getter;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;
import java.util.List;

public class PostDto {

    @Getter
    @Builder
    public static class SaveRequest {

        @ApiParam(value = "작성자 ID", required = true)
        private Long userId;

        @ApiParam(value = "카테고리 ID", required = true)
        @NotBlank(message = "카테고리를 선택해 주세요.")
        private Long categoryId;

        @ApiParam(value = "게시글 제목", required = true)
        @NotBlank(message = "제목을 입력해 주세요.")
        private String title;

        @ApiParam(value = "게시글 내용", required = true)
        @NotBlank(message = "내용을 입력해 주세요.")
        private String content;

        @ApiParam(value = "게시글 이미지 리스트")
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

        @ApiParam(value = "글 제목", required = true)
        @NotBlank(message = "제목을 입력해 주세요.")
        private String title;

        @ApiParam(value = "글 내용", required = true)
        @NotBlank(message = "내용을 입력해 주세요.")
        private String content;

        @ApiParam(value = "게시글 이미지 리스트")
        private List<MultipartFile> multipartFiles;

    }

    @Getter
    public static class Response {

        @ApiModelProperty(notes = "게시글 ID")
        private Long id;

        @ApiModelProperty(notes = "작성자 ID")
        private Long userId;

        @ApiModelProperty(notes = "카테고리 ID")
        private Long categoryId;

        @ApiModelProperty(notes = "작성자 닉네임")
        private String userNickname;

        @ApiModelProperty(notes = "카테고리 이름")
        private String categoryName;

        @ApiModelProperty(notes = "게시글 내용")
        private String title;

        @ApiModelProperty(notes = "게시글 내용")
        private String content;

        @ApiModelProperty(notes = "조회수")
        private int view;

        @ApiModelProperty(notes = "게시글 이미지 리스트")
        private List<Image> imageList;

        @ApiModelProperty(notes = "게시글 생성일")
        private LocalDateTime createAt;

        @ApiModelProperty(notes = "게시글 수정일")
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
}
