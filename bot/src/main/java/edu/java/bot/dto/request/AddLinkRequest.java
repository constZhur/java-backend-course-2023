package edu.java.bot.dto.request;

import java.net.URI;
import org.hibernate.validator.constraints.URL;

public record AddLinkRequest(
    @URL URI link
) { }
