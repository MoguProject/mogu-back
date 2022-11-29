package com.teamof4.mogu.dto;

import com.teamof4.mogu.entity.*;
import io.swagger.annotations.ApiModelProperty;
import io.swagger.annotations.ApiParam;
import lombok.Builder;
import lombok.Getter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public class ProjectStudyDto {

    @Getter
    public static class Request {

        @NotBlank(message = "진행 방식을 선택해 주세요.")
        @ApiParam(value = "진행 방식", required = true)
        private String preferredMethod;

        @NotBlank(message = "지역을 선택해 주세요.")
        @ApiParam(value = "지역", required = true)
        private String region;

        @NotBlank(message = "진행 기간을 선택해 주세요.")
        @ApiParam(value = "진행 기간", required = true)
        private String period;

        @NotNull(message = "모집 인원을 선택해 주세요.")
        @ApiParam(value = "모집 인원", required = true)
        private int memberCount;

        @NotEmpty(message = "기술 스택을 선택해 주세요.")
        @ApiParam(value = "기술 스택", required = true)
        private List<Skill> skills;

        @NotNull(message = "시작 예정일을 선택해 주세요.")
        @ApiParam(value = "시작 예정일", required = true)
        private LocalDate startAt;

        @NotNull
        @ApiParam(value = "모집 여부")
        private boolean openStatus;

        @Builder
        public ProjectStudy toEntity(Post post) {
            return ProjectStudy.builder()
                    .post(post)
                    .preferredMethod(preferredMethod)
                    .region(region)
                    .period(period)
                    .memberCount(memberCount)
                    .startAt(startAt).build();
        }

    }

    @Getter
    public static class Response {

        @ApiModelProperty(notes = "게시글 ID")
        private Long postId;

        @ApiModelProperty(notes = "작성목 ID")
        private Long userId;

        @ApiModelProperty(notes = "게시글 ID")
        private Long categoryId;

        @ApiModelProperty(notes = "작성자 닉네임")
        private String userNickname;

        @ApiModelProperty(notes = "카테고리 이름")
        private String categoryName;

        @ApiModelProperty(notes = "게시글 제목")
        private String title;

        @ApiModelProperty(notes = "게시글 내용")
        private String content;

        @ApiModelProperty(notes = "진행 방식")
        private String preferredMethod;

        @ApiModelProperty(notes = "지역")
        private String region;

        @ApiModelProperty(notes = "진행 기간")
        private String period;

        @ApiModelProperty(notes = "모집 인원")
        private int memberCount;

        @ApiModelProperty(notes = "조회수")
        private int view;

        @ApiModelProperty(notes = "모집 완료 여부")
        private boolean openStatus;

        // filter 조건으로 사용하기 위해 필요
        @ApiModelProperty(notes = "게시글 삭제 여부")
        private boolean deleteStatus;

        @ApiModelProperty(notes = "기술 스택 리스트")
        private List<PostSkill> postSkills;

        @ApiModelProperty(notes = "게시글 이미지 리스트")
        private List<Image> imageList;

        @ApiModelProperty(notes = "시작 예정일")
        private LocalDate startAt;

        @ApiModelProperty(notes = "게시글 생성일")
        private LocalDateTime createAt;

        @ApiModelProperty(notes = "게시글 생성일")
        private LocalDateTime updateAt;

        @Builder
        public Response(Post post, ProjectStudy projectStudy, List<Image> images) {

            this.postId = post.getId();
            this.userId = post.getUser().getId();
            this.categoryId = post.getCategory().getId();
            this.userNickname = post.getUser().getNickname();
            this.categoryName = post.getCategory().getCategoryName();
            this.title = post.getTitle();
            this.content = post.getContent();
            this.view = post.getView();
            this.preferredMethod = projectStudy.getPreferredMethod();
            this.region = projectStudy.getRegion();
            this.period = projectStudy.getPeriod();
            this.memberCount = projectStudy.getMemberCount();
            this.deleteStatus = post.isDeleted();
            this.openStatus = projectStudy.isOpenStatus();
            this.postSkills = projectStudy.getPostSkills();
            this.imageList = images;
            this.startAt = projectStudy.getStartAt();
            this.createAt = post.getCreatedAt();
            this.updateAt = post.getUpdatedAt();

        }
    }
}
