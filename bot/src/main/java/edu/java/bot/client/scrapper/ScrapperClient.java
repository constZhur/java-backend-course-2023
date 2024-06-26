package edu.java.bot.client.scrapper;

import edu.java.bot.dto.request.AddLinkRequest;
import edu.java.bot.dto.request.RemoveLinkRequest;
import edu.java.bot.dto.response.ApiErrorResponse;
import edu.java.bot.dto.response.LinkResponse;
import edu.java.bot.dto.response.ListLinksResponse;
import edu.java.bot.exception.BotApiBadRequestException;
import edu.java.bot.exception.BotApiNotFoundException;
import io.github.resilience4j.retry.Retry;
import lombok.SneakyThrows;
import org.hibernate.validator.constraints.URL;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;

public class ScrapperClient implements WebScrapperClient {
    private static final String TG_CHAT_ENDPOINT = "/tg-chat";
    private static final String LINKS_ENDPOINT = "/links";
    private static final String TG_CHAT_ID_HEADER = "Tg-Chat-Id";

    @Value("${api.scrapper.url}")
    private String url;
    private final WebClient webClient;

    private Retry retry;

    public ScrapperClient() {
        this.webClient = WebClient.builder().baseUrl(url).build();
    }

    public ScrapperClient(Retry retry, @URL String url) {
        this.retry = retry;
        if (url != null && !url.isEmpty()) {
            this.url = url;
        }
        this.webClient = WebClient.builder().baseUrl(this.url).build();
    }

    @Override
    public void registerChat(Long chatId) {
        webClient
            .post().uri(TG_CHAT_ENDPOINT + "/" + chatId)
            .retrieve()
            .onStatus(
                HttpStatus.BAD_REQUEST::equals,
                response -> response.bodyToMono(ApiErrorResponse.class).map(BotApiBadRequestException::new)
            )
            .bodyToMono(Void.class)
            .block();
    }

    public void registerChatRetry(Long id) {
        retry.executeRunnable(() -> registerChat(id));
    }

    @Override
    public void deleteChat(Long chatId) {
        webClient.delete()
            .uri(TG_CHAT_ENDPOINT + "/" + chatId)
            .retrieve()
            .onStatus(
                HttpStatus.BAD_REQUEST::equals,
                response -> response.bodyToMono(ApiErrorResponse.class).map(BotApiBadRequestException::new)
            )
            .onStatus(
                HttpStatus.NOT_FOUND::equals,
                response -> response.bodyToMono(ApiErrorResponse.class).map(BotApiNotFoundException::new)
            )
            .bodyToMono(Void.class)
            .block();
    }

    public void deleteChatRetry(Long id) {
        retry.executeRunnable(() -> deleteChat(id));
    }

    @Override
    public ListLinksResponse getAllLinks(Long chatId) {
        return webClient.get()
            .uri(LINKS_ENDPOINT)
            .header(TG_CHAT_ID_HEADER, String.valueOf(chatId))
            .retrieve()
            .onStatus(
                HttpStatus.BAD_REQUEST::equals,
                response -> response.bodyToMono(ApiErrorResponse.class).map(BotApiBadRequestException::new)
            )
            .bodyToMono(ListLinksResponse.class)
            .block();
    }

    @SneakyThrows
    public ListLinksResponse getAllLinksRetry(Long chatId) {
        return retry.executeCallable(() -> getAllLinks(chatId));
    }

    @Override
    public LinkResponse addLink(Long chatId, AddLinkRequest request) {
        return webClient.post()
            .uri(LINKS_ENDPOINT)
            .header(TG_CHAT_ID_HEADER, String.valueOf(chatId))
            .body(BodyInserters.fromValue(request))
            .retrieve()
            .onStatus(
                HttpStatus.BAD_REQUEST::equals,
                response -> response.bodyToMono(ApiErrorResponse.class).map(BotApiBadRequestException::new)
            )
            .bodyToMono(LinkResponse.class)
            .block();
    }

    @SneakyThrows
    public LinkResponse addLinkRetry(Long chatId, AddLinkRequest request) {
        return retry.executeCallable(() -> addLink(chatId, request));
    }

    @Override
    public LinkResponse deleteLink(Long chatId, RemoveLinkRequest request) {
        return webClient.method(HttpMethod.DELETE)
            .uri(LINKS_ENDPOINT)
            .header(TG_CHAT_ID_HEADER, String.valueOf(chatId))
            .body(BodyInserters.fromValue(request))
            .retrieve()
            .onStatus(
                HttpStatus.BAD_REQUEST::equals,
                response -> response.bodyToMono(ApiErrorResponse.class).map(BotApiBadRequestException::new)
            )
            .onStatus(
                HttpStatus.NOT_FOUND::equals,
                response -> response.bodyToMono(ApiErrorResponse.class).map(BotApiNotFoundException::new)
            )
            .bodyToMono(LinkResponse.class)
            .block();
    }

    @SneakyThrows
    public LinkResponse deleteLinkRetry(Long chatId, RemoveLinkRequest request) {
        return retry.executeCallable(() -> deleteLink(chatId, request));
    }
}
