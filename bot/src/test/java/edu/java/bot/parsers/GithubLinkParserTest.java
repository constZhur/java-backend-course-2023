package edu.java.bot.parsers;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import java.net.URI;

public class GithubLinkParserTest {

    @Test
    void testParserWithMatchingLink() {
        LinkParser githubLinkParser = new GithubLinkParser();
        URI stackOverflowUri = URI.create("https://github.com/pengrad/java-telegram-bot-api");

        Assertions.assertTrue(githubLinkParser.parseLink(stackOverflowUri));
    }

    @Test
    void testParserWithNonMatchingLink() {
        LinkParser stackOverflowLinkParser = new StackOverflowLinkParser();
        URI nonMatchingUri = URI.create("https://edu.tinkoff.ru/my-activities/current");

        Assertions.assertFalse(stackOverflowLinkParser.parseLink(nonMatchingUri));
    }
}
