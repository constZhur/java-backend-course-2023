package edu.java.scrapper.api;

import com.github.tomakehurst.wiremock.WireMockServer;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import edu.java.clients.impl.GithubClient;
import edu.java.dto.response.EventResponse;
import edu.java.exception.NoSuchRepositoryException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.util.ResourceUtils;
import org.testcontainers.shaded.org.apache.commons.io.FileUtils;
import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class GithubClientTest {
    private GithubClient client;
    private WireMockServer server;

    @BeforeEach
    void setUp(){
        server = new WireMockServer();
        server.start();
        client = new GithubClient("http://localhost:" + server.port(), "token", 10);
    }

    @AfterEach
    void tearDown(){
        server.stop();
    }

    @Test
    public void testFetchingNonExistingRepository() throws IOException {
        String invalidRepository = "defaultRepo";
        String username = "defaultUser";

        File file = ResourceUtils.getFile("classpath:github-fail-response-data.json");
        String expectedJson = FileUtils.readFileToString(file, StandardCharsets.UTF_8);

        server
            .stubFor(get("/repos/%s/%s/events?per_page=%d".formatted(username, invalidRepository, 10))
                .willReturn(aResponse()
                    .withStatus(404)
                    .withBody(expectedJson)
                    .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)));

        assertThatThrownBy(() -> client.fetchGithubRepositoryEvents(username, invalidRepository))
            .isInstanceOf(NoSuchRepositoryException.class);
    }

    @Test
    public void testFetchEventsPullRequestEvent() throws IOException {
        String repository = "java-backend-course-2023";
        String username = "constZhur";
        File file = ResourceUtils.getFile("classpath:github-response-pull-request-event.json");
        String expectedJson = FileUtils.readFileToString(file, StandardCharsets.UTF_8);

        server
            .stubFor(get("/repos/%s/%s/events?per_page=%d".formatted(username, repository, 10))
                .willReturn(aResponse()
                    .withStatus(200)
                    .withBody(expectedJson)
                    .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)));

        List<EventResponse> response = client.fetchGithubRepositoryEvents(username, repository);

        EventResponse actual = response.getFirst();

        assertThat(response).isNotNull();
        assertThat(response).hasSize(1);
        assertThat(actual.getType()).isEqualTo("PullRequestEvent");
        assertThat(actual.getRepo().name()).isEqualTo("constZhur/java-backend-course-2023");
        assertThat(actual.getActor().login()).isEqualTo("constZhur");

        String mergeTo = actual.getPayload().path("pull_request").path("base").path("ref").asText();
        String mergeFrom = actual.getPayload().path("pull_request").path("head").path("ref").asText();
        assertThat(mergeTo).isEqualTo("master");
        assertThat(mergeFrom).isEqualTo("hw9");
    }

    @Test
    public void testFetchEventsPushEvent() throws IOException {
        String repository = "java-backend-course-2023";
        String username = "constZhur";
        File file = ResourceUtils.getFile("classpath:github-response-push-event.json");
        String expectedJson = FileUtils.readFileToString(file, StandardCharsets.UTF_8);

        server
            .stubFor(get("/repos/%s/%s/events?per_page=%d".formatted(username, repository, 10))
                .willReturn(aResponse()
                    .withStatus(200)
                    .withBody(expectedJson)
                    .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)));

        List<EventResponse> response = client.fetchGithubRepositoryEvents(username, repository);

        EventResponse actual = response.getFirst();

        assertThat(response).isNotNull();
        assertThat(response).hasSize(1);
        assertThat(actual.getType()).isEqualTo("PushEvent");
        assertThat(actual.getRepo().name()).isEqualTo("constZhur/java-backend-course-2023");
        assertThat(actual.getActor().login()).isEqualTo("constZhur");
    }
}
