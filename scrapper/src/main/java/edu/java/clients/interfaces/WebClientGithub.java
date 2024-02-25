package edu.java.clients.interfaces;

import edu.java.clients.dto.github.GithubRepoOwner;
import edu.java.clients.dto.github.GithubResponse;

public interface WebClientGithub {
    GithubResponse fetchGitHubRepository(GithubRepoOwner owner, String repository);
}
