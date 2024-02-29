package edu.java.bot.commands;

import edu.java.bot.models.User;
import edu.java.bot.parsers.LinkParser;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import static org.assertj.core.api.Assertions.assertThat;

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
        Mockito.when(mockUser.getId()).thenReturn(CHAT_ID);
        trackCommand = new TrackCommand(mockUserRepository, List.of(mockLinkParser));
    }

    @Test
    public void testCommand() {
        Assertions.assertEquals("/track", trackCommand.command());
    }

    @Test
    public void testDescription(){
        Assertions.assertEquals("Начать отслеживать ссылку",
            trackCommand.description());
    }

    @Test
    public void testHandleWhenLinkNotSent() {
        Mockito.when(mockMessage.text()).thenReturn("/track");

        String expectedText = "Недостаточно параметров. " +
            "Введите все необходимые данные!";

        assertMessage(expectedText, trackCommand.handle(mockUpdate));
    }

    @Test
    public void testHandleWithUnsupportedResource() {
        Mockito.when(mockMessage.text()).thenReturn("/track https://edu.tinkoff.ru/");
        Mockito.when(mockUserRepository.getById(CHAT_ID)).thenReturn(mockUser);
        Mockito.when(mockLinkParser.parseLink(Mockito.any())).thenReturn(false);

        assertMessage("Данный ресурс не поддерживается!", trackCommand.handle(mockUpdate));
    }

    @Test
    public void testHandleWhenLinkAlreadyTracked() {
        Set<String> trackedLinks = new HashSet<>();
        trackedLinks.add("https://github.com/");

        Mockito.when(mockMessage.text()).thenReturn("/track https://github.com/");
        Mockito.when(mockUserRepository.getById(CHAT_ID)).thenReturn(mockUser);
        Mockito.when(mockUser.getLinks()).thenReturn(trackedLinks);
        Mockito.when(mockLinkParser.parseLink(Mockito.any())).thenReturn(true);

        assertMessage("Данный ресурс уже отслеживается", trackCommand.handle(mockUpdate));
    }

    @Test
    public void testHandleWhenLinkAddedSuccessfully() {
        Set<String> trackedLinks = new HashSet<>();

        Mockito.when(mockMessage.text()).thenReturn("/track https://github.com/");
        Mockito.when(mockUserRepository.getById(CHAT_ID)).thenReturn(mockUser);
        Mockito.when(mockUser.getLinks()).thenReturn(trackedLinks);
        Mockito.when(mockLinkParser.parseLink(Mockito.any())).thenReturn(true);

        assertMessage("Ссылка добавлена!", trackCommand.handle(mockUpdate));
        assertThat(trackedLinks).contains("https://github.com/");
    }

    @Test
    public void testHandleWhenUserNotFound() {
        Mockito.when(mockMessage.text()).thenReturn("/track https://github.com/");
        Mockito.when(mockUserRepository.getById(CHAT_ID)).thenReturn(null);

        assertMessage("Пользователь не найден.\nПерезапустите бота с помощью /start",
            trackCommand.handle(mockUpdate));
    }

    @Test
    public void testHandleWhenLinkHasWrongFormat() {
        Mockito.when(mockMessage.text()).thenReturn("track https://github.com/");
        Mockito.when(mockUserRepository.getById(CHAT_ID)).thenReturn(mockUser);

        assertMessage("Неверный формат команды", trackCommand.handle(mockUpdate));
    }
}
