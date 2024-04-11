package edu.java.bot.commands;

import com.pengrad.telegrambot.request.SendMessage;
import edu.java.bot.exception.BotApiBadRequestException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
public class StartCommandTest extends AbstractCommandTest {
    private StartCommand startCommand;

    @BeforeEach
    public void setup() {
        super.setUp();
        startCommand = new StartCommand(scrapperClient);
    }

    @Test
    public void testCommand() {
        assertThat(startCommand.command()).isEqualTo("/start");
    }

    @Test
    public void testDescription(){
        assertThat(startCommand.description()).isEqualTo("Зарегистироваться в боте");
    }

    @Test
    public void testHandleWhenUserNotRegistered() {
        scrapperClient.registerChatRetry(CHAT_ID);

        String expectedText = "Добро пожаловать!\nЧтобы увидеть список команд, используйте /help";
        SendMessage msg = startCommand.handle(mockUpdate);

        assertMessage(expectedText, msg);
    }

    @Test
    public void testHandleWhenUserAlreadyRegistered() {
        doThrow(BotApiBadRequestException.class).when(scrapperClient).registerChatRetry(CHAT_ID);

        String expectedText = "Бот уже запущен и готов к использованию";
        SendMessage msg = startCommand.handle(mockUpdate);

        assertMessage(expectedText, msg);
    }
}
