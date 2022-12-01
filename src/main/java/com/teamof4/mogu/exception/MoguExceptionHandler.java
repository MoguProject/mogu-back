package com.teamof4.mogu.exception;

import com.teamof4.mogu.exception.image.FailedImageUploadException;
import com.teamof4.mogu.exception.image.FailedImageConvertException;
import com.teamof4.mogu.exception.image.ImageNotFoundException;
import com.teamof4.mogu.exception.user.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

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

    @ExceptionHandler(WrongPasswordException.class)
    public final ResponseEntity<String> handleWrongPasswordException(
            WrongPasswordException exception) {
        log.debug("잘못된 비밀번호입니다.");
        return WRONG_PASSWORD;
    }

    @ExceptionHandler(AlreadyMyPasswordException.class)
    public final ResponseEntity<String> handleAlreadyMyPasswordException(
            AlreadyMyPasswordException exception) {
        log.debug("이미 내가 사용중인 비밀번호입니다.");
        return ALREADY_MY_PASSWORD;
    }

    @ExceptionHandler(UserSkillNotFoundException.class)
    public final ResponseEntity<String> handleUserSkillNotFoundException(
            UserSkillNotFoundException exception) {
        log.debug("해당 유저의 해당 기술스택을 찾을 수 없습니다.");
        return USERSKILL_NOT_FOUND;
    }

    @ExceptionHandler(ImageNotFoundException.class)
    public final ResponseEntity<String> handleImageNotFoundException(
            ImageNotFoundException exception) {
        log.debug("이미지 파일을 찾는데 실패했습니다.", exception);
        return IMAGE_NOT_FOUND;
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

    @ExceptionHandler(UserNotLoginedException.class)
    public final ResponseEntity<String> handleUserNotLoginedException() {
        return NOT_LOGINED_USER;
    }

    @ExceptionHandler(BindException.class)
    public ResponseEntity<Map<String, String>> handleBindException(BindException exception) {
        Map<String, String> errors = new HashMap<>();
        exception.getBindingResult().getAllErrors()
                .forEach(e -> errors.put(((FieldError) e).getField(), e.getDefaultMessage()));
        log.debug("erros = {}", errors);
        return ResponseEntity.badRequest().body(errors);
    }

}