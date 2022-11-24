package com.teamof4.mogu.service;

import com.teamof4.mogu.dto.UserDto;
import com.teamof4.mogu.dto.UserDto.SaveRequest;
import com.teamof4.mogu.entity.User;
import com.teamof4.mogu.exception.user.DuplicatedEmailException;
import com.teamof4.mogu.exception.user.DuplicatedNicknameException;
import com.teamof4.mogu.exception.user.DuplicatedPhoneException;
import com.teamof4.mogu.repository.UserRepository;
import com.teamof4.mogu.util.aws.AwsS3Service;
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

    @Transactional
    public void save(SaveRequest requestDto, MultipartFile profileImage) {
        if(!checkDuplicated(requestDto)) {
            requestDto.encryptPassword(encryptionService);

            User user = requestDto.toEntity();
            user.setImage(imageService.saveProfileImage(profileImage));

            userRepository.save(user);
        }
    }

    public boolean checkDuplicated(SaveRequest requestDto) {
        if(userRepository.existByEmail(requestDto.getEmail())) {
            throw new DuplicatedEmailException();
        }

        if(userRepository.existByNickname(requestDto.getNickname())) {
            throw new DuplicatedNicknameException();
        }

        if(userRepository.existByPhone(requestDto.getPhone())) {
            throw new DuplicatedPhoneException();
        }

        return false;
    }
}
