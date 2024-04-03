package edu.java.clients.impl;

import edu.java.clients.dto.BotApiErrorResponse;
import edu.java.clients.interfaces.WebClientBot;
import edu.java.clients.retry.RetryConfigProxy;
import edu.java.clients.retry.RetryPolicy;
import edu.java.configuration.RetryConfiguration;
import edu.java.dto.request.LinkUpdateRequest;
import io.github.resilience4j.retry.Retry;
import jakarta.annotation.PostConstruct;
import java.util.List;
import lombok.SneakyThrows;
import org.hibernate.validator.constraints.URL;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

public class BotClient implements WebClientBot {
    @Value("${api.bot.url}")
    private String url;
    private final WebClient webClient;

    private Retry retry;

    @Value("${api.bot.retry-policy}")
    private RetryPolicy retryPolicy;
    @Value("${api.bot.max-retries}")
    private Integer maxRetries;
    @Value("${api.bot.retry-delay}")
    private Long retryDelay;
    @Value("${api.bot.increment}")
    private Integer increment;
    @Value("${api.bot.http-codes}")
    private List<HttpStatus> httpCodes;

    public BotClient() {
        this.webClient = WebClient.builder().baseUrl(url).build();
    }

    public BotClient(@URL String url) {
        if (url != null && !url.isEmpty()) {
            this.url = url;
        }
        this.webClient = WebClient.builder().baseUrl(this.url).build();
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
    public HttpStatus sendUpdates(LinkUpdateRequest linkUpdate) {
        return webClient.post()
            .uri("/updates")
            .bodyValue(linkUpdate)
            .retrieve()
            .onStatus(HttpStatusCode::isError, clientResponse -> clientResponse.bodyToMono(BotApiErrorResponse.class)
                .flatMap(error -> Mono.error(new BotApiErrorResponse(error.getDescription(), error.getCode(),
                    error.getExceptionName(), error.getExceptionMessage(), error.getStacktrace()))))
            .bodyToMono(HttpStatus.class)
            .block();
    }

    @SneakyThrows
    public void sendUpdatesRetry(LinkUpdateRequest linkUpdate) {
        retry.executeCallable(() -> sendUpdates(linkUpdate));
    }
}
