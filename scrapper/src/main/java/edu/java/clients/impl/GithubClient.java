package edu.java.clients.impl;

import edu.java.clients.dto.github.GithubResponse;
import edu.java.clients.interfaces.WebClientGithub;
import org.springframework.http.HttpStatusCode;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

public class GithubClient implements WebClientGithub {
    private String githubBaseUrl = "https://api.github.com";
    private final WebClient webClient;

    public GithubClient() {
        this.webClient = WebClient.builder().baseUrl(this.githubBaseUrl).build();
    }

    public GithubClient(String apiUrl) {
        if (!(apiUrl == null) && !apiUrl.isEmpty()) {
            this.githubBaseUrl = apiUrl;
        }
        this.webClient = WebClient.builder().baseUrl(this.githubBaseUrl).build();
    }

    @Override
    public GithubResponse fetchGitHubRepository(String owner, String repository) {
        return webClient.get()
            .uri("/repos/{user}/{repo}", owner, repository)
            .retrieve()
            .onStatus(HttpStatusCode::isError, clientResponse -> clientResponse.bodyToMono(RuntimeException.class)
                .flatMap(error -> Mono.error(new RuntimeException("GitHub API Exception"))))
            .bodyToMono(GithubResponse.class)
            .block();
    }
}
