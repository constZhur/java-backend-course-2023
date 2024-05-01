package edu.java.service.update.handler.github;

import edu.java.dto.Update;
import edu.java.dto.response.EventResponse;
import edu.java.model.Link;
import java.util.Optional;

public interface Event {
    String type();

    String getDescription(EventResponse eventResponse);

    default boolean supports(EventResponse response) {
        return response.getType() != null && response.getType().equals(type());
    }

    default Optional<Update> process(EventResponse eventResponse, Link link) {
        return Optional.of(new Update(link.getId(), link.getUrl(), getDescription(eventResponse)));
    }
}
