package edu.java.scrapper.unit.service.update.handler;

import edu.java.clients.dto.github.GithubRepoOwner;
import edu.java.clients.dto.github.GithubResponse;
import edu.java.clients.impl.GithubClient;
import edu.java.dto.Update;
import edu.java.model.Link;
import edu.java.service.update.handler.GithubUpdateHandler;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.Optional;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class GithubUpdateHandlerTest {
    @Mock
    private GithubClient githubClient;

    @InjectMocks
    private GithubUpdateHandler githubUpdateHandler;

    @Test
    void testHostName() {
        assertThat(githubUpdateHandler.hostName()).isEqualTo("github.com");
    }

    @Test
    void testFetchUpdate() {
        String testUrl = "/github.com/constZhur/java-backend-course-2023";
        String repoOwner = "constZhur";
        String repoName = "java-backend-course-2023";
        Link link = new Link(1, testUrl,
            OffsetDateTime.of(2024, 3, 20, 21, 0, 0, 0, ZoneOffset.UTC));

        GithubResponse response = new GithubResponse(
            1L,
            repoName,
            new GithubRepoOwner(1L, repoOwner),
            OffsetDateTime.now().minusDays(5),
            OffsetDateTime.now()
        );

        when(githubClient.fetchGitHubRepositoryRetry(repoOwner, repoName)).thenReturn(response);

        Optional<Update> update = githubUpdateHandler.fetchUpdate(link);

        assertThat(update).isPresent();
        assertThat(update.get().linkId()).isEqualTo(1);
        assertThat(update.get().url()).isEqualTo(testUrl);
        assertThat(update.get().description()).isEqualTo("Новый коммит");
        assertThat(update.get().updateTime()).isEqualTo(response.pushedAt());
    }
}
