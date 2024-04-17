package edu.java.scrapper.unit.service.update.handler.github;

import com.fasterxml.jackson.databind.JsonNode;
import edu.java.clients.dto.github.GithubRepoOwner;
import edu.java.clients.dto.github.GithubResponse;
import edu.java.clients.impl.GithubClient;
import edu.java.dto.Update;
import edu.java.dto.response.EventResponse;
import edu.java.exception.NoSuchRepositoryException;
import edu.java.model.Link;
import edu.java.service.update.handler.github.Event;
import edu.java.service.update.handler.github.GithubUpdateHandler;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class GithubUpdateHandlerTest {
    private final Link link = new Link("https://github.com/user/repository");
    private final Update update = new Update(link.getId(), link.getUrl().toString(), "description");
    private final EventResponse eventResponse = new EventResponse(
        "pushEvent",
        OffsetDateTime.now(),
        new EventResponse.Actor("constZhur"),
        new EventResponse.Repo("constZhur/java-backend-course-2023"),
        mock(JsonNode.class));

    @Mock
    private GithubClient githubClient;

    @Mock
    Event event;

    @InjectMocks
    GithubUpdateHandler handler;

    @BeforeEach
    void setUp() {
        handler = new GithubUpdateHandler(githubClient, List.of(event));
    }

    @Test
    public void testGetHost() {
        assertThat(handler.hostName()).isEqualTo("github.com");
    }

    @Test
    void testFetchUpdatesReturnsUpdatesWhenEventsAfterLastCheckTime() {
        link.setCheckedAt(OffsetDateTime.now().minusDays(1));
        List<EventResponse> events = Collections.singletonList(eventResponse);

        when(event.supports(eventResponse)).thenReturn(true);
        when(event.process(eventResponse, link)).thenReturn(Optional.of(update));
        when(githubClient.fetchGithubRepositoryEvents("user", "repository")).thenReturn(events);

        List<Optional<Update>> updates = handler.fetchUpdates(link);
        assertThat(updates).hasSize(1);
        assertThat(updates.getFirst()).isPresent();
    }

    @Test
    void testFetchUpdatesReturnsUpdatesWhenEventsAfterLastCheckTimeEventNotSupported() {
        link.setCheckedAt(OffsetDateTime.now().minusDays(1));
        List<EventResponse> events = Collections.singletonList(eventResponse);

        when(githubClient.fetchGithubRepositoryEvents("user", "repository")).thenReturn(events);

        List<Optional<Update>> updates = handler.fetchUpdates(link);
        assertThat(updates).hasSize(0);
    }

    @Test
    void testFetchUpdatesReturnsUpdatesWhenEventsBeforeLastCheckTime() {
        link.setCheckedAt(OffsetDateTime.now().plusDays(1));
        List<EventResponse> events = Collections.singletonList(eventResponse);

        when(githubClient.fetchGithubRepositoryEvents("user", "repository")).thenReturn(events);

        List<Optional<Update>> updates = handler.fetchUpdates(link);
        assertThat(updates).isEmpty();
    }

    @Test
    void testFetchUpdatesHandlesNoSuchRepositoryException() {
        link.setCheckedAt(OffsetDateTime.now().minusDays(1));
        when(githubClient.fetchGithubRepositoryEvents(anyString(), anyString())).thenThrow(NoSuchRepositoryException.class);

        List<Optional<Update>> updates = handler.fetchUpdates(link);

        assertThat(updates).isEmpty();
    }
}
