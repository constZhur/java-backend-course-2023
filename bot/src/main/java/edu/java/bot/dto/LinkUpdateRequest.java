package edu.java.bot.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import org.hibernate.validator.constraints.URL;

public record LinkUpdateRequest(
    @NotNull Long id,
    @URL String url,
    String description,
    @NotEmpty List<Long> tgChatIds
) { }
