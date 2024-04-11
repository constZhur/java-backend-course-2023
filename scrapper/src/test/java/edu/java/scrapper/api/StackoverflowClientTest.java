package edu.java.scrapper.api;

import com.github.tomakehurst.wiremock.WireMockServer;
import edu.java.clients.dto.stackoverflow.StackoverflowItemsResponse;
import edu.java.clients.dto.stackoverflow.StackoverflowResponse;
import edu.java.clients.impl.StackoverflowClient;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import edu.java.clients.interfaces.WebClientStackoverflow;
import edu.java.clients.retry.RetryConfigProxy;
import edu.java.clients.retry.RetryPolicy;
import edu.java.configuration.RetryConfiguration;
import io.github.resilience4j.retry.Retry;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.util.ResourceUtils;
import org.testcontainers.shaded.org.apache.commons.io.FileUtils;
import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class StackoverflowClientTest {

    private static Retry retry;

    private WireMockServer server;
    private StackoverflowClient client;

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
        server = new WireMockServer();
        server.start();
        client = new StackoverflowClient(retry,"http://localhost:" + server.port());
    }

    @AfterEach
    public void tearDown() {
        server.stop();
    }

    @Test
    public void testFetchStackOverflowQuestion() throws IOException {
        long questionId = 46125417;
        File file = ResourceUtils.getFile("classpath:stackoverflow-success-response-data.json");
        String expectedJson = FileUtils.readFileToString(file, StandardCharsets.UTF_8);

        server
            .stubFor(get("/questions/%d?site=stackoverflow".formatted(questionId))
                .willReturn(aResponse()
                    .withStatus(200)
                    .withBody(expectedJson)
                    .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)));

        StackoverflowItemsResponse response = client.fetchStackOverflowQuestion(questionId);

        assertThat(response).isNotNull();
        assertThat(response.items()).hasSize(1);

        StackoverflowResponse question = response.items().get(0);
        assertThat(question.id()).isEqualTo(46125417);
        assertThat(question.link()).isEqualTo("https://stackoverflow.com/questions/46125417/how-to-mock-a-service-using-wiremock");
        assertThat(question.owner()).isNotNull();
        assertThat(question.owner().id()).isEqualTo(6181892);
        assertThat(question.owner().login()).isEqualTo("worrynerd");
        assertThat(question.lastActivityDate()).isNotNull();
        assertThat(question.lastActivityDate().toEpochSecond()).isEqualTo(1505142513);

    }

    @Test
    public void testFetchStackOverflowQuestionWithRetry() throws IOException {
        long questionId = 46125417;
        File file = ResourceUtils.getFile("classpath:stackoverflow-success-response-data.json");
        String expectedJson = FileUtils.readFileToString(file, StandardCharsets.UTF_8);

        server
            .stubFor(get("/questions/%d?site=stackoverflow".formatted(questionId))
                .willReturn(aResponse()
                    .withStatus(200)
                    .withBody(expectedJson)
                    .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)));

        StackoverflowItemsResponse response = client.fetchStackOverflowQuestionRetry(questionId);

        assertThat(response).isNotNull();
        assertThat(response.items()).hasSize(1);

        StackoverflowResponse question = response.items().get(0);
        assertThat(question.id()).isEqualTo(46125417);
        assertThat(question.link()).isEqualTo("https://stackoverflow.com/questions/46125417/how-to-mock-a-service-using-wiremock");
        assertThat(question.owner()).isNotNull();
        assertThat(question.owner().id()).isEqualTo(6181892);
        assertThat(question.owner().login()).isEqualTo("worrynerd");
        assertThat(question.lastActivityDate()).isNotNull();
        assertThat(question.lastActivityDate().toEpochSecond()).isEqualTo(1505142513);

    }

    @Test
    public void testFetchStackOverflowQuestionWithInvalidUrl() throws IOException {
        long invalidQuestionId = 1;

        File file = ResourceUtils.getFile("classpath:stackoverflow-fail-response-data.json");
        String expectedJson = FileUtils.readFileToString(file, StandardCharsets.UTF_8);

        server
            .stubFor(get("/questions/%d?site=stackoverflow".formatted(invalidQuestionId))
                .willReturn(aResponse()
                    .withStatus(404)
                    .withBody(expectedJson)
                    .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)));

        assertThatThrownBy(() -> client.fetchStackOverflowQuestion(invalidQuestionId))
            .isInstanceOf(RuntimeException.class)
            .hasMessage("StackOverflow API Exception");
    }

    @Test
    public void testFetchStackOverflowQuestionWithInvalidUrlWithRetry() throws IOException {
        long invalidQuestionId = 1;

        File file = ResourceUtils.getFile("classpath:stackoverflow-fail-response-data.json");
        String expectedJson = FileUtils.readFileToString(file, StandardCharsets.UTF_8);

        server
            .stubFor(get("/questions/%d?site=stackoverflow".formatted(invalidQuestionId))
                .willReturn(aResponse()
                    .withStatus(404)
                    .withBody(expectedJson)
                    .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)));

        assertThatThrownBy(() -> client.fetchStackOverflowQuestionRetry(invalidQuestionId))
            .isInstanceOf(RuntimeException.class)
            .hasMessage("StackOverflow API Exception");
    }
}
