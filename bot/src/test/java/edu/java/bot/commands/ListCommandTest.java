package edu.java.bot.commands;

import com.pengrad.telegrambot.request.SendMessage;
import edu.java.bot.dto.response.LinkResponse;
import edu.java.bot.dto.response.ListLinksResponse;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ListCommandTest extends AbstractCommandTest {
    private ListCommand listCommand;

    @BeforeEach
    public void setUp() {
        super.setUp();
        listCommand = new ListCommand(scrapperClient);
    }

    @Test
    public void testCommand() {
        assertThat(listCommand.command()).isEqualTo("/list");
    }

    @Test
    public void testDescription() {
        assertThat(listCommand.description()).isEqualTo("Показать список всех отслеживаемых ссылок");
    }

    @Test
    public void testHandleEmptyTrackedList() {
        when(scrapperClient.getAllLinksRetry(CHAT_ID)).thenReturn(new ListLinksResponse(Collections.emptyList(), 0));

        String expectedText = "В данный момент вы не отслеживание никакие ресурсы.\n"
            + "Начать отслеживание ссылки можно при помощи команды /track";
        SendMessage msg = listCommand.handle(mockUpdate);

        assertMessage(expectedText, msg);
    }

    @Test
    public void testHandleWithTrackedLinks() {
        List<LinkResponse> links = new ArrayList<>(List.of(
            new LinkResponse(1L, URI.create("https://google.com/")),
            new LinkResponse(2L, URI.create("https://github.com/")))
        );

        when(scrapperClient.getAllLinksRetry(CHAT_ID)).thenReturn(new ListLinksResponse(links, 2));

        String expectedText = "Список отслеживаемых ссылок:\n- https://google.com/\n- https://github.com/\n";
        SendMessage msg = listCommand.handle(mockUpdate);
        boolean displayWebPagePreview = (boolean) msg.getParameters().get("disable_web_page_preview");

        assertMessage(expectedText, msg);
        Assertions.assertTrue(displayWebPagePreview);
    }
}
