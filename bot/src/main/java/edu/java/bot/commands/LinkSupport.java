package edu.java.bot.commands;

import com.pengrad.telegrambot.model.Update;
import edu.java.bot.parsers.LinkParser;
import java.net.URI;
import java.util.List;

public interface LinkSupport {
    default boolean isInvalidInput(Update update) {
        String text = update.message().text();
        return text == null || text.strip().startsWith("/");
    }

    default boolean isSupportedLink(URI uri, List<? extends LinkParser> linkParsers) {
        return linkParsers.stream().anyMatch(linkParser -> linkParser.parseLink(uri));
    }
}
