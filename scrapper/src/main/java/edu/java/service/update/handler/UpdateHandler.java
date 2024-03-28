package edu.java.service.update.handler;

import edu.java.dto.Update;
import edu.java.model.Link;
import java.net.URI;
import java.util.Optional;

public interface UpdateHandler {
    String hostName();

    Optional<Update> fetchUpdate(Link link);

    default boolean supports(URI uri) {
        return uri.getHost() != null && uri.getHost().equals(hostName());
    }
}
