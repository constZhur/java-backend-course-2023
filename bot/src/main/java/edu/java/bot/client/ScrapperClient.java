package edu.java.bot.client;

import edu.java.bot.dto.AddLinkRequest;
import edu.java.bot.dto.LinkResponse;
import edu.java.bot.dto.ListLinksResponse;
import edu.java.bot.dto.RemoveLinkRequest;
import org.hibernate.validator.constraints.URL;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;

public class ScrapperClient implements WebScrapperClient {
    private static final String TG_CHAT_ENDPOINT = "/tg-chat";
    private static final String LINKS_ENDPOINT = "/links";
    private static final String TG_CHAT_ID_HEADER = "Tg-Chat-Id";

    @Value("${api.scrapper.url}")
    private String url;
    private final WebClient webClient;

    public ScrapperClient() {
        this.webClient = WebClient.builder().baseUrl(url).build();
    }

    public ScrapperClient(@URL String url) {
        if (url != null && !url.isEmpty()) {
            this.url = url;
        }
        this.webClient = WebClient.builder().baseUrl(this.url).build();
    }

    public Void registerChat(Long chatId) {
        return webClient
            .post().uri(TG_CHAT_ENDPOINT + "/" + chatId)
            .retrieve()
            .bodyToMono(Void.class)
            .block();
    }

    public Void deleteChat(Long chatId) {
        return webClient.delete()
            .uri(TG_CHAT_ENDPOINT + "/" + chatId)
            .retrieve()
            .bodyToMono(Void.class)
            .block();
    }

    @Override
    public ListLinksResponse getAllLinks(Long chatId) {
        return webClient.get()
            .uri(LINKS_ENDPOINT)
            .header(TG_CHAT_ID_HEADER, String.valueOf(chatId))
            .retrieve()
            .bodyToMono(ListLinksResponse.class)
            .block();
    }

    public LinkResponse addLink(Long chatId, AddLinkRequest request) {
        return webClient.post()
            .uri(LINKS_ENDPOINT)
            .header(TG_CHAT_ID_HEADER, String.valueOf(chatId))
            .body(BodyInserters.fromValue(request))
            .retrieve()
            .bodyToMono(LinkResponse.class)
            .block();
    }

    public LinkResponse deleteLink(Long chatId, RemoveLinkRequest request) {
        return webClient.method(HttpMethod.DELETE)
            .uri(LINKS_ENDPOINT)
            .header(TG_CHAT_ID_HEADER, String.valueOf(chatId))
            .body(BodyInserters.fromValue(request))
            .retrieve()
            .bodyToMono(LinkResponse.class)
            .block();
    }
}
