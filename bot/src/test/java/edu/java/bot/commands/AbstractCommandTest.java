package edu.java.bot.commands;

import com.pengrad.telegrambot.model.*;
import com.pengrad.telegrambot.request.SendMessage;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public abstract class AbstractCommandTest {
    @Mock
    protected Update mockUpdate;

    @Mock
    protected Message mockMessage;

    @Mock
    protected Chat mockChat;

    protected static final long CHAT_ID = 1L;

    protected void setUpMocks() {
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
