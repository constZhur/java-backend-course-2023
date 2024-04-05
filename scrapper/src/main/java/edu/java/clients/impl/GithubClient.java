package edu.java.clients.impl;

import edu.java.clients.dto.github.GithubResponse;
import edu.java.clients.interfaces.WebClientGithub;
import edu.java.clients.retry.RetryConfigProxy;
import edu.java.clients.retry.RetryPolicy;
import edu.java.configuration.retry.RetryConfiguration;
import io.github.resilience4j.retry.Retry;
import jakarta.annotation.PostConstruct;
import java.util.List;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

public class GithubClient implements WebClientGithub {
    private String githubBaseUrl = "https://api.github.com";
    private final WebClient webClient;

    private Retry retry;

    @Value("${api.github.retry-policy}")
    private RetryPolicy retryPolicy;
    @Value("${api.github.max-retries}")
    private Integer maxRetries;
    @Value("${api.github.retry-delay}")
    private Long retryDelay;
    @Value("${api.github.increment}")
    private Integer increment;
    @Value("${api.github.http-codes}")
    private List<HttpStatus> httpCodes;

    public GithubClient() {
        this.webClient = WebClient.builder().baseUrl(this.githubBaseUrl).build();
    }

    public GithubClient(String apiUrl) {
        if (!(apiUrl == null) && !apiUrl.isEmpty()) {
            this.githubBaseUrl = apiUrl;
        }
        this.webClient = WebClient.builder().baseUrl(this.githubBaseUrl).build();
    }

    @PostConstruct
    private void startRetry() {
        RetryConfigProxy proxy = RetryConfigProxy.builder()
            .policy(retryPolicy)
            .maxRetries(maxRetries)
            .retryDelay(retryDelay)
            .increment(increment)
            .httpStatuses(httpCodes)
            .build();
        retry = RetryConfiguration.start(proxy);
    }

    @Override
    public GithubResponse fetchGitHubRepository(String owner, String repository) {
        return webClient.get()
            .uri("/repos/{user}/{repo}", owner, repository)
            .retrieve()
            .onStatus(HttpStatusCode::isError, clientResponse -> clientResponse.bodyToMono(RuntimeException.class)
                .flatMap(error -> Mono.error(new RuntimeException("GitHub API Exception"))))
            .bodyToMono(GithubResponse.class)
            .block();
    }

    @SneakyThrows
    public GithubResponse fetchGitHubRepositoryRetry(String owner, String repository) {
        return retry.executeCallable(() -> fetchGitHubRepository(owner, repository));
    }
}
