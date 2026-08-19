package com.shiba.auth.controller.advice;

import com.shiba.auth.data.dto.AuthResponseDTO;
import com.shiba.auth.exception.AuthException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class AuthExceptionHandler {

    @ExceptionHandler(AuthException.class)
    public ResponseEntity<AuthResponseDTO> handleException(AuthException e) {
        return new ResponseEntity<>(AuthResponseDTO.builder()
                .message(e.getMessage())
                .build()
        , HttpStatus.UNAUTHORIZED);
    }

}
