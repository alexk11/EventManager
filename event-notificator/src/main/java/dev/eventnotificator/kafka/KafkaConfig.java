package dev.eventnotificator.kafka;

import dev.eventcommon.kafka.EventChangeMessage;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.LongDeserializer;
import org.springframework.beans.factory.annotation.Value;
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

    @Value("${spring.kafka.bootstrap-servers}")
    private String brokerUrl;

    @Value("${spring.kafka.consumer.group-id}")
    private String consumerGroupId;

    @Bean
    public ConsumerFactory<Long, EventChangeMessage> consumerFactory() {

        Map<String, Object> configProperties = new HashMap<>();
        configProperties.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, brokerUrl);
        configProperties.put(ConsumerConfig.GROUP_ID_CONFIG, consumerGroupId);
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
