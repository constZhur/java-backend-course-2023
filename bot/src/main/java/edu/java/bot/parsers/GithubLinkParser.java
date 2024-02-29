package edu.java.bot.parsers;

import java.net.URI;
import org.springframework.stereotype.Service;

@Service
public class GithubLinkParser implements LinkParser {
    @Override
    public boolean parseLink(URI uri) {
        return uri != null && URL.GITHUB.getHostName().equals(uri.getHost());
    }
}
