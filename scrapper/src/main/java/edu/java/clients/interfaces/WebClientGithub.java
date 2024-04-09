package edu.java.clients.interfaces;

import edu.java.dto.response.EventResponse;
import java.util.List;

public interface WebClientGithub {
    List<EventResponse> fetchGithubRepositoryEvents(String owner, String repository);
}
