package edu.java.clients.impl;

import edu.java.clients.dto.BotApiErrorResponse;
import edu.java.clients.interfaces.WebClientBot;
import edu.java.dto.request.LinkUpdateRequest;
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

    public BotClient() {
        this.webClient = WebClient.builder().baseUrl(url).build();
    }

    public BotClient(@URL String url) {
        if (url != null && !url.isEmpty()) {
            this.url = url;
        }
        this.webClient = WebClient.builder().baseUrl(this.url).build();
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
}
