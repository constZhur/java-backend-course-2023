package edu.java.scrapper.api;

import edu.java.clients.impl.ScrapperQueueProducer;
import edu.java.dto.request.LinkUpdateRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.core.KafkaTemplate;
import java.net.URI;
import java.util.List;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@SpringBootTest
@ExtendWith(MockitoExtension.class)
public class ScrapperQueueProducerTest {

    @Mock
    private KafkaTemplate<String, LinkUpdateRequest> kafkaTemplate;

    private ScrapperQueueProducer scrapperQueueProducer;

    private LinkUpdateRequest update;

    @BeforeEach
    void setUp() {
        update = new LinkUpdateRequest(1, URI.create("http://github.com"), "Description", List.of(1L, 2L));

        scrapperQueueProducer = new ScrapperQueueProducer(kafkaTemplate, "bot-updates");
    }

    @Test
    void testSend() {
        String topicName = "bot-updates";

        scrapperQueueProducer.send(update);

        verify(kafkaTemplate, times(1)).send(eq(topicName), eq(update));
    }
}
