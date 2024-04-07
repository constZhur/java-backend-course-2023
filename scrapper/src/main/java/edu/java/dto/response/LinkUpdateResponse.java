package edu.java.dto.response;

import java.net.URI;
import java.util.List;

public record LinkUpdateResponse(
    Integer id,
    URI url,
    String description,
    List<Long> tgChatIds
) { }
