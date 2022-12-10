package com.teamof4.mogu.dto;

import com.teamof4.mogu.entity.Category;
import com.teamof4.mogu.entity.Post;
import com.teamof4.mogu.entity.User;
import io.swagger.annotations.ApiModelProperty;
import io.swagger.annotations.ApiParam;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.List;

public class PostDto {

    @Getter
    @Builder
    public static class SaveRequest {

        @ApiParam(value = "카테고리 ID", required = true)
        @NotNull(message = "카테고리를 선택해 주세요.")
        private Long categoryId;

        @ApiParam(value = "게시글 제목", required = true)
        @NotBlank(message = "제목을 입력해 주세요.")
        private String title;

        @ApiParam(value = "게시글 내용", required = true)
        @NotBlank(message = "내용을 입력해 주세요.")
        private String content;

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

        @ApiModelProperty(notes = "게시글 제목")
        private String title;

        @ApiModelProperty(notes = "게시글 내용")
        private String content;

        @ApiModelProperty(notes = "조회수")
        private int view;

        @ApiModelProperty(notes = "좋아요 수")
        private int likeCount;

        @ApiModelProperty(notes = "로그인 한 유저의 좋아요 여부")
        private boolean likeStatus;

        @ApiModelProperty(notes = "댓글 리스트")
        private List<ReplyDto.Response> replyList;

        @ApiModelProperty(notes = "게시글 생성 시간")
        private LocalDateTime createdAt;

        @ApiModelProperty(notes = "게시글 수정 시간")
        private LocalDateTime updatedAt;

        @Builder
        public Response(Post post, List<ReplyDto.Response> replies, boolean isLiked) {
            this.id = post.getId();
            this.userId = post.getUser().getId();
            this.categoryId = post.getCategory().getId();
            this.userNickname = post.getUser().getNickname();
            this.categoryName = post.getCategory().getCategoryName();
            this.title = post.getTitle();
            this.content = post.getContent();
            this.view = post.getView();
            this.likeCount = post.getLikes().size();
            this.likeStatus = isLiked;
            this.replyList = replies;
            this.createdAt = post.getCreatedAt();
            this.updatedAt = post.getUpdatedAt();
        }
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MyPageResponse {

        @ApiModelProperty(notes = "게시물 ID")
        private Long id;

        @ApiModelProperty(notes = "카테고리명")
        private String categoryName;

        @ApiModelProperty(notes = "제목")
        private String title;

        @ApiModelProperty(notes = "작성자 닉네임")
        private String nickname;

        @ApiModelProperty(notes = "좋아요 수")
        private int likeCount;

        @ApiModelProperty(notes = "조회수")
        private int view;

        @ApiModelProperty(notes = "나의 좋아요 여부")
        private boolean isLiked;

        @ApiModelProperty(notes = "작성일자")
        private LocalDateTime createdAt;
    }
}
