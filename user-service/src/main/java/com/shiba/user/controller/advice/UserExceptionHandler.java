package com.shiba.user.controller.advice;

import com.shiba.user.exception.UserNotFoundException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;

//TODO: I'll implement it later
public class UserExceptionHandler {

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<Boolean> handleUserNotFoundException(UserNotFoundException e) {
        return ResponseEntity.ok(false);
    }
}
