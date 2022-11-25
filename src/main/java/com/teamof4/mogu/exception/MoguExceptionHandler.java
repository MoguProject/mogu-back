package com.teamof4.mogu.exception;

import com.teamof4.mogu.exception.image.FailedImageUploadException;
import com.teamof4.mogu.exception.image.FailedImageConvertException;
import com.teamof4.mogu.exception.user.DuplicatedEmailException;
import com.teamof4.mogu.exception.user.DuplicatedNicknameException;
import com.teamof4.mogu.exception.user.DuplicatedPhoneException;
import com.teamof4.mogu.exception.user.UserNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import static com.teamof4.mogu.constants.ResponseConstants.*;

@Slf4j
@RestControllerAdvice
public class MoguExceptionHandler {

    @ExceptionHandler(DuplicatedEmailException.class)
    public final ResponseEntity<String> handleDuplicatedEmailException(
            DuplicatedEmailException exception) {
        log.debug("중복된 이메일입니다", exception);
        return DUPLICATED_EMAIL;
    }

    @ExceptionHandler(DuplicatedNicknameException.class)
    public final ResponseEntity<String> handleDuplicatedNicknameException(
            DuplicatedNicknameException exception) {
        log.debug("중복된 닉네임입니다.", exception);
        return DUPLICATED_NICKNAME;
    }

    @ExceptionHandler(DuplicatedPhoneException.class)
    public final ResponseEntity<String> handleDuplicatedPhoneException(
            DuplicatedPhoneException exception) {
        log.debug("중복된 휴대폰 번호입니다.", exception);
        return DUPLICATED_PHONE;
    }

    @ExceptionHandler(UserNotFoundException.class)
    public final ResponseEntity<String> handleUserNotFoundException(
            UserNotFoundException exception) {
        return USER_NOT_FOUND;
    }

    @ExceptionHandler(FailedImageConvertException.class)
    public final ResponseEntity<String> handleFailedToConvertImageException(
            FailedImageConvertException exception) {
        log.debug("이미지 파일 변환에 실패했습니다.", exception);
        return FAILED_IMAGE_CONVERT;
    }

    @ExceptionHandler(FailedImageUploadException.class)
    public final ResponseEntity<String> handleFailedToImageUploadException(
            FailedImageUploadException exception) {
        log.debug("이미지 업로드에 실패했습니다.", exception);
        return FAILED_IMAGE_UPLOAD;
    }

}