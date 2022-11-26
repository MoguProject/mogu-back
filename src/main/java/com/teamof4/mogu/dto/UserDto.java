package com.teamof4.mogu.dto;

import com.teamof4.mogu.entity.User;
import com.teamof4.mogu.util.encryption.EncryptionService;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.*;

import static com.teamof4.mogu.constants.RegexConstants.PASSWORD;
import static com.teamof4.mogu.constants.RegexConstants.PHONE;

public class UserDto {

    @Getter
    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class SaveRequest {

        @NotBlank(message = "이메일 주소를 입력해주세요")
        @Email(message = "이메일 형식에 맞춰 입력해주세요")
        private String email;

        @NotBlank(message = "비밀번호를 입력해주세요")
        @Size(min = 8, max = 20, message = "비밀번호는 8자 이상 20자 이하로 입력해주세요")
        @Pattern(regexp = PASSWORD, message = "숫자, 문자, 특수문자 중 2가지를 조합해 입력해주세요")
        private String password;

        @NotBlank(message = "닉네임을 입력해주세요")
        @Size(min = 3, max = 12, message = "닉네임은 3자 이상 12자 이하로 입력해주세요")
        private String nickname;

        @NotBlank(message = "이름을 입력해주세요")
        @Size(min = 2, message = "2자 이상 올바른 이름을 입력해주세요")
        private String name;

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

    @Getter
    public static class LoginRequest {

        private String email;

        private String password;

        public boolean checkPassword(EncryptionService encryptionService, String encryptedPassword) {
            return encryptionService.isSamePassword(this.password, encryptedPassword);
        }
    }

    @Builder
    @Getter
    public static class LoginResponse {

        private String nickname;

        private String profileImageUrl;

        private String token;

        public void createToken(String token) {
            this.token = token;
        }
    }
}