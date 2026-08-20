package com.shiba.auth.kafka;

import com.shiba.auth.data.repository.CredentialRepository;
import com.shiba.event.EventType;
import com.shiba.event.UserEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@Component
public class AuthSagaConsumer {

    private final CredentialRepository credentialRepository;

    @Transactional
    @KafkaListener(topics = "user-events", groupId = "auth-service-group")
    public void handleUserEvent(UserEvent event) {
        if (event.getEventType() == EventType.USER_CREATION_FAILED) {
            UUID userId = UUID.fromString(event.getUserId());

            log.warn("SAGA COMPENSATION: user-service failed to create profile for userId={}. Reason: {}",
                    userId, event.getErrorReason());

            credentialRepository.deleteByUserId(userId);

            log.warn("SAGA COMPENSATION: deleted credential for userId={}", userId);
        }
    }

}
