package com.teamof4.mogu.exception;

import com.teamof4.mogu.exception.user.DuplicatedEmailException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import static com.teamof4.mogu.constants.ResponseConstants.DUPLICATED_EMAIL;

@Slf4j
@RestControllerAdvice
public class MoguExceptionHandler {

    @ExceptionHandler(DuplicatedEmailException.class)
    public final ResponseEntity<String> handleDuplicatedEmailException(
            DuplicatedEmailException exception) {
        log.debug("중복된 이메일입니다", exception);
        return DUPLICATED_EMAIL;
    }
}