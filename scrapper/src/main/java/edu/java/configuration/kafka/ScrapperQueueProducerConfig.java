package edu.java.configuration.kafka;

import edu.java.dto.request.LinkUpdateRequest;
import edu.java.clients.impl.ScrapperQueueProducer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.KafkaTemplate;

@Configuration
@ConditionalOnProperty(prefix = "app", name = "use-queue", havingValue = "true")
public class ScrapperQueueProducerConfig {
    @Value("${app.kafka-properties.topic-name}")
    private String topicName;

    @Bean
    public ScrapperQueueProducer scrapperQueueProducer(KafkaTemplate<String, LinkUpdateRequest> kafkaTemplate) {
        return new ScrapperQueueProducer(kafkaTemplate, topicName);
    }
}
