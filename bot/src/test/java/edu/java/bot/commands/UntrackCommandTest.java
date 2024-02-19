package edu.java.bot.commands;

import com.pengrad.telegrambot.request.SendMessage;
import edu.java.bot.models.User;
import edu.java.bot.repositories.UserRepository;
import edu.java.bot.repositories.UserRepositoryImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.HashSet;

@ExtendWith(MockitoExtension.class)
public class UntrackCommandTest extends AbstractCommandTest {
    @Mock
    private UserRepository mockUserRepository;

    @Mock
    private User mockUser;

    @InjectMocks
    private UntrackCommand untrackCommand;

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
    public void testHandleWithRequestForLinkEntry() {
        setUpMocks();
        String expectedText = "Введите ссылку, за которой необходимо перестать следить:";
        SendMessage msg = untrackCommand.handle(mockUpdate);

        assertMessage(expectedText, msg);
    }

    @Test
    public void testHandleWhenNoSuchLink() {
        setUpMocks();
        Mockito.when(mockMessage.text()).thenReturn("https://github.com");
        Mockito.when(mockUserRepository.getById(1L)).thenReturn(null);

        String expectedText = "Пользователь не найден.\n"
            + "Перезапустите бота с помощью /start";
        SendMessage msg = untrackCommand.handle(mockUpdate);

        assertMessage(expectedText, msg);
    }

    @Test
    public void testHandleWhenLinkRemovedSuccessfully() {
        setUpMocks();
        Mockito.when(mockMessage.text()).thenReturn("https://github.com");
        HashSet<String> trackedLinks = new HashSet<>();
        trackedLinks.add("https://github.com");
        Mockito.when(mockUser.getLinks()).thenReturn(trackedLinks);
        Mockito.when(mockUserRepository.getById(CHAT_ID)).thenReturn(mockUser);

        String expectedText = "Отслеживание ссылки прекращено!";;
        SendMessage msg = untrackCommand.handle(mockUpdate);

        assertMessage(expectedText, msg);
    }

    @Test
    public void testHandle_LinkNotTracked() {
        setUpMocks();
        Mockito.when(mockMessage.text()).thenReturn("https://github.com");
        Mockito.when(mockUser.getLinks()).thenReturn(new HashSet<>());
        Mockito.when(mockUserRepository.getById(CHAT_ID)).thenReturn(mockUser);

        String expectedText = "Данного ресурса нет среди отслеживаемых";
        SendMessage msg = untrackCommand.handle(mockUpdate);

        assertMessage(expectedText, msg);
    }

}
