package edu.java.scrapper.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.WireMockServer;
import edu.java.clients.impl.BotClient;
import edu.java.clients.impl.GithubClient;
import edu.java.clients.retry.RetryPolicy;
import edu.java.dto.request.LinkUpdateRequest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import java.io.IOException;
import java.net.URI;
import java.util.Arrays;
import java.util.List;
import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.assertj.core.api.Assertions.assertThat;

public class BotClientTest {
    private WireMockServer wireMockServer;
    private BotClient botClient;
    private ObjectMapper objectMapper;

    LinkUpdateRequest updateRequest = new LinkUpdateRequest(
        1,
        URI.create("https://example.com"),
        "Example description",
        List.of(2L, 3L)
    );

    @BeforeEach
    public void setUp() {
        wireMockServer = new WireMockServer(8888);
        wireMockServer.start();
        botClient = new BotClient(
            "http://localhost:" + wireMockServer.port(),
            RetryPolicy.LINEAR,
            10,
            15L,
            2,
            Arrays.asList(
                HttpStatus.INTERNAL_SERVER_ERROR,
                HttpStatus.SERVICE_UNAVAILABLE,
                HttpStatus.BAD_GATEWAY,
                HttpStatus.GATEWAY_TIMEOUT,
                HttpStatus.INSUFFICIENT_STORAGE
            )
        );

        objectMapper = new ObjectMapper();
    }

    @AfterEach
    public void tearDown() {
        wireMockServer.stop();
    }

    @Test
    public void testSendUpdates() throws IOException {
        wireMockServer
            .stubFor(post("/updates")
                .willReturn(aResponse()
                    .withStatus(200)
                    .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)));

        HttpStatus response = botClient.sendUpdates(updateRequest);

        assertThat(response).isNull();
        wireMockServer.verify(postRequestedFor(urlEqualTo("/updates"))
            .withHeader(HttpHeaders.CONTENT_TYPE, matching(MediaType.APPLICATION_JSON_VALUE))
            .withRequestBody(equalToJson(objectMapper.writeValueAsString(updateRequest))));
    }

    @Test
    public void testSendUpdatesWithRetry() throws IOException {
        wireMockServer
            .stubFor(post("/updates")
                .willReturn(aResponse()
                    .withStatus(200)
                    .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)));

        HttpStatus response = botClient.sendUpdatesRetry(updateRequest);

        assertThat(response).isNull();
        wireMockServer.verify(postRequestedFor(urlEqualTo("/updates"))
            .withHeader(HttpHeaders.CONTENT_TYPE, matching(MediaType.APPLICATION_JSON_VALUE))
            .withRequestBody(equalToJson(objectMapper.writeValueAsString(updateRequest))));
    }
}
