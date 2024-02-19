package edu.java.bot.parsers;

import org.springframework.stereotype.Service;
import java.net.URI;

@Service
public class GithubLinkParser implements LinkParser {
    @Override
    public boolean parseLink(URI uri) {
        return uri != null && URL.GITHUB.getHostName().equals(uri.getHost());
    }
}
