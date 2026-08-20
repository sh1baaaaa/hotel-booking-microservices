package com.shiba.user.kafka;

import com.shiba.event.EventType;
import com.shiba.event.UserEvent;
import com.shiba.user.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@RequiredArgsConstructor
@Component
public class UserEventConsumer {

    private final UserService userService;
    private final KafkaTemplate<String, UserEvent> kafkaTemplate;

    @KafkaListener(topics = "user-events", groupId = "user-service-group")
    public void consume(UserEvent event) {
        log.info("Received event: type={}, userId={}", event.getEventType(), event.getUserId());

        if (event.getEventType() == EventType.USER_REGISTERED) {
            handleRegistration(event);
        }
    }

    private void handleRegistration(UserEvent event) {
        try {
            userService.createFromEvent(event);

            UserEvent successEvent = UserEvent.newBuilder()
                    .setUserId(event.getUserId())
                    .setEmail(event.getEmail())
                    .setFirstName(event.getFirstName())
                    .setLastName(event.getLastName())
                    .setEventType(EventType.USER_CREATED)
                    .build();

            kafkaTemplate.send("user-events", event.getUserId(), successEvent);
            log.info("SAGA: published USER_CREATED for userId={}", event.getUserId());

        } catch (Exception e) {
            UserEvent failEvent = UserEvent.newBuilder()
                    .setUserId(event.getUserId())
                    .setEmail(event.getEmail())
                    .setFirstName(event.getFirstName())
                    .setLastName(event.getLastName())
                    .setEventType(EventType.USER_CREATION_FAILED)
                    .setErrorReason(e.getMessage())
                    .build();

            kafkaTemplate.send("user-events", event.getUserId(), failEvent);
            log.error("SAGA: published USER_CREATION_FAILED for userId={}", event.getUserId(), e);
        }
    }
}
