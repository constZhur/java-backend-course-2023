package edu.java.bot.dto.request;

import org.hibernate.validator.constraints.URL;

public record AddLinkRequest(
    @URL String link
) { }
