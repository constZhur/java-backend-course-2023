package edu.java.clients.dto.github;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import java.time.OffsetDateTime;

public record GithubResponse(
    @NotNull @JsonProperty("id") long id,
    @NotNull @JsonProperty("name") String name,
    @NotNull GithubRepoOwner owner,
    @NotNull @JsonProperty("updated_at") OffsetDateTime updatedAt,
    @NotNull @JsonProperty("pushed_at") OffsetDateTime pushedAt
) { }
