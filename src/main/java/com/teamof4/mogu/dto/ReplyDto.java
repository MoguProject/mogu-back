package com.teamof4.mogu.dto;

import com.teamof4.mogu.entity.Reply;
import io.swagger.annotations.ApiModelProperty;
import io.swagger.annotations.ApiParam;
import lombok.Builder;
import lombok.Getter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.List;

public class ReplyDto {

    @Getter
    @Builder
    public static class Request {

        @NotNull
        @ApiParam(value = "댓글 ID", required = true)
        private Long replyId;

        @NotBlank
        @ApiParam(value = "댓글 내용", required = true)
        private String content;
    }

    @Getter
    @Builder
    public static class SuperRequest {

        @NotNull
        @ApiParam(value = "게시글 ID", required = true)
        private Long postId;

        @NotBlank
        @ApiParam(value = "댓글 내용", required = true)
        private String content;
    }

    @Getter
    public static class Response {

        @ApiModelProperty(notes = "댓글 ID")
        private Long id;

        @ApiModelProperty(notes = "댓글 작성자 ID")
        private long userId;

        @ApiModelProperty(notes = "댓글 내용")
        private String content;

        @ApiModelProperty(notes = "대댓글 대상 작성자 ID")
        private String targetNickname;

        @ApiModelProperty(notes = "댓글 삭제 여부")
        private Boolean deleteStatus;

        @ApiModelProperty(notes = "댓글 작성 시간")
        private LocalDateTime createAt;

        @ApiModelProperty(notes = "댓글 수정 시간")
        private LocalDateTime updatedAt;

        @ApiModelProperty(notes = "대댓글 리스트")
        private List<Response> children;

        @Builder
        public Response(Reply reply, String targetNickname, List<Response> children) {
            this.id = reply.getId();
            this.userId = reply.getUser().getId();
            this.content = reply.getContent();
            this.createAt = reply.getCreatedAt();
            this.updatedAt = reply.getUpdatedAt();
            this.children = children;
            this.deleteStatus = reply.isDeleted();
            this.targetNickname = targetNickname;
        }
    }

}
