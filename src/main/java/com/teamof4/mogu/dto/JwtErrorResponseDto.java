package com.teamof4.mogu.dto;

import lombok.Builder;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Builder
@Getter
public class JwtErrorResponseDto {

    private HttpStatus status;

    private String error;

    public static JwtErrorResponseDto of(HttpStatus status, String error) {
        return JwtErrorResponseDto.builder()
                .status(status)
                .error(error)
                .build();
    }

    public String convertToJson() {
        return "{\"status\":\""+this.status+"\",\"error\":\""+error+"\"}";
    }
}
