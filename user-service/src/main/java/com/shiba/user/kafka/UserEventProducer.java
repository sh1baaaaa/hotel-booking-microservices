package com.shiba.user.kafka;

import com.shiba.event.UserEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class UserEventProducer {

    private final KafkaTemplate<String, UserEvent> kafkaTemplate;

    @Async
    public void publishUserUpdated(UserEvent event) {
        log.info("Publishing USER_UPDATED event for userId={}", event.getUserId());
        kafkaTemplate.send("user-events", event.getUserId(), event);
    }

    @Async
    public void publishUserDeleted(UserEvent event) {
        log.info("Publishing USER_DELETED event for userId={}", event.getUserId());
        kafkaTemplate.send("user-events", event.getUserId(), event);
    }
}
