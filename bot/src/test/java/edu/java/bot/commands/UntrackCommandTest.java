package edu.java.bot.commands;

import com.pengrad.telegrambot.request.SendMessage;
import edu.java.bot.models.User;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.HashSet;

@ExtendWith(MockitoExtension.class)
public class UntrackCommandTest extends AbstractCommandTest {
    private UntrackCommand untrackCommand;

    @Mock
    private User mockUser;

    @BeforeEach
    public void setUp() {
        super.setUp();
        untrackCommand = new UntrackCommand(mockUserRepository);
    }

    @Test
    public void testCommand() {
        Assertions.assertEquals("/untrack", untrackCommand.command());
    }

    @Test
    public void testDescription(){
        Assertions.assertEquals("Прекратить отслеживание ссылки",
            untrackCommand.description());
    }

    @Test
    public void testHandleWhenLinkNotSent(){
        Mockito.when(mockMessage.text()).thenReturn("/untrack");

        String expectedText = "Недостаточно параметров. Введите все необходимые данные!";

        assertMessage(expectedText, untrackCommand.handle(mockUpdate));
    }

    @Test
    public void testHandleWhenNoSuchLink() {
        Mockito.when(mockMessage.text()).thenReturn("/untruck https://edu.tinkoff.ru/");
        Mockito.when(mockUserRepository.getById(1L)).thenReturn(null);

        String expectedText = "Пользователь не найден.\n"
            + "Перезапустите бота с помощью /start";
        SendMessage msg = untrackCommand.handle(mockUpdate);

        assertMessage(expectedText, msg);
    }


    @Test
    public void testHandleWhenLinkRemovedSuccessfully() {
        Mockito.when(mockMessage.text()).thenReturn("/untrack https://github.com/");
        HashSet<String> trackedLinks = new HashSet<>();
        trackedLinks.add("https://github.com/");
        Mockito.when(mockUser.getLinks()).thenReturn(trackedLinks);
        Mockito.when(mockUserRepository.getById(CHAT_ID)).thenReturn(mockUser);

        String expectedText = "Отслеживание ссылки прекращено!";
        SendMessage actual = untrackCommand.handle(mockUpdate);

        assertMessage(expectedText, actual);
    }

    @Test
    public void testHandleWhenLinkNotTracked() {
        Mockito.when(mockMessage.text()).thenReturn("/untrack https://github.com/");
        Mockito.when(mockUser.getLinks()).thenReturn(new HashSet<>());
        Mockito.when(mockUserRepository.getById(CHAT_ID)).thenReturn(mockUser);

        String expectedText = "Данного ресурса нет среди отслеживаемых";
        SendMessage actual = untrackCommand.handle(mockUpdate);

        assertMessage(expectedText, actual);
    }

    @Test
    public void testHandleWhenUserNotFound() {
        Mockito.when(mockMessage.text()).thenReturn("/untrack https://github.com/");
        Mockito.when(mockUserRepository.getById(CHAT_ID)).thenReturn(null);

        assertMessage("Пользователь не найден.\nПерезапустите бота с помощью /start",
            untrackCommand.handle(mockUpdate));
    }
}
