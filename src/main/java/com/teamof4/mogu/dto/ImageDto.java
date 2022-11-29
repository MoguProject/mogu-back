package com.teamof4.mogu.dto;

import com.teamof4.mogu.entity.Image;
import io.swagger.annotations.ApiModelProperty;
import io.swagger.annotations.ApiParam;
import lombok.*;

import javax.validation.constraints.NotBlank;

@Getter
public class ImageDto {

    @NotBlank(message = "이미지 URL이 비어있습니다")
    private String imageUrl;

    public static Image of(String imageUrl) {
        return Image.builder()
                .imageUrl(imageUrl)
                .build();
    }
}
