package edu.java.bot.dto;

import org.hibernate.validator.constraints.URL;

public record RemoveLinkRequest(
    @URL String link
) { }
