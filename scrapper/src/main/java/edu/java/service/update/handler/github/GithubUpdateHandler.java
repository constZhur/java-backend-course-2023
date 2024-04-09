package edu.java.service.update.handler.github;

import edu.java.clients.dto.github.GithubResponse;
import edu.java.clients.impl.GithubClient;
import edu.java.dto.Update;
import edu.java.dto.response.EventResponse;
import edu.java.exception.NoSuchRepositoryException;
import edu.java.model.Link;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import edu.java.service.update.handler.UpdateHandler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class GithubUpdateHandler implements UpdateHandler {
    private static final String HOSTNAME = "github.com";

    private final int usernameIndex = 3;
    private final int repositoryIndex = 4;

    private final GithubClient githubClient;
    private final List<Event> supportedEvents;

    @Override
    public String hostName() {
        return HOSTNAME;
    }

    @Override
    public List<Optional<Update>> fetchUpdates(Link link) {
        String url = link.getUrl().toString();
        String[] urlParts = url.split("/");

        List<Optional<Update>> updates = new ArrayList<>();
        try {
            List<EventResponse> events = fetchNewEvents(
                urlParts[usernameIndex],
                urlParts[repositoryIndex],
                link.getCheckedAt()
            );

            events.forEach(event -> {
                for (Event e : supportedEvents) {
                    if (e.supports(event)) {
                        updates.add(e.process(event, link));
                    }
                }
            });
        } catch (NoSuchRepositoryException e) {
            log.error("При обработке обновлений возникла ошибка.\n{}", updates, e);
        }
        return updates;
    }

    private List<EventResponse> fetchNewEvents(String username, String repository, OffsetDateTime lastUpdateTime) {
        List<EventResponse> events = githubClient.fetchGithubRepositoryEventsRetry(username, repository);
        log.info(events.toString());
        log.info(events.stream().filter(e -> e.getCreatedAt().isAfter(lastUpdateTime)).toList().toString());
        return events.stream().filter(e -> e.getCreatedAt().isAfter(lastUpdateTime)).toList();
    }
}
