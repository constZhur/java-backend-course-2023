package edu.java.service.update.handler;

import edu.java.clients.dto.github.GithubResponse;
import edu.java.clients.impl.GithubClient;
import edu.java.dto.Update;
import edu.java.model.Link;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class GithubUpdateHandler implements UpdateHandler {
    private static final String HOSTNAME = "github.com";

    private final int usernameIndex = 2;
    private final int repositoryIndex = 3;

    private final GithubClient githubClient;

    @Override
    public String hostName() {
        return HOSTNAME;
    }

    @Override
    public Optional<Update> fetchUpdate(Link link) {
        String url = link.getUrl();
        String[] urlParts = url.split("/");

        GithubResponse response = githubClient.fetchGitHubRepositoryRetry(
            urlParts[usernameIndex], urlParts[repositoryIndex]);

        Optional<Update> update = Optional.empty();
        if (response.pushedAt().isAfter(link.getCheckedAt())) {
            update = Optional.of(new Update(
                link.getId(),
                url,
                "Новый коммит",
                response.pushedAt())
            );
        }
        return update;
    }
}
