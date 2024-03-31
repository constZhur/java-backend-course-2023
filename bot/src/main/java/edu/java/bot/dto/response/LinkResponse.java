package edu.java.bot.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import java.net.URI;

public record LinkResponse(
    @JsonProperty("id")
    Long id,

    @NotNull
    @JsonProperty("url")
    URI uri
) { }
