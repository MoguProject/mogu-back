package com.teamof4.mogu.dto;

import com.teamof4.mogu.entity.PreferredMethod;
import com.teamof4.mogu.entity.User;
import com.teamof4.mogu.util.encryption.EncryptionService;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import static com.teamof4.mogu.constants.RegexConstants.PHONE;

public class UserDto {

    @Getter
    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class SaveRequest {

        @NotBlank(message = "이메일 주소를 입력해주세요")
        @Email(message = "이메일 형식에 맞춰 입력해주세요")
        private String email;

        @NotBlank(message = "비밀번호를 입력해주세요")
        @Size(min = 8, max = 30, message = "비밀번호는 8자 이상 30자 이하로 입력해주세요")
        private String password;

        @NotBlank(message = "닉네임을 입력해주세요")
        @Size(min = 2, max = 12, message = "닉네임은 3자 이상 12자 이하로 입력해주세요")
        private String nickname;

        @NotBlank(message = "이름을 입력해주세요")
        @Size(min = 2, message = "2자 이상 올바른 이름을 입력해주세요")
        private String name;

        @NotBlank(message = "휴대폰 번호를 입력해주세요")
        @Pattern(regexp = PHONE, message = "올바른 휴대폰번호를 입력해주세요")
        private String phone;

        @NotBlank(message = "진행방식을 입력해주세요")
        private String preferredMethod;

        @NotBlank(message = "지역을 입력해주세요")
        private String region;

        @NotBlank(message = "자기소개를 입력해주세요")
        @Size(max = 200, message = "200자 이하로 입력해주세요")
        private String information;

        @NotBlank(message = "공개여부를 선택해주세요")
        private Boolean isActivated;

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
                    .preferredMethod(this.preferredMethod)
                    .region(this.region)
                    .isActivated(this.isActivated)
                    .isDeleted(false)
                    .build();
        }

    }

}