package edu.java.clients.impl;

import edu.java.clients.dto.stackoverflow.StackoverflowItemsResponse;
import edu.java.clients.interfaces.WebClientStackoverflow;
import io.github.resilience4j.retry.Retry;
import lombok.SneakyThrows;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

public class StackoverflowClient implements WebClientStackoverflow {
    private String stackoverflowBaseUrl = "https://api.stackexchange.com";
    private final WebClient webClient;

    private Retry retry;

    public StackoverflowClient() {
        this.webClient = WebClient.builder().baseUrl(this.stackoverflowBaseUrl).build();
    }

    public StackoverflowClient(Retry retry, String apiUrl) {
        this.retry = retry;
        if (!(apiUrl == null) && !apiUrl.isEmpty()) {
            this.stackoverflowBaseUrl = apiUrl;
        }
        this.webClient = WebClient.builder().baseUrl(this.stackoverflowBaseUrl).build();
    }

    @Override
    public StackoverflowItemsResponse fetchStackOverflowQuestion(long id) {
        return webClient.get()
            .uri("/questions/{id}?site=stackoverflow", id)
            .retrieve()
            .onStatus(HttpStatusCode::isError, clientResponse -> clientResponse.bodyToMono(RuntimeException.class)
                .flatMap(error -> Mono.error(
                    new WebClientResponseException("StackOverflow API Exception",
                        HttpStatus.NOT_FOUND.value(), "Not Found", null, null, null)
                )))
            .bodyToMono(StackoverflowItemsResponse.class)
            .block();
    }

    @SneakyThrows
    public StackoverflowItemsResponse fetchStackOverflowQuestionRetry(long id) {
        return retry.executeCallable(() -> fetchStackOverflowQuestion(id));
    }
}
