package edu.java.clients.impl;

import edu.java.clients.dto.stackoverflow.StackoverflowItemsResponse;
import edu.java.clients.interfaces.WebClientStackoverflow;
import edu.java.clients.retry.RetryConfigProxy;
import edu.java.clients.retry.RetryPolicy;
import edu.java.configuration.RetryConfiguration;
import io.github.resilience4j.retry.Retry;
import jakarta.annotation.PostConstruct;
import java.util.List;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

public class StackoverflowClient implements WebClientStackoverflow {
    private String stackoverflowBaseUrl = "https://api.stackexchange.com";
    private final WebClient webClient;

    private Retry retry;

    @Value("${api.stackoverflow.retry-policy}")
    private RetryPolicy retryPolicy;
    @Value("${api.stackoverflow.max-retries}")
    private Integer maxRetries;
    @Value("${api.stackoverflow.retry-delay}")
    private Long retryDelay;
    @Value("${api.stackoverflow.increment}")
    private Integer increment;
    @Value("${api.stackoverflow.http-codes}")
    private List<HttpStatus> httpCodes;

    public StackoverflowClient() {
        this.webClient = WebClient.builder().baseUrl(this.stackoverflowBaseUrl).build();
    }

    public StackoverflowClient(String apiUrl) {
        if (!(apiUrl == null) && !apiUrl.isEmpty()) {
            this.stackoverflowBaseUrl = apiUrl;
        }
        this.webClient = WebClient.builder().baseUrl(this.stackoverflowBaseUrl).build();
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
    public StackoverflowItemsResponse fetchStackOverflowQuestion(long id) {
        return webClient.get()
            .uri("/questions/{id}?site=stackoverflow", id)
            .retrieve()
            .onStatus(HttpStatusCode::isError, clientResponse -> clientResponse.bodyToMono(RuntimeException.class)
                .flatMap(error -> Mono.error(new RuntimeException("StackOverflow API Exception"))))
            .bodyToMono(StackoverflowItemsResponse.class)
            .block();
    }

    @SneakyThrows
    public StackoverflowItemsResponse fetchStackOverflowQuestionRetry(long id) {
        return retry.executeCallable(() -> fetchStackOverflowQuestion(id));
    }
}
