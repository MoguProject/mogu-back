package com.teamof4.mogu.service;

import com.teamof4.mogu.dto.UserDto.*;
import com.teamof4.mogu.dto.UserDto.LoginRequest;
import com.teamof4.mogu.dto.UserDto.LoginResponse;
import com.teamof4.mogu.dto.UserDto.SaveRequest;
import com.teamof4.mogu.entity.Skill;
import com.teamof4.mogu.entity.User;
import com.teamof4.mogu.entity.UserSkill;
import com.teamof4.mogu.exception.AlreadyMyNicknameException;
import com.teamof4.mogu.exception.AlreadyMyPasswordException;
import com.teamof4.mogu.exception.AlreadyMyPhoneException;
import com.teamof4.mogu.exception.user.*;
import com.teamof4.mogu.repository.SkillRepository;
import com.teamof4.mogu.repository.UserRepository;
import com.teamof4.mogu.repository.UserSkillRepository;
import com.teamof4.mogu.security.TokenProvider;
import com.teamof4.mogu.util.certification.EmailService;
import com.teamof4.mogu.util.encryption.EncryptionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private final ImageService imageService;
    private final UserRepository userRepository;
    private final UserSkillRepository userSkillRepository;
    private final SkillRepository skillRepository;
    private final EncryptionService encryptionService;
    private final EmailService emailService;
    private final TokenProvider tokenProvider;

    @Transactional
    public void save(SaveRequest requestDto, MultipartFile profileImage) {
        checkDuplicatedForCreate(requestDto);

        requestDto.encryptPassword(encryptionService);
        User user = requestDto.toEntity();
        user.setImage(imageService.saveProfileImage(profileImage));

        userRepository.save(user);
    }


    @Transactional(readOnly = true)
    public void checkDuplicatedForCreate(SaveRequest requestDto) {
        if (userRepository.existsByEmail(requestDto.getEmail())) {
            throw new DuplicatedEmailException();
        }

        if (userRepository.existsByNickname(requestDto.getNickname())) {
            throw new DuplicatedNicknameException();
        }

        if (userRepository.existsByPhone(requestDto.getPhone())) {
            throw new DuplicatedPhoneException();
        }
    }

    @Transactional
    public LoginResponse login(LoginRequest loginRequest) {
        User user = userRepository.findByEmail(loginRequest.getEmail())
                .orElseThrow(() -> new UserNotFoundException("존재하지 않는 사용자 입니다."));

        if (user.getIsDeleted()) {
            throw new UserNotFoundException("탈퇴한 회원입니다.");
        }

        if (!loginRequest.checkPassword(encryptionService, user.getPassword())) {
            throw new UserNotFoundException("이메일 또는 비밀번호가 일치하지 않습니다.");
        }

        String token = tokenProvider.create(user);
        LoginResponse loginResponse = user.toLoginResponse();
        loginResponse.createToken(token);

        return loginResponse;
    }

    @Transactional(readOnly = true)
    public UserInfoResponse getMyPageInformation(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("존재하지 않는 사용자 입니다."));

        UserInfoResponse userInfoResponse = user.toUserInfoResponse();

        return userInfoResponse;
    }

    @Transactional
    public void update(UpdateRequest updateRequest, MultipartFile profileImage, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("존재하지 않는 사용자 입니다."));

        checkDuplicatedForUpdate(user, updateRequest);
        updateRequest.encryptPassword(encryptionService);
        updateSkills(user, updateRequest);
        user = updateRequest.toEntity();
        //profileImage가 null이면 수정 X
        if (!profileImage.isEmpty()) {
            user.setImage(imageService.updateProfileImage(profileImage));
        }
        userRepository.save(user);
    }

    @Transactional(readOnly = true)
    public void checkDuplicatedForUpdate(User user, UpdateRequest requestDto) {
        if (user.getNickname().equals(requestDto.getNickname())) {
            throw new AlreadyMyNicknameException();
        }
        if (user.getPhone().equals(requestDto.getPhone())) {
            throw new AlreadyMyPhoneException();
        }
        if (requestDto.checkPassword(encryptionService, user.getPassword())) {
            throw new AlreadyMyPasswordException();
        }
        if (userRepository.existsByNickname(requestDto.getNickname())) {
            throw new DuplicatedNicknameException();
        }

        if (userRepository.existsByPhone(requestDto.getPhone())) {
            throw new DuplicatedPhoneException();
        }
    }

    @Transactional
    public void updateSkills(User user, UpdateRequest updateRequest) {
        List<UserSkill> originalUserSkills = user.getUserSkills();
        List<String> updatingSkillNames = updateRequest.getSkills();
        originalUserSkills
                .stream()
                .filter(original -> updatingSkillNames.stream().noneMatch(updating -> original.getSkill().getSkillName().equals(updating)))
                .forEach(userSkill -> userSkillRepository.delete(userSkill));
        updatingSkillNames
                .stream()
                .filter(updating -> user.getUserSkillNames().stream().noneMatch(original -> updating.equals(original)))
                .map(skillName -> skillRepository.findBySkillName(skillName))
                .forEach(skill -> userSkillRepository.save(UserSkill.of(user, skill)));
    }

    @Transactional
    public void delete(DeleteRequest requestDto, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("존재하지 않는 사용자 입니다."));

        if (!requestDto.checkPassword(encryptionService, user.getPassword())) {
            throw new WrongPasswordException();
        }

        user.deleteUser();
        userRepository.save(user);
        imageService.deleteProfileImage(user.getImage());
    }

    public String certificateByEmail(EmailCertificationRequest requestDto) {
        return emailService.sendCertificationEmail(requestDto.getEmail());
    }
}