package edu.java.dto.request;

import org.hibernate.validator.constraints.URL;

public record RemoveLinkRequest(
    @URL String link
) { }
