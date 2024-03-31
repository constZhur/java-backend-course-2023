package edu.java.bot.dto.request;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.net.URI;
import java.util.List;
import org.hibernate.validator.constraints.URL;

public record LinkUpdateRequest(
    @NotNull Long id,
    @URL URI url,
    String description,
    @NotEmpty List<Long> tgChatIds
) { }
