package edu.java.scrapper.api;

import com.github.tomakehurst.wiremock.WireMockServer;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.OffsetDateTime;
import java.util.Arrays;
import edu.java.clients.dto.github.GithubRepoOwner;
import edu.java.clients.dto.github.GithubResponse;
import edu.java.clients.impl.GithubClient;
import edu.java.clients.interfaces.WebClientGithub;
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

@SpringBootTest
public class GithubClientTest {
    private static Retry retry;

    private GithubClient client;
    private WireMockServer server;

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
    void setUp(){
        server = new WireMockServer();
        server.start();
        client = new GithubClient(retry, "http://localhost:" + server.port());
    }

    @AfterEach
    void tearDown(){
        server.stop();
    }

    @Test
    public void testFetchingExistingRepository() throws IOException {
        String repository = "java-backend-course-2023";
        String username = "constZhur";

        File file = ResourceUtils.getFile("classpath:github-success-response-data.json");
        String expectedJson = FileUtils.readFileToString(file, StandardCharsets.UTF_8);

        server
            .stubFor(get("/repos/%s/%s".formatted(username, repository))
                .willReturn(aResponse()
                    .withStatus(200)
                    .withBody(expectedJson)
                    .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)));

        GithubResponse response = client.fetchGitHubRepository("constZhur", repository);

        assertThat(response).isNotNull();
        assertThat(response.id()).isEqualTo(755879057L);
        assertThat(response.name()).isEqualTo("java-backend-course-2023");
        assertThat(response.owner().login()).isEqualTo("constZhur");
        assertThat(response.updatedAt()).isEqualTo(OffsetDateTime.parse("2024-02-11T11:13:55Z"));
        assertThat(response.pushedAt()).isEqualTo(OffsetDateTime.parse("2024-02-24T21:43:31Z"));
    }

    @Test
    public void testFetchingExistingRepositoryWithRetry() throws IOException {
        String repository = "java-backend-course-2023";
        String username = "constZhur";

        File file = ResourceUtils.getFile("classpath:github-success-response-data.json");
        String expectedJson = FileUtils.readFileToString(file, StandardCharsets.UTF_8);

        server
            .stubFor(get("/repos/%s/%s".formatted(username, repository))
                .willReturn(aResponse()
                    .withStatus(200)
                    .withBody(expectedJson)
                    .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)));

        GithubResponse response = client.fetchGitHubRepositoryRetry("constZhur", repository);

        assertThat(response).isNotNull();
        assertThat(response.id()).isEqualTo(755879057L);
        assertThat(response.name()).isEqualTo("java-backend-course-2023");
        assertThat(response.owner().login()).isEqualTo("constZhur");
        assertThat(response.updatedAt()).isEqualTo(OffsetDateTime.parse("2024-02-11T11:13:55Z"));
        assertThat(response.pushedAt()).isEqualTo(OffsetDateTime.parse("2024-02-24T21:43:31Z"));
    }

    @Test
    public void testFetchingNonExistingRepository() throws IOException {
        String invalidRepository = "defaultRepo";
        String username = "defaultUser";

        File file = ResourceUtils.getFile("classpath:github-fail-response-data.json");
        String expectedJson = FileUtils.readFileToString(file, StandardCharsets.UTF_8);

        server
            .stubFor(get("/repos/%s/%s".formatted(username, invalidRepository))
                .willReturn(aResponse()
                    .withStatus(404)
                    .withBody(expectedJson)
                    .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)));

        assertThatThrownBy(() -> client.fetchGitHubRepository(
             "defaultUser", invalidRepository))
            .isInstanceOf(RuntimeException.class)
            .hasMessage("GitHub API Exception");
    }

    @Test
    public void testFetchingNonExistingRepositoryWithRetry() throws IOException {
        String invalidRepository = "defaultRepo";
        String username = "defaultUser";

        File file = ResourceUtils.getFile("classpath:github-fail-response-data.json");
        String expectedJson = FileUtils.readFileToString(file, StandardCharsets.UTF_8);

        server
            .stubFor(get("/repos/%s/%s".formatted(username, invalidRepository))
                .willReturn(aResponse()
                    .withStatus(404)
                    .withBody(expectedJson)
                    .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)));

        assertThatThrownBy(() -> client.fetchGitHubRepositoryRetry(
            "defaultUser", invalidRepository))
            .isInstanceOf(RuntimeException.class)
            .hasMessage("GitHub API Exception");
    }
}
