package edu.java.bot.commands;

import edu.java.bot.dto.request.AddLinkRequest;
import edu.java.bot.dto.response.LinkResponse;
import edu.java.bot.exception.BotApiBadRequestException;
import edu.java.bot.models.User;
import edu.java.bot.parsers.LinkParser;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import java.net.URI;
import java.util.List;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class TrackCommandTest extends AbstractCommandTest {
    private TrackCommand trackCommand;

    @Mock
    private User mockUser;

    @Mock
    private LinkParser mockLinkParser;

    @BeforeEach
    public void setUp() {
        super.setUp();
        when(mockUser.getId()).thenReturn(CHAT_ID);
        trackCommand = new TrackCommand(scrapperClient, List.of(mockLinkParser));
    }

    @Test
    public void testCommand() {
        assertThat(trackCommand.command()).isEqualTo("/track");
    }

    @Test
    public void testDescription(){
        assertThat(trackCommand.description()).isEqualTo("Начать отслеживать ссылку");
    }

    @Test
    public void testHandleWhenLinkNotSent() {
        when(mockMessage.text()).thenReturn("/track");

        String expectedText = "Недостаточно параметров. " +
            "Введите все необходимые данные!";

        assertMessage(expectedText, trackCommand.handle(mockUpdate));
    }

    @Test
    public void testHandleWhenLinkAlreadyTracked() {
        URI url = URI.create("https://github.com/");

        when(mockMessage.text()).thenReturn("/track https://github.com/");
        when(scrapperClient.addLinkRetry(CHAT_ID,
            new AddLinkRequest(url)))
            .thenThrow(new BotApiBadRequestException(mockErrorResponse));
        when(mockLinkParser.parseLink(Mockito.any())).thenReturn(true);

        assertMessage("Данный ресурс уже отслеживается", trackCommand.handle(mockUpdate));
    }

    @Test
    public void testHandleWhenLinkAddedSuccessfully() {
        URI url = URI.create("https://github.com/");

        when(mockMessage.text()).thenReturn("/track https://github.com/");
        when(mockLinkParser.parseLink(Mockito.any())).thenReturn(true);
        when(scrapperClient.addLinkRetry(CHAT_ID, new AddLinkRequest(url))).thenReturn(new LinkResponse(1L, url));

        assertMessage("Ссылка добавлена!", trackCommand.handle(mockUpdate));
    }
}
