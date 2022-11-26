package com.teamof4.mogu.dto;

import com.teamof4.mogu.entity.Image;
import lombok.*;

import javax.validation.constraints.NotBlank;

public class ImageDto {

    @Getter
    @AllArgsConstructor
    public static class SaveRequest {

        @NotBlank(message = "이미지 URL이 비어있습니다")
        private String imageUrl;

        public Image toEntity() {
            return Image.builder()
                    .imageUrl(this.imageUrl)
                    .build();
        }
    }

}
