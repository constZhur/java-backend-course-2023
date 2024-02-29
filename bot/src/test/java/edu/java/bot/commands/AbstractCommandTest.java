package edu.java.bot.commands;

import com.pengrad.telegrambot.model.*;
import com.pengrad.telegrambot.request.SendMessage;
import edu.java.bot.repositories.UserRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public abstract class AbstractCommandTest {
    @Mock
    protected UserRepository mockUserRepository;

    @Mock
    protected Update mockUpdate;

    @Mock
    protected Message mockMessage;

    @Mock
    protected Chat mockChat;

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
}
