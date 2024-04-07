package edu.java.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.OffsetDateTime;

public record Update(
    @NotNull
    Integer linkId,

    @NotBlank
    String url,

    @NotBlank
    String description,

    @NotNull
    OffsetDateTime updateTime
) { }
