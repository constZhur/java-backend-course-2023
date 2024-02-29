package edu.java.bot.commands;

import com.pengrad.telegrambot.request.SendMessage;
import edu.java.bot.models.User;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.HashSet;

@ExtendWith(MockitoExtension.class)
public class StartCommandTest extends AbstractCommandTest {
    private StartCommand startCommand;

    @BeforeEach
    public void setup() {
        super.setUp();
        startCommand = new StartCommand(mockUserRepository);
    }

    @Test
    public void testCommand() {
        Assertions.assertEquals("/start", startCommand.command());
    }

    @Test
    public void testDescription(){
        Assertions.assertEquals("Зарегистироваться в боте",
            startCommand.description());
    }

    @Test
    public void testHandleWhenUserNotRegistered() {
        Mockito.when(mockUserRepository.getById(CHAT_ID)).thenReturn(null);

        String expectedText = "Добро пожаловать!\nЧтобы увидеть список команд, используйте /help";
        SendMessage msg = startCommand.handle(mockUpdate);

        assertMessage(expectedText, msg);
        Mockito.verify(mockUserRepository).save(new User(CHAT_ID, new HashSet<>()));
    }

    @Test
    public void testHandleWhenUserAlreadyRegistered() {
        Mockito.when(mockUserRepository.getById(CHAT_ID)).thenReturn(new User(CHAT_ID, new HashSet<>()));

        String expectedText = "Бот уже запущен и готов к использованию";
        SendMessage msg = startCommand.handle(mockUpdate);

        assertMessage(expectedText, msg);
        Mockito.verify(mockUserRepository, Mockito.never()).save(Mockito.any());
    }
}
