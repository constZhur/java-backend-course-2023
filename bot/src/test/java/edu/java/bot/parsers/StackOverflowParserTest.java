package edu.java.bot.parsers;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import java.net.URI;

public class StackOverflowParserTest {

    @Test
    void testParserWithMatchingLink() {
        LinkParser stackOverflowLinkParser = new StackOverflowLinkParser();
        URI stackOverflowUri = URI.create("https://stackoverflow.com/questions/tagged/javascript");

        Assertions.assertTrue(stackOverflowLinkParser.parseLink(stackOverflowUri));
    }

    @Test
    void testParserWithNonMatchingLink() {
        LinkParser stackOverflowLinkParser = new StackOverflowLinkParser();
        URI nonMatchingUri = URI.create("https://edu.tinkoff.ru/my-activities/current");

        Assertions.assertFalse(stackOverflowLinkParser.parseLink(nonMatchingUri));
    }
}
