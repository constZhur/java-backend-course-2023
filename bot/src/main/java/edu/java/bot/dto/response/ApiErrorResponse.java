package edu.java.bot.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import java.util.List;

public record ApiErrorResponse(
    @NotBlank
    @JsonProperty("description")
    String description,

    @NotBlank
    @JsonProperty("code")
    String code,

    @NotBlank
    @JsonProperty("exceptionName")
    String exceptionName,

    @NotBlank
    @JsonProperty("exceptionMessage")
    String exceptionMessage,

    @NotEmpty
    @JsonProperty("stacktrace")
    List<String> stacktrace
) { }
