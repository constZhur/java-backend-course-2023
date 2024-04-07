package edu.java.bot.service.kafka;

import edu.java.bot.dto.request.LinkUpdateRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;
import java.net.URI;
import java.util.List;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class DLQProducerServiceTest {

    @Mock
    private KafkaTemplate<String, LinkUpdateRequest> kafkaTemplate;

    @InjectMocks
    private DLQProducerService dlqProducerService;

    private LinkUpdateRequest update;

    @BeforeEach
    void setUp() {
        update = new LinkUpdateRequest(1L, URI.create("http://github.com"), "Description", List.of(1L, 2L));
    }

    @Test
    void testSend() {
        dlqProducerService.send(update);

        verify(kafkaTemplate).send(eq(null), eq(update));
    }
}
