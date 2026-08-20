package com.shiba.user.service;

import com.shiba.event.EventType;
import com.shiba.event.UserEvent;
import com.shiba.user.data.dto.UserResponseDTO;
import com.shiba.user.data.dto.UserUpdateRequestDTO;
import com.shiba.user.data.entity.UserEntity;
import com.shiba.user.data.mapper.UserMapper;
import com.shiba.user.data.repository.UserRepository;
import com.shiba.user.exception.UserNotFoundException;
import com.shiba.user.kafka.UserEventProducer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@Service
public class UserService {

    private final UserEventProducer userEventProducer;
    private final UserRepository userRepository;
    private final UserMapper userMapper;

    public void createFromEvent(UserEvent event) {

        if(userRepository.existsByIdOrEmailOrUsername(UUID.fromString(event.getUserId()),
                event.getEmail(), event.getUsername())) {
            log.warn("User already exists: {}", event.getUsername());
            return;
        }

        UserEntity userEntity = UserEntity.builder()
                .id(UUID.fromString(event.getUserId()))
                .username(event.getUsername())
                .email(event.getEmail())
                .firstName(event.getFirstName())
                .lastName(event.getLastName())
                .roles(List.of(UserEntity.UserRole.ROLE_USER))
                .build();

        userRepository.save(userEntity);
        log.info("User profile created from event: {}", event.getUsername());
    }

    @Transactional
    public UserResponseDTO updateById(UUID id, UserUpdateRequestDTO request) {
        UserEntity userEntity = userRepository.findById(id).orElseThrow(() -> new UserNotFoundException(id));
        userEntity = userMapper.updateEntityFromRequestDTO(request, userEntity);

        userRepository.save(userEntity);
        userEventProducer.publishUserUpdated(UserEvent.newBuilder()
                .setUserId(id.toString())
                        .setEmail(userEntity.getEmail())
                        .setFirstName(userEntity.getFirstName())
                        .setLastName(userEntity.getLastName())
                        .setUsername(userEntity.getUsername())
                        .setEventType(EventType.USER_UPDATED)
                .build()
        );
        return userMapper.toResponseDTO(userEntity);
    }

    @Transactional
    public Boolean deleteById(UUID id) {
        UserEntity userEntity = userRepository.findById(id).orElse(null);
        if (userEntity == null) {
            return false;
        }
        userRepository.delete(userEntity);

        userEventProducer.publishUserDeleted(UserEvent.newBuilder()
                .setUserId(id.toString())
                .setEmail(userEntity.getEmail())
                .setFirstName(userEntity.getFirstName())
                .setLastName(userEntity.getLastName())
                .setUsername(userEntity.getUsername())
                .setEventType(EventType.USER_UPDATED)
                .build()
        );
        return true;
    }

    public UserResponseDTO findById(UUID id) {
        UserEntity userEntity = userRepository.findById(id).orElseThrow(() -> new UserNotFoundException(id));
        return userMapper.toResponseDTO(userEntity);
    }

}
