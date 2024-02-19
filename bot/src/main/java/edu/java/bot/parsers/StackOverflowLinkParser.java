package edu.java.bot.parsers;

import org.springframework.stereotype.Service;
import java.net.URI;

@Service
public class StackOverflowLinkParser implements LinkParser {
    @Override
    public boolean parseLink(URI uri) {
        return uri != null && URL.STACK_OVERFLOW.getHostName().equals(uri.getHost());
    }
}
