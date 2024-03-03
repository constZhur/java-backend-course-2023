package edu.java.clients.dto.stackoverflow;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;

public record StackOverflowAccountOwner(
    @NotNull @JsonProperty("account_id") long id,
    @NotNull @JsonProperty("display_name") String login
) { }
