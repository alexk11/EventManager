package dev.eventmanager.async;

import dev.eventcommon.kafka.EventChangeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;


@Slf4j
@RequiredArgsConstructor
@Component
public class KafkaMessageSender {

    private final KafkaTemplate<Long, EventChangeMessage> eventTemplate;

    @Value("${spring.kafka.topics.event-manager.events-topic}")
    private String eventTopic;

    public void send(EventChangeMessage message) {
        log.info("Sending event message = {}", message);
        eventTemplate.send(
                        eventTopic,
                        message.eventId(),
                        message)
                .thenAccept(
                        sendResult -> log.info("Event send success"));
    }

}
