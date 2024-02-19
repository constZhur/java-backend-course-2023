package edu.java.bot.commands;

import edu.java.bot.models.User;
import edu.java.bot.parsers.LinkParser;
import edu.java.bot.repositories.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
public class TrackCommandTest extends AbstractCommandTest {
    @Mock
    private UserRepository mockUserRepository;

    @Mock
    private LinkParser mockLinkParser;

    @Mock
    private User mockUser;

    @InjectMocks
    private TrackCommand trackCommand;

    @BeforeEach
    public void setUp(){
        trackCommand = new TrackCommand(mockUserRepository, List.of(mockLinkParser));
        Mockito.when(mockUpdate.message()).thenReturn(mockMessage);
        Mockito.when(mockMessage.chat()).thenReturn(mockChat);
        Mockito.when(mockChat.id()).thenReturn(CHAT_ID);
    }

    @Test
    public void testHandleWithRequestForLinkEntry() {
        assertMessage("Введите ссылку для отслеживания:", trackCommand.handle(mockUpdate));
    }

    @Test
    public void testHandleWithInvalidLink() {
        Mockito.when(mockMessage.text()).thenReturn("github");
        Mockito.when(mockUserRepository.getById(CHAT_ID)).thenReturn(mockUser);

        assertMessage("Неверный формат ссылки", trackCommand.handle(mockUpdate));
    }

    @Test
    public void testHandleWithUnsupportedResource() {
        Mockito.when(mockMessage.text()).thenReturn("https://edu.tinkoff.ru");
        Mockito.when(mockUserRepository.getById(CHAT_ID)).thenReturn(mockUser);
        Mockito.when(mockLinkParser.parseLink(Mockito.any())).thenReturn(false);

        assertMessage("Данный ресурс не поддерживается!", trackCommand.handle(mockUpdate));
    }

    @Test
    public void testHandleWhenLinkAlreadyTracked() {
        Set<String> trackedLinks = new HashSet<>();
        trackedLinks.add("https://github.com");

        Mockito.when(mockMessage.text()).thenReturn("https://github.com");
        Mockito.when(mockUserRepository.getById(CHAT_ID)).thenReturn(mockUser);
        Mockito.when(mockUser.getLinks()).thenReturn(trackedLinks);
        Mockito.when(mockLinkParser.parseLink(Mockito.any())).thenReturn(true);

        assertMessage("Данный ресурс уже отслеживается", trackCommand.handle(mockUpdate));
    }

    @Test
    public void testHandleWhenLinkAddedSuccessfully() {
        Set<String> trackedLinks = new HashSet<>();

        Mockito.when(mockMessage.text()).thenReturn("https://github.com");
        Mockito.when(mockUserRepository.getById(CHAT_ID)).thenReturn(mockUser);
        Mockito.when(mockUser.getLinks()).thenReturn(trackedLinks);
        Mockito.when(mockLinkParser.parseLink(Mockito.any())).thenReturn(true);

        assertMessage("Ссылка добавлена!", trackCommand.handle(mockUpdate));
        assertThat(trackedLinks).contains("https://github.com");
    }

}
