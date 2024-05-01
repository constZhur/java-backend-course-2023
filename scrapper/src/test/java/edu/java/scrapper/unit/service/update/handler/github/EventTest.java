package edu.java.scrapper.unit.service.update.handler.github;

import com.fasterxml.jackson.databind.JsonNode;
import java.net.URI;
import java.time.OffsetDateTime;
import java.util.Optional;
import edu.java.dto.Update;
import edu.java.dto.response.EventResponse;
import edu.java.model.Link;
import edu.java.service.update.handler.github.Event;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

class EventTest {
    private final int testLinkId = 1;
    private final String testLinkUrl = "https://github.com/user/repo";

    private Link testLink;
    private Event testEvent;
    private EventResponse testEventResponse;

    @BeforeEach
    void setUp() {
        testLink = new Link(testLinkUrl);
        testLink.setId(testLinkId);

        testEvent = new TestEvent();

        testEventResponse = new EventResponse(
            "testEvent",
            OffsetDateTime.now(),
            new EventResponse.Actor("constZhur"),
            new EventResponse.Repo("constZhur/test"),
            mock(JsonNode.class));
    }

    @Test
    public void testSupportsWithMatchingEvent() {
        assertThat(testEvent.supports(testEventResponse)).isTrue();
    }

    @Test
    public void testSupportsWithNonMatchingEvent() {
        EventResponse otherEventResponse = new EventResponse(
            "otherEvent",
            OffsetDateTime.now(),
            new EventResponse.Actor("constZhur"),
            new EventResponse.Repo("constZhur/test"),
            mock(JsonNode.class));

        assertThat(testEvent.supports(otherEventResponse)).isFalse();
    }

    @Test
    public void testSupportsWithNullEvent() {
        EventResponse otherEventResponse = new EventResponse(
            null,
            OffsetDateTime.now(),
            new EventResponse.Actor("constZhur"),
            new EventResponse.Repo("constZhur/test"),
            mock(JsonNode.class));

        assertThat(testEvent.supports(otherEventResponse)).isFalse();
    }

    @Test
    public void testProcess() {
        Optional<Update> expected = Optional.of(new Update(testLinkId, testLinkUrl, "test description"));

        assertThat(testEvent.process(testEventResponse, testLink)).isEqualTo(expected);
    }

    private static class TestEvent implements Event {
        @Override
        public String type() {
            return "testEvent";
        }

        @Override
        public String getDescription(EventResponse eventResponse) {
            return "test description";
        }
    }
}
