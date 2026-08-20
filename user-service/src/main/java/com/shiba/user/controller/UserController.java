package com.shiba.user.controller;

import com.shiba.user.data.dto.UserResponseDTO;
import com.shiba.user.data.dto.UserUpdateRequestDTO;
import com.shiba.user.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RequiredArgsConstructor
@RequestMapping("/api/v1/user")
@RestController
public class UserController {

    private final UserService userService;

    @GetMapping("/data")
    public ResponseEntity<UserResponseDTO> getUser(@AuthenticationPrincipal UUID userId) {
        return ResponseEntity.ok(userService.findById(userId));
    }

    @PutMapping
    public ResponseEntity<UserResponseDTO> updateUser(@AuthenticationPrincipal UUID userId,
                                                      @Valid @RequestBody UserUpdateRequestDTO request) {
        return new ResponseEntity<>(userService.updateById(userId, request), HttpStatus.OK);
    }

    @DeleteMapping
    public ResponseEntity<Boolean> deleteUser(@AuthenticationPrincipal UUID userId) {
        return ResponseEntity.ok(userService.deleteById(userId));
    }
}
