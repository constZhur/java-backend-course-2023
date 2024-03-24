package edu.java.clients.interfaces;

import edu.java.clients.dto.github.GithubResponse;

public interface WebClientGithub {
    GithubResponse fetchGitHubRepository(String owner, String repository);
}
