package edu.java.bot.service.kafka;

import edu.java.bot.dto.request.LinkUpdateRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DLQProducerService {
    private final KafkaTemplate<String, LinkUpdateRequest> kafkaTemplate;

    @Value(value = "${app.kafka-properties.dlq-topic-name}")
    private String dlqTopicName;

    public void send(LinkUpdateRequest update) {
        kafkaTemplate.send(dlqTopicName, update);
    }
}
