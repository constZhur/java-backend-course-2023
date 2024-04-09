package edu.java.dto.response;

import jakarta.validation.constraints.NotBlank;

public record ErrorResponse(
    @NotBlank
    String message
) { }
