package com.teamof4.mogu.dto;

import com.teamof4.mogu.entity.User;
import com.teamof4.mogu.util.encryption.EncryptionService;
import lombok.*;
import io.swagger.annotations.ApiModelProperty;
import io.swagger.annotations.ApiParam;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.*;

import java.util.List;

import static com.teamof4.mogu.constants.RegexConstants.PASSWORD;
import static com.teamof4.mogu.constants.RegexConstants.PHONE;

public class UserDto {

    @Getter
    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class SaveRequest {

        @ApiParam(value = "회원 이메일")
        @NotBlank(message = "이메일 주소를 입력해주세요")
        @Email(message = "이메일 형식에 맞춰 입력해주세요")
        private String email;

        @ApiParam(value = "회원 비밀번호")
        @NotBlank(message = "비밀번호를 입력해주세요")
        @Size(min = 8, max = 20, message = "비밀번호는 8자 이상 20자 이하로 입력해주세요")
        @Pattern(regexp = PASSWORD, message = "숫자, 문자, 특수문자 중 2가지를 조합해 입력해주세요")
        private String password;

        @ApiParam(value = "회원 닉네임")
        @NotBlank(message = "닉네임을 입력해주세요")
        @Size(min = 3, max = 12, message = "닉네임은 3자 이상 12자 이하로 입력해주세요")
        private String nickname;

        @ApiParam(value = "회원 이름")
        @NotBlank(message = "이름을 입력해주세요")
        @Size(min = 2, message = "2자 이상 올바른 이름을 입력해주세요")
        private String name;

        @ApiParam(value = "회원 휴대폰 번호")
        @NotBlank(message = "휴대폰 번호를 입력해주세요")
        @Pattern(regexp = PHONE, message = "올바른 휴대폰번호를 입력해주세요")
        private String phone;


        //userDTo의 비밀번호를 BCrypt방식으로 암호화
        public void encryptPassword(EncryptionService encryptionService) {
            this.password = encryptionService.encrypt(password);
        }

        public User toEntity() {
            return User.builder()
                    .email(this.email)
                    .name(this.name)
                    .nickname(this.nickname)
                    .password(this.password)
                    .phone(this.phone)
                    .preferredMethod("NOTENTERED")
                    .region("NOTENTERED")
                    .information("NOTENTERED")
                    .isActivated(true)
                    .isDeleted(false)
                    .build();
        }

    }

    @ToString
    @Getter
    public static class LoginRequest {

        @ApiParam(value = "회원 이메일")
        private String email;

        @ApiParam(value = "회원 비밀번호")
        private String password;

        public boolean checkPassword(EncryptionService encryptionService, String encryptedPassword) {
            return encryptionService.isSamePassword(this.password, encryptedPassword);
        }
    }

    @Builder
    @Getter
    public static class LoginResponse {

        @ApiModelProperty(notes = "회원 닉네임")
        private String nickname;

        @ApiModelProperty(notes = "회원 프로필 이미지")
        private String profileImageUrl;

        @ApiModelProperty(notes = "로그인 인증 토큰")
        private String token;

        public void createToken(String token) {
            this.token = token;
        }
    }

    @Getter
    @Builder
    public static class UserInfoResponse {

        private String profileImageUrl;

        private String email;

        private String name;

        private String nickname;

        private String phone;

        private boolean isActivated;

        private String preferredMethod;

        private String region;

        private String information;

        private List<String> skills;

    }

    @Getter
    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class UpdateRequest {

        @NotBlank(message = "비밀번호를 입력해주세요")
        @Size(min = 8, max = 20, message = "비밀번호는 8자 이상 20자 이하로 입력해주세요")
        @Pattern(regexp = PASSWORD, message = "숫자, 문자, 특수문자 3가지를 조합해 입력해주세요")
        private String password;

        @NotBlank(message = "닉네임을 입력해주세요")
        @Size(min = 2, max = 12, message = "닉네임은 3자 이상 12자 이하로 입력해주세요")
        private String nickname;

        @NotBlank(message = "휴대폰 번호를 입력해주세요")
        @Pattern(regexp = PHONE, message = "올바른 휴대폰번호를 입력해주세요")
        private String phone;

        @NotBlank
        private boolean isActivated;

        @NotBlank
        private String preferredMethod;

        @NotBlank
        private String region;

        @NotBlank
        private String information;

        @NotBlank
        private List<String> skills;

        public User toEntity() {
            return User.builder()
                    .nickname(this.getNickname())
                    .password(this.getPassword())
                    .phone(this.getPhone())
                    .preferredMethod(this.getPreferredMethod())
                    .region(this.getRegion())
                    .information(this.getInformation())
                    .isActivated(this.isActivated)
                    .build();
        }

        public boolean checkPassword(EncryptionService encryptionService, String encryptedPassword) {
            return encryptionService.isSamePassword(this.password, encryptedPassword);
        }

        public void encryptPassword(EncryptionService encryptionService) {
            this.password = encryptionService.encrypt(password);
        }
    }

    @Getter
    public static class DeleteRequest {
        @NotBlank(message = "비밀번호를 입력해주세요")
        private String password;

        public boolean checkPassword(EncryptionService encryptionService, String encryptedPassword) {
            return encryptionService.isSamePassword(this.password, encryptedPassword);
        }
    }

    @Getter
    public static class EmailCertificationRequest {

        @NotBlank(message = "이메일 주소를 입력해주세요")
        private String email;
    }
}