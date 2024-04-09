package edu.java.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import java.time.OffsetDateTime;

@Data
@AllArgsConstructor
public class EventResponse {
    private final @NotBlank String type;

    @JsonProperty("created_at")
    private final @NotNull OffsetDateTime createdAt;

    private final @NotNull Actor actor;

    private final @NotNull Repo repo;

    private final @NotNull JsonNode payload;

    public record Actor(String login) {
    }

    public record Repo(String name) {
    }
}
