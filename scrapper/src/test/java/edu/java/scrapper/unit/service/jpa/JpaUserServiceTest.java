package edu.java.scrapper.unit.service.jpa;

import edu.java.exception.ChatNotFoundException;
import edu.java.exception.RegisteredUserExistsException;
import edu.java.model.Link;
import edu.java.model.User;
import edu.java.repository.jpa.JpaLinkRepository;
import edu.java.repository.jpa.JpaUserRepository;
import edu.java.service.jpa.JpaUserService;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class JpaUserServiceTest {

    @Mock
    private JpaUserRepository userRepository;

    @Mock
    private JpaLinkRepository linkRepository;

    @InjectMocks
    private JpaUserService userService;

    static User user;
    static Link link;

    @BeforeAll
    public static void setUp(){
        user = new User(1L, new HashSet<>());

        link = new Link(1,
            "https://github.com/constZhur/java-backend-course-2023",
            OffsetDateTime.of(2024, 3, 20, 21, 0, 0, 0, ZoneOffset.UTC));

    }

    @Test
    void registerUserChat_WhenUserDoesNotExist_ShouldAddUser() {
        when(userRepository.existsById(user.getId())).thenReturn(false);

        userService.registerUserChat(user);

        verify(userRepository).save(user);
    }

    @Test
    void registerUserChat_WhenUserExists_ShouldThrowException() {
        when(userRepository.existsById(user.getId())).thenReturn(true);

        assertThrows(RegisteredUserExistsException.class, () -> userService.registerUserChat(user));
    }

    @Test
    void unregisterUserChat_WhenUserExists_ShouldRemoveUser() {
        Long userIdToRemove = user.getId();
        when(userRepository.existsById(userIdToRemove)).thenReturn(true);

        userService.unregisterUserChat(userIdToRemove);

        verify(userRepository).deleteById(userIdToRemove);
    }

    @Test
    void unregisterUserChat_WhenUserDoesNotExist_ShouldThrowException() {
        Long nonExistingUserId = 999L;
        when(userRepository.existsById(nonExistingUserId)).thenReturn(false);

        assertThrows(ChatNotFoundException.class, () -> userService.unregisterUserChat(nonExistingUserId));
    }

    @Test
    void checkThatUserChatExists_WhenUserExists_ShouldNotThrowException() {
        Long existingUserId = user.getId();
        when(userRepository.existsById(existingUserId)).thenReturn(true);

        assertDoesNotThrow(() -> userService.checkThatUserChatExists(existingUserId));
    }

    @Test
    void checkThatUserChatExists_WhenUserDoesNotExist_ShouldThrowException() {
        Long nonExistingUserId = 999L;
        when(userRepository.existsById(nonExistingUserId)).thenReturn(false);

        assertThrows(ChatNotFoundException.class, () -> userService.checkThatUserChatExists(nonExistingUserId));
    }

    @Test
    void getAllUserChatIdsByLinkId_ShouldReturnListOfIds() {
        Integer linkId = link.getId();
        List<Long> expectedChatIds = List.of(1L, 2L, 3L);
        when(userRepository.getAllUserChatIdsByLinkId(linkId)).thenReturn(expectedChatIds);

        List<Long> actualChatIds = userService.getAllUserChatIdsByLinkId(linkId);

        assertEquals(expectedChatIds, actualChatIds);
    }

    @Test
    void addLinkForUser_WhenUserChatExistsAndLinkExists_ShouldAddLinkForUser() {
        Long userId = user.getId();

        when(userRepository.existsById(userId)).thenReturn(true);
        when(linkRepository.findByUrl(link.getUrl())).thenReturn(Optional.of(link));
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        userService.addLinkForUser(userId, link);

        verify(userRepository).addLinkForUser(userId, link.getId());
    }

    @Test
    void addLinkForUser_WhenUserChatExistsAndLinkDoesNotExist_ShouldAddLinkForUser() {
        Long userId = user.getId();

        when(userRepository.existsById(userId)).thenReturn(true);
        when(linkRepository.findByUrl(link.getUrl())).thenReturn(Optional.empty());
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        userService.addLinkForUser(userId, link);

        verify(linkRepository).save(link);
        verify(userRepository).addLinkForUser(userId, link.getId());
    }


    @Test
    void addLinkForUser_WhenUserChatDoesNotExist_ShouldThrowChatNotFoundException() {
        Long userId = user.getId();

        when(userRepository.existsById(userId)).thenReturn(false);

        assertThrows(ChatNotFoundException.class, () -> userService.addLinkForUser(userId, link));
    }
}

