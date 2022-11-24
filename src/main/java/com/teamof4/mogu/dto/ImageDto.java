package com.teamof4.mogu.dto;

import com.teamof4.mogu.entity.Image;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

public class ImageDto {

    @Getter
    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class SaveRequest {

        @Builder
        public SaveRequest(String imageUrl) {
            this.imageUrl = imageUrl;
        }

        @NotBlank(message = "이미지 URL이 비어있습니다")
        private String imageUrl;

        public Image toEntity() {
            return Image.builder()
                    .imageUrl(this.imageUrl)
                    .build();
        }
    }

}
