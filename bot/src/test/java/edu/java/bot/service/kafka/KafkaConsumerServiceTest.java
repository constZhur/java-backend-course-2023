package edu.java.bot.service.kafka;

import edu.java.bot.dto.request.LinkUpdateRequest;
import edu.java.bot.service.UpdateService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;
import java.net.URI;
import java.util.List;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.doThrow;

@SpringBootTest
@ExtendWith(MockitoExtension.class)
public class KafkaConsumerServiceTest {
    @Mock
    private UpdateService updateService;

    @Mock
    private DLQProducerService dlqProducerService;

    @InjectMocks
    private KafkaConsumerService kafkaConsumerService;

    private LinkUpdateRequest update;

    @BeforeEach
    void setUp() {
        update = new LinkUpdateRequest(1L, URI.create("http://github.com"), "Description", List.of(1L, 2L));
    }

    @Test
    void testListen_SuccessfulProcessing() {
        kafkaConsumerService.listen(update);

        verify(updateService, times(1)).processUpdate(update);
        verify(dlqProducerService, never()).send(update);
    }

    @Test
    void testListen_ExceptionHandling() {
        doThrow(new RuntimeException()).when(updateService).processUpdate(update);

        kafkaConsumerService.listen(update);

        verify(updateService, times(1)).processUpdate(update);
        verify(dlqProducerService, times(1)).send(update);
    }
}
