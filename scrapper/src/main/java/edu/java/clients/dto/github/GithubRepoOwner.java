package edu.java.clients.dto.github;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;

public record GithubRepoOwner(
    @NotNull @JsonProperty("id") long id,
    @NotNull @JsonProperty("login") String login
) { }
