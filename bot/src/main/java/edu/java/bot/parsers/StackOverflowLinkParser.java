package edu.java.bot.parsers;

import java.net.URI;
import org.springframework.stereotype.Service;

@Service
public class StackOverflowLinkParser implements LinkParser {
    @Override
    public boolean parseLink(URI uri) {
        return uri != null && URL.STACK_OVERFLOW.getHostName().equals(uri.getHost());
    }
}
