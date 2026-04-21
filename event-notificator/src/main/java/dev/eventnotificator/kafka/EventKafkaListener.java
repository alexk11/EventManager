package dev.eventnotificator.kafka;

import dev.eventcommon.kafka.EventChangeMessage;
import dev.eventnotificator.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;


@Slf4j
@RequiredArgsConstructor
@Component
public class EventKafkaListener {

    private final NotificationService notificationService;

    @KafkaListener(topics = "${spring.kafka.topics.events-topic:events}",
            containerFactory = "containerFactory")
    public void listenEvents(ConsumerRecord<Long, EventChangeMessage> record) {
        log.info("Received event = {}", record.value());
        notificationService.saveEvent(record.value());
    }

}
