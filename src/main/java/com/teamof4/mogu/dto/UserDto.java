package com.teamof4.mogu.dto;

import com.teamof4.mogu.entity.User;
import com.teamof4.mogu.util.encryption.EncryptionService;
import io.swagger.annotations.ApiModelProperty;
import io.swagger.annotations.ApiParam;
import lombok.*;

import javax.validation.constraints.*;
import java.util.List;

import static com.teamof4.mogu.constants.RegexConstants.PASSWORD;
import static com.teamof4.mogu.constants.RegexConstants.PHONE;

public class UserDto {

    @Getter
    @Builder
    @AllArgsConstructor
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

    @Builder
    @Getter
    public static class LoginRequest {

        @NotBlank(message = "이메일 주소를 입력해주세요")
        @ApiParam(value = "회원 이메일")
        private String email;

        @NotBlank(message = "비밀번호를 입력해주세요")
        @ApiParam(value = "회원 비밀번호")
        private String password;

        public boolean checkPassword(EncryptionService encryptionService, String encryptedPassword) {
            return encryptionService.isSamePassword(this.password, encryptedPassword);
        }
    }

    @Builder
    @AllArgsConstructor
    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    @Getter
    public static class LoginResponse {

        @ApiModelProperty(notes = "로그인 인증 토큰")
        private String token;

        public void createToken(String token) {
            this.token = token;
        }
    }

    @Getter
    @Builder
    public static class LoginInfoResponse {
        @ApiModelProperty(notes = "로그인 유저 닉네임")
        private String nickname;

        @ApiModelProperty(notes = "로그인 유저 프로필 이미지 주소")
        private String profileImageUrl;
    }

    @Getter
    @Builder
    public static class UserInfoResponse {

        @ApiModelProperty(notes = "회원 프로필 이미지 URL")
        private String profileImageUrl;

        @ApiModelProperty(notes = "회원 이메일 주소")
        private String email;

        @ApiModelProperty(notes = "회원 이름")
        private String name;

        @ApiModelProperty(notes = "회원 닉네임")
        private String nickname;

        @ApiModelProperty(notes = "회원 휴대폰 번호")
        private String phone;

        @ApiModelProperty(notes = "회원정보 공개 여부")
        private boolean isActivated;

        @ApiModelProperty(notes = "회원 선호 진행방식")
        private String preferredMethod;

        @ApiModelProperty(notes = "회원 선호 지역")
        private String region;

        @ApiModelProperty(notes = "회원 자기소개")
        private String information;

        @ApiModelProperty(notes = "회원 기술스택")
        private List<String> skills;
    }

    @ToString
    @Getter
    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class UpdateRequest {

        @ApiParam(value = "회원 닉네임")
        @NotBlank(message = "닉네임을 입력해주세요")
        @Size(min = 2, max = 12, message = "닉네임은 3자 이상 12자 이하로 입력해주세요")
        private String nickname;

        @ApiParam(value = "회원 휴대폰 번호")
        @NotBlank(message = "휴대폰 번호를 입력해주세요")
        @Pattern(regexp = PHONE, message = "올바른 휴대폰번호를 입력해주세요")
        private String phone;

        @ApiParam(value = "회원정보 공개여부")
        @NotNull
        private Boolean isActivated;

        @ApiParam(value = "회원 선호 진행방식")
        @NotBlank
        private String preferredMethod;

        @ApiParam(value = "회원 선호 지역")
        @NotBlank
        private String region;

        @ApiParam(value = "회원 자기소개")
        @NotBlank
        private String information;

        @ApiParam(value = "회원 기술스택")
        @NotNull
        private List<String> skills;
    }

    @Getter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class UpdatePasswordRequest {
        @ApiParam(value = "기존 회원 비밀번호")
        @NotBlank(message = "기존 비밀번호를 입력해주세요")
        @Size(min = 8, max = 20, message = "비밀번호는 8자 이상 20자 이하로 입력해주세요")
        @Pattern(regexp = PASSWORD, message = "숫자, 문자, 특수문자 3가지를 조합해 입력해주세요")
        private String currentPassword;

        @ApiParam(value = "변경할 회원 비밀번호")
        @NotBlank(message = "기존 비밀번호를 입력해주세요")
        @Size(min = 8, max = 20, message = "비밀번호는 8자 이상 20자 이하로 입력해주세요")
        @Pattern(regexp = PASSWORD, message = "숫자, 문자, 특수문자 3가지를 조합해 입력해주세요")
        private String newPassword;

        public boolean checkPassword(EncryptionService encryptionService, String encryptedPassword) {
            return encryptionService.isSamePassword(this.currentPassword, encryptedPassword);
        }

        public boolean isAlreadyMyPassword() {
            return currentPassword.equals(newPassword);
        }

        public void encryptPassword(EncryptionService encryptionService) {
            this.newPassword = encryptionService.encrypt(newPassword);
        }
    }

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DeleteRequest {
        @ApiParam(value = "회원 비밀번호")
        @NotBlank(message = "비밀번호를 입력해주세요")
        private String password;

        public boolean checkPassword(EncryptionService encryptionService, String encryptedPassword) {
            return encryptionService.isSamePassword(this.password, encryptedPassword);
        }
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class EmailCertificationRequest {
        @ApiParam(value = "회원 메일 주소")
        @NotBlank(message = "이메일 주소를 입력해주세요")
        private String email;
    }

    @Getter
    @Builder
    public static class CreatePasswordRequest {
        @ApiParam(value = "가입한 이메일 주소")
        @NotBlank(message = "이메일 주소를 입력해주세요")
        private String email;

        @ApiParam(value = "본인 이름")
        @NotBlank(message = "이름을 입력해주세요")
        private String name;
    }

    public static String encryptPassword(EncryptionService encryptionService, String password) {
        return encryptionService.encrypt(password);
    }
}