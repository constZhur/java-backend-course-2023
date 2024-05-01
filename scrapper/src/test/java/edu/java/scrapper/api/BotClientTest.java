package edu.java.scrapper.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.WireMockServer;
import edu.java.clients.impl.BotClient;
import edu.java.clients.retry.RetryConfigProxy;
import edu.java.clients.retry.RetryPolicy;
import edu.java.configuration.retry.RetryConfiguration;
import edu.java.dto.request.LinkUpdateRequest;
import io.github.resilience4j.retry.Retry;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
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

    private static Retry retry;

    private WireMockServer wireMockServer;
    private BotClient botClient;
    private ObjectMapper objectMapper;

    LinkUpdateRequest updateRequest = new LinkUpdateRequest(
        1,
        URI.create("https://example.com"),
        "Example description",
        List.of(2L, 3L)
    );

    @BeforeAll
    static void beforeAll() {
        retry = RetryConfiguration.start(RetryConfigProxy
            .builder()
            .policy(RetryPolicy.LINEAR)
            .maxRetries(10)
            .retryDelay(15L)
            .increment(2)
            .httpStatuses(
                Arrays.asList(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    HttpStatus.SERVICE_UNAVAILABLE,
                    HttpStatus.BAD_GATEWAY,
                    HttpStatus.GATEWAY_TIMEOUT,
                    HttpStatus.INSUFFICIENT_STORAGE
                )
            )
            .build());
    }

    @BeforeEach
    public void setUp() {
        wireMockServer = new WireMockServer(8888);
        wireMockServer.start();
        botClient = new BotClient(retry, "http://localhost:" + wireMockServer.port());
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

    @SneakyThrows
    @Test
    public void testSend() {
        Retry retryMock = Mockito.mock(Retry.class);

        botClient = new BotClient(retryMock, "http://localhost:" + wireMockServer.port());


        botClient.send(updateRequest);

        Mockito.verify(retryMock).executeCallable(Mockito.any());
    }
}
