package edu.java.bot.dto.response;

import java.net.URI;

public record LinkResponse(
    long id,
    URI uri
) { }
