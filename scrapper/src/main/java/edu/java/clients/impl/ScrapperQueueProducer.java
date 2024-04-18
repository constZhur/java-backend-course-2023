package edu.java.clients.impl;

import edu.java.clients.interfaces.UpdateSender;
import edu.java.dto.request.LinkUpdateRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;

@RequiredArgsConstructor
public class ScrapperQueueProducer implements UpdateSender {
    private final KafkaTemplate<String, LinkUpdateRequest> kafkaTemplate;
    private final String topicName;

    @Override
    public void send(LinkUpdateRequest update) {
        kafkaTemplate.send(topicName, update);
    }
}
