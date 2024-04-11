package edu.java.bot.commands;

import com.pengrad.telegrambot.model.*;
import com.pengrad.telegrambot.request.SendMessage;
import edu.java.bot.client.scrapper.ScrapperClient;
import edu.java.bot.dto.response.ApiErrorResponse;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public abstract class AbstractCommandTest {
    @Mock
    protected ScrapperClient scrapperClient;

    @Mock
    protected Update mockUpdate;

    @Mock
    protected Message mockMessage;

    @Mock
    protected Chat mockChat;

    @Mock
    protected ApiErrorResponse mockErrorResponse;

    protected static final long CHAT_ID = 1L;

    @BeforeEach
    protected void setUp() {
        Mockito.when(mockUpdate.message()).thenReturn(mockMessage);
        Mockito.when(mockMessage.chat()).thenReturn(mockChat);
        Mockito.when(mockChat.id()).thenReturn(CHAT_ID);
    }

    protected void assertMessage(String expectedText, SendMessage actualMessage) {
        long actualChatId = (long) actualMessage.getParameters().get("chat_id");
        String actualText = (String) actualMessage.getParameters().get("text");

        Assertions.assertEquals(CHAT_ID, actualChatId);
        Assertions.assertEquals(expectedText, actualText);
    }

    @Test
    public void testNotSupports() {
        when(mockMessage.text()).thenReturn("/test");

        Command command = new TestCommand();

        assertThat(command.supports(mockUpdate)).isTrue();
    }

    @Test
    public void testToApiCommand() {
        Command command = new TestCommand();

        BotCommand botCommand = command.toApiCommand();

        assertThat(botCommand.command()).isEqualTo("/test");
        assertThat(botCommand.description()).isEqualTo("Тестовая команда");
    }

    private static class TestCommand implements Command {
        private static final String COMMAND = "/test";
        private static final String DESCRIPTION = "Тестовая команда";

        @Override
        public String command() {
            return COMMAND;
        }

        @Override
        public String description() {
            return DESCRIPTION;
        }

        @Override
        public SendMessage handle(Update update) {
            return null;
        }
    }
}


