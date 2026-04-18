package dev.eventnotificator.kafka;

import dev.eventcommon.kafka.EventChangeMessage;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.LongDeserializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.support.serializer.JsonDeserializer;

import java.util.HashMap;
import java.util.Map;


@Configuration
@EnableKafka
public class KafkaConfig {

    @Bean
    public ConsumerFactory<Long, EventChangeMessage> consumerFactory() {

        Map<String, Object> configProperties = new HashMap<>();
        configProperties.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9093");
        configProperties.put(ConsumerConfig.GROUP_ID_CONFIG, "notificator-group");
        configProperties.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, LongDeserializer.class);

        var factory = new DefaultKafkaConsumerFactory<Long, EventChangeMessage>(configProperties);

        factory.setValueDeserializer(new JsonDeserializer<>(EventChangeMessage.class, false));

        return factory;
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<Long, EventChangeMessage> containerFactory(
            ConsumerFactory<Long, EventChangeMessage> consumerFactory
    ) {
        var factory = new ConcurrentKafkaListenerContainerFactory<Long, EventChangeMessage>();
        factory.setConsumerFactory(consumerFactory);
        return factory;
    }

}
