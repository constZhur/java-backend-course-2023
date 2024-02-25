package edu.java.clients.impl;

import edu.java.clients.dto.stackoverflow.StackoverflowItemsResponse;
import edu.java.clients.interfaces.WebClientStackoverflow;
import org.springframework.http.HttpStatusCode;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

public class StackoverflowClient implements WebClientStackoverflow {
    private String stackoverflowBaseUrl = "https://api.stackexchange.com";
    private final WebClient webClient;

    public StackoverflowClient() {
        this.webClient = WebClient.builder().baseUrl(this.stackoverflowBaseUrl).build();
    }

    public StackoverflowClient(String apiUrl) {
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
                .flatMap(error -> Mono.error(new RuntimeException("StackOverflow API Exception"))))
            .bodyToMono(StackoverflowItemsResponse.class)
            .block();
    }
}
