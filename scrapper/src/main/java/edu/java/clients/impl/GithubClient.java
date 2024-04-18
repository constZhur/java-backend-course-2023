package edu.java.clients.impl;

import edu.java.clients.dto.github.GithubResponse;
import edu.java.clients.interfaces.WebClientGithub;
import io.github.resilience4j.retry.Retry;
import lombok.SneakyThrows;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

public class GithubClient implements WebClientGithub {
    private String githubBaseUrl = "https://api.github.com";
    private final WebClient webClient;

    private Retry retry;

    public GithubClient() {
        this.webClient = WebClient.builder().baseUrl(this.githubBaseUrl).build();
    }

    public GithubClient(Retry retry, String apiUrl) {
        this.retry = retry;
        if (!(apiUrl == null) && !apiUrl.isEmpty()) {
            this.githubBaseUrl = apiUrl;
        }
        this.webClient = WebClient.builder().baseUrl(this.githubBaseUrl).build();
    }

    @Override
    public GithubResponse fetchGitHubRepository(String owner, String repository) {
        return webClient.get()
            .uri("/repos/{user}/{repo}", owner, repository)
            .retrieve()
            .onStatus(HttpStatusCode::isError, clientResponse -> clientResponse.bodyToMono(RuntimeException.class)
                .flatMap(error -> Mono.error(
                    new WebClientResponseException("GitHub API Exception",
                        HttpStatus.NOT_FOUND.value(), "Not Found", null, null, null)
                )))
            .bodyToMono(GithubResponse.class)
            .block();
    }

    @SneakyThrows
    public GithubResponse fetchGitHubRepositoryRetry(String owner, String repository) {
        return retry.executeCallable(() -> fetchGitHubRepository(owner, repository));
    }
}
