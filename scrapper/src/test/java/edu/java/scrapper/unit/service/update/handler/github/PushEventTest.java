package edu.java.scrapper.unit.service.update.handler.github;

import com.fasterxml.jackson.databind.JsonNode;
import edu.java.dto.response.EventResponse;
import edu.java.service.update.handler.github.PushEvent;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import java.time.OffsetDateTime;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

@ExtendWith(MockitoExtension.class)
public class PushEventTest {
    @InjectMocks
    private PushEvent commitEventTest;

    @Test
    public void testType() {
        assertThat(commitEventTest.type()).isEqualTo("PushEvent");
    }

    @Test
    public void testGetDescription() {
        EventResponse eventResponse = new EventResponse(
            "PushEvent",
            OffsetDateTime.now(),
            new EventResponse.Actor("username"),
            new EventResponse.Repo("repository"),
            mock(JsonNode.class));

        assertThat(commitEventTest.getDescription(eventResponse))
            .isEqualTo("Пользователь username отправил изменения в репозиторий repository");

    }
}
