package edu.java.clients.dto.stackoverflow;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSetter;
import jakarta.validation.constraints.NotNull;
import java.time.OffsetDateTime;

public record StackoverflowResponse(
    @NotNull @JsonSetter("question_id") long id,
    @NotNull @JsonProperty("link") String link,
    @NotNull StackOverflowAccountOwner owner,
    @NotNull @JsonSetter("last_activity_date") OffsetDateTime lastActivityDate
) { }
