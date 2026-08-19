package com.shiba.auth.service;

import com.shiba.auth.data.dto.AuthResponseDTO;
import com.shiba.auth.data.dto.LoginRequestDTO;
import com.shiba.auth.data.dto.RegisterRequestDTO;
import com.shiba.auth.data.entity.CredentialEntity;
import com.shiba.auth.data.repository.CredentialRepository;
import com.shiba.auth.exception.AuthException;
import com.shiba.auth.kafka.AuthEventProducer;
import com.shiba.event.EventType;
import com.shiba.event.UserEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@Service
public class AuthService {

    private final JwtService jwtService;
    private final CredentialRepository credentialRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthEventProducer authEventProducer;

    @Cacheable(value = "tokens", key = "#request.username")
    public AuthResponseDTO login(LoginRequestDTO request) {
        log.info("Attempt to login. Username: {}", request.getUsername());

        CredentialEntity credentialEntity
                = credentialRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new AuthException("User is not found"));

        if (!passwordEncoder.matches(request.getPassword(), credentialEntity.getPasswordHash())) {
            log.error("Password Mismatch. Username: {}", request.getUsername());
            throw new AuthException("Wrong password");
        }

        return AuthResponseDTO.builder()
                .token(jwtService.generateToken(
                        credentialEntity.getUserId().toString(),
                        credentialEntity.getUsername(),
                        credentialEntity.getRole())
                )
                .userId(credentialEntity.getUserId().toString())
                .build();
    }

    @Transactional
    public AuthResponseDTO register(RegisterRequestDTO request) {
        log.info("Attempt to register. Username: {}", request.getUsername());

        if (credentialRepository.existsByUsername(request.getUsername())) {
            throw new AuthException("Username is already in use");
        }

        CredentialEntity credentialEntity = CredentialEntity.builder()
                .id(UUID.randomUUID())
                .userId(UUID.randomUUID())
                .username(request.getUsername())
                .passwordHash(passwordEncoder.encode(request.getPassword()))
                .role("USER")
                .build();

        credentialRepository.save(credentialEntity);

        UserEvent userEvent = UserEvent.newBuilder()
                .setUserId(credentialEntity.getUserId().toString())
                .setEventType(EventType.USER_REGISTERED)
                .setEmail(request.getEmail())
                .setUsername(request.getUsername())
                .setFirstName(request.getFirstName())
                .setLastName(request.getLastName())
                .build();

        authEventProducer.publishUserRegistered(userEvent);

        return AuthResponseDTO.builder()
                .token(jwtService.generateToken(
                        credentialEntity.getUserId().toString(),
                        credentialEntity.getUsername(),
                        credentialEntity.getRole()
                ))
                .userId(credentialEntity.getUserId().toString())
                .build();
    }
}
