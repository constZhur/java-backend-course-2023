package edu.java.bot.commands;

import com.pengrad.telegrambot.request.SendMessage;
import edu.java.bot.dto.request.RemoveLinkRequest;
import edu.java.bot.dto.response.LinkResponse;
import edu.java.bot.exception.BotApiBadRequestException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import java.net.URI;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UntrackCommandTest extends AbstractCommandTest {
    private UntrackCommand untrackCommand;

    @BeforeEach
    public void setUp() {
        super.setUp();
        untrackCommand = new UntrackCommand(scrapperClient);
    }

    @Test
    public void testCommand() {
        assertThat(untrackCommand.command()).isEqualTo("/untrack");
    }

    @Test
    public void testDescription(){
        assertThat(untrackCommand.description()).isEqualTo("Прекратить отслеживание ссылки");
    }

    @Test
    public void testHandleWhenLinkNotSent(){
        when(mockMessage.text()).thenReturn("/untrack");

        String expectedText = "Недостаточно параметров. Введите все необходимые данные!";

        assertMessage(expectedText, untrackCommand.handle(mockUpdate));
    }


    @Test
    public void testHandleWhenLinkRemovedSuccessfully() {
        when(mockMessage.text()).thenReturn("/untrack https://github.com/");
        when(scrapperClient.deleteLink(CHAT_ID,
            new RemoveLinkRequest(URI.create("https://github.com/"))))
                .thenReturn(new LinkResponse(1L, URI.create("https://example.com")));

        String expectedText = "Отслеживание ссылки прекращено!";
        SendMessage actual = untrackCommand.handle(mockUpdate);

        assertMessage(expectedText, actual);
    }

    @Test
    public void testHandleWhenLinkNotTracked() {
        when(mockMessage.text()).thenReturn("/untrack https://github.com/");
        when(scrapperClient.deleteLink(CHAT_ID, new RemoveLinkRequest(URI.create("https://github.com/"))))
            .thenThrow(BotApiBadRequestException.class);

        String expectedText = "Данного ресурса нет среди отслеживаемых";
        SendMessage actual = untrackCommand.handle(mockUpdate);

        assertMessage(expectedText, actual);
    }
}
