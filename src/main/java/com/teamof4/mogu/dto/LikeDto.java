package com.teamof4.mogu.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class LikeDto {

    @ApiModelProperty(notes = "좋아요 클릭 여부")
    private boolean likeStatus;

    @ApiModelProperty(notes = "좋아요 수")
    private int count;

}
