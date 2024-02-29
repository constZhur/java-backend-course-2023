package edu.java.bot.commands;

import com.pengrad.telegrambot.request.SendMessage;
import edu.java.bot.models.User;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@ExtendWith(MockitoExtension.class)
public class ListCommandTest extends AbstractCommandTest {
    private ListCommand listCommand;

    @BeforeEach
    public void setUp() {
        super.setUp();
        listCommand = new ListCommand(mockUserRepository);
    }

    @Test
    public void testCommand() {
        Assertions.assertEquals("/list", listCommand.command());
    }

    @Test
    public void testDescription(){
        Assertions.assertEquals("Показать список всех отслеживаемых ссылок",
            listCommand.description());
    }

    @Test
    public void testHandleUserNotFound() {
        Mockito.when(mockUserRepository.getById(CHAT_ID)).thenReturn(null);

        String  expectedText = "Пользователь не был найден.\n"
            + "Запустите бота заново, использя команду /start";
        SendMessage msg = listCommand.handle(mockUpdate);

        assertMessage(expectedText, msg);
    }

    @Test
    public void testHandleEmptyTrackedList() {
        Set<String> emptySet = Collections.emptySet();
        Mockito.when(mockUserRepository.getById(CHAT_ID)).thenReturn(new User(CHAT_ID, emptySet));

        String expectedText = "В данный момент вы не отслеживание никакие ресурсы.\n"
            + "Начать отслеживание ссылки можно при помощи команды /track";
        SendMessage msg = listCommand.handle(mockUpdate);

        assertMessage(expectedText, msg);
    }

    @Test
    public void testHandleWithTrackedLinks() {
        Set<String> links = new HashSet<>(List.of("https://google.com/", "https://github.com/"));
        Mockito.when(mockUserRepository.getById(CHAT_ID)).thenReturn(new User(CHAT_ID, links));

        String expectedText = "Список отслеживаемых ссылок:\n- https://github.com/\n- https://google.com/\n";
        SendMessage msg = listCommand.handle(mockUpdate);
        boolean displayWebPagePreview = (boolean) msg.getParameters().get("disable_web_page_preview");

        assertMessage(expectedText, msg);
        Assertions.assertTrue(displayWebPagePreview);
    }
}
