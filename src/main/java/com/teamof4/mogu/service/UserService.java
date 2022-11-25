package com.teamof4.mogu.service;

import com.teamof4.mogu.dto.UserDto;
import com.teamof4.mogu.dto.UserDto.LoginRequest;
import com.teamof4.mogu.dto.UserDto.LoginResponse;
import com.teamof4.mogu.dto.UserDto.SaveRequest;
import com.teamof4.mogu.entity.User;
import com.teamof4.mogu.exception.user.UserNotFoundException;
import com.teamof4.mogu.exception.user.DuplicatedEmailException;
import com.teamof4.mogu.exception.user.DuplicatedNicknameException;
import com.teamof4.mogu.exception.user.DuplicatedPhoneException;
import com.teamof4.mogu.repository.UserRepository;
import com.teamof4.mogu.security.TokenProvider;
import com.teamof4.mogu.util.encryption.EncryptionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final ImageService imageService;
    private final EncryptionService encryptionService;

    private final TokenProvider tokenProvider;

    @Transactional
    public void save(SaveRequest requestDto, MultipartFile profileImage) {
        if (!checkDuplicated(requestDto)) {
            requestDto.encryptPassword(encryptionService);

            User user = requestDto.toEntity();
            user.setImage(imageService.saveProfileImage(profileImage));

            userRepository.save(user);
        }
    }

    public boolean checkDuplicated(SaveRequest requestDto) {
        if (userRepository.existsByEmail(requestDto.getEmail())) {
            throw new DuplicatedEmailException();
        }

        if (userRepository.existsByNickname(requestDto.getNickname())) {
            throw new DuplicatedNicknameException();
        }

        if (userRepository.existsByPhone(requestDto.getPhone())) {
            throw new DuplicatedPhoneException();
        }

        return false;
    }

    public LoginResponse login(LoginRequest loginRequest) {
        User user = userRepository.findByEmail(loginRequest.getEmail())
                .orElseThrow(() -> new UserNotFoundException("존재하지 않는 사용자 입니다."));


        if (!loginRequest.checkPassword(encryptionService, user.getPassword())) {
            throw new UserNotFoundException("이메일 또는 비밀번호가 일치하지 않습니다.");
        }

        String token = tokenProvider.create(user);
        LoginResponse loginResponse = user.toLoginResponse();
        loginResponse.createToken(token);

        return loginResponse;
    }
}
