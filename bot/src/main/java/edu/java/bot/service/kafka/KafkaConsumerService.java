package edu.java.bot.service.kafka;

import edu.java.bot.dto.request.LinkUpdateRequest;
import edu.java.bot.service.UpdateService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class KafkaConsumerService {

    UpdateService updateService;
    DLQProducerService dlqProducerService;

    @KafkaListener(
        topics = "${app.kafka-properties.topic-name}",
        groupId = "${app.kafka-properties.kafka-consumer.group-id}"
    )
    public void listen(LinkUpdateRequest update) {
        try {
            updateService.processUpdate(update);
        } catch (Exception e) {
            dlqProducerService.send(update);
        }
    }
}
