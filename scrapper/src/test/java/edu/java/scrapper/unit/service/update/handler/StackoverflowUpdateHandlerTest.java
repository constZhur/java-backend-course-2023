package edu.java.scrapper.unit.service.update.handler;

import edu.java.clients.dto.stackoverflow.StackOverflowAccountOwner;
import edu.java.clients.dto.stackoverflow.StackoverflowItemsResponse;
import edu.java.clients.dto.stackoverflow.StackoverflowResponse;
import edu.java.clients.impl.StackoverflowClient;
import edu.java.dto.Update;
import edu.java.model.Link;
import edu.java.service.update.handler.StackoverflowUpdateHandler;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Optional;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class StackoverflowUpdateHandlerTest {

    @Mock
    private StackoverflowClient stackoverflowClient;

    @InjectMocks
    private StackoverflowUpdateHandler stackoverflowUpdateHandler;


    @Test
    void testHostName() {
        assertThat(stackoverflowUpdateHandler.hostName()).isEqualTo ("stackoverflow.com");
    }

    @Test
    void testFetchUpdate() {
        String testUrl = "https://stackoverflow.com/questions/46125417/how-to-mock-a-service-using-wiremock";
        Link link = new Link(1, testUrl,
            OffsetDateTime.of(2024, 3, 20, 21, 0, 0, 0, ZoneOffset.UTC));
        long questionId = 46125417L;
        StackOverflowAccountOwner owner = new StackOverflowAccountOwner(1L, "constZhur");
        StackoverflowResponse response = new StackoverflowResponse(
            questionId,
            "Test question",
            owner,
            OffsetDateTime.now()
        );
        StackoverflowItemsResponse itemsResponse = new StackoverflowItemsResponse(List.of(response));

        when(stackoverflowClient.fetchStackOverflowQuestionRetry(questionId)).thenReturn(itemsResponse);

        Optional<Update> update = stackoverflowUpdateHandler.fetchUpdate(link);

        assertThat(update).isPresent();
        assertThat(update.get().linkId()).isEqualTo(link.getId());
        assertThat(update.get().url()).isEqualTo(testUrl);
        assertThat(update.get().description()).isEqualTo("Обновления в вопросе");
        assertThat(update.get().updateTime()).isEqualTo(response.lastActivityDate());
    }
}
