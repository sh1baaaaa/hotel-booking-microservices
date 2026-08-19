package com.shiba.auth.kafka;

import com.shiba.event.UserEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@RequiredArgsConstructor
@Component
public class AuthEventProducer {

    private final KafkaTemplate<String, UserEvent> kafkaTemplate;

    public void publishUserRegistered(UserEvent event) {
        log.info("Publishing USER_REGISTERED event for userId={}", event.getUserId());
        kafkaTemplate.send("user-events", event.getUserId(), event);
    }
}
