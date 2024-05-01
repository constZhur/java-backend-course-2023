package edu.java.clients.impl;

import edu.java.clients.interfaces.WebClientGithub;
import edu.java.dto.response.ErrorResponse;
import edu.java.dto.response.EventResponse;
import edu.java.exception.NoSuchRepositoryException;
import io.github.resilience4j.retry.Retry;
import java.util.List;
import lombok.SneakyThrows;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

public class GithubClient implements WebClientGithub {
    private String githubBaseUrl = "https://api.github.com";
    private String accessToken;
    private Integer eventsCount;
    private final WebClient webClient;

    private Retry retry;

    public GithubClient() {
        this.webClient = WebClient.builder().baseUrl(this.githubBaseUrl).build();
    }

    public GithubClient(Retry retry, String apiUrl, String accessToken, Integer eventsCount) {
        this.retry = retry;
        if (!(apiUrl == null) && !apiUrl.isEmpty()) {
            this.githubBaseUrl = apiUrl;
        }
        this.webClient = WebClient.builder().baseUrl(this.githubBaseUrl).build();
        this.accessToken = accessToken;
        this.eventsCount = eventsCount;
    }

    @Override
    public List<EventResponse> fetchGithubRepositoryEvents(String owner, String repository) {
            Mono<List<EventResponse>> events = webClient.get()
                .uri("/repos/{usr}/{repo}/events?per_page={count}", owner, repository, eventsCount)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
                .retrieve()
                .onStatus(
                    HttpStatus.NOT_FOUND::equals,
                    response -> response.bodyToMono(ErrorResponse.class).map(NoSuchRepositoryException::new)
                ).bodyToFlux(EventResponse.class).collectList();
            return events.block();
    }

    @SneakyThrows
    public List<EventResponse> fetchGithubRepositoryEventsRetry(String owner, String repository) {
        return retry.executeCallable(() -> fetchGithubRepositoryEvents(owner, repository));
    }
}
