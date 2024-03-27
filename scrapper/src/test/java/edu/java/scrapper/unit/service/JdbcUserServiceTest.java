package edu.java.scrapper.unit.service;

import edu.java.exception.ChatNotFoundException;
import edu.java.exception.RegisteredUserExistsException;
import edu.java.model.Link;
import edu.java.model.User;
import edu.java.repository.jdbc.JdbcLinkRepository;
import edu.java.repository.jdbc.JdbcUserRepository;
import edu.java.service.jdbc.JdbcUserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.time.OffsetDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;;

@ExtendWith(MockitoExtension.class)
public class JdbcUserServiceTest {

    @Mock
    private JdbcUserRepository userRepository;

    @Mock
    private JdbcLinkRepository linkRepository;

    @InjectMocks
    private JdbcUserService userService;

    @Test
    void registerUserChat_WhenUserDoesNotExist_ShouldAddUser() {
        // Given
        User newUser = new User(1L, "username", new HashSet<>());
        when(userRepository.findById(newUser.getId())).thenReturn(Optional.empty());

        // When
        userService.registerUserChat(newUser);

        // Then
        verify(userRepository, times(1)).add(newUser);
    }

    @Test
    void registerUserChat_WhenUserExists_ShouldThrowException() {
        // Given
        User existingUser = new User(1L, "existingUser", new HashSet<>());
        when(userRepository.findById(existingUser.getId())).thenReturn(Optional.of(existingUser));

        // When, Then
        assertThrows(RegisteredUserExistsException.class, () -> userService.registerUserChat(existingUser));
    }

    @Test
    void unregisterUserChat_WhenUserExists_ShouldRemoveUser() {
        // Given
        Long userIdToRemove = 1L;
        when(userRepository.findById(userIdToRemove)).thenReturn(Optional.of(new User(userIdToRemove, "user", new HashSet<>())));

        // When
        userService.unregisterUserChat(userIdToRemove);

        // Then
        verify(userRepository, times(1)).remove(userIdToRemove);
    }

    @Test
    void unregisterUserChat_WhenUserDoesNotExist_ShouldThrowException() {
        // Given
        Long nonExistingUserId = 999L;
        when(userRepository.findById(nonExistingUserId)).thenReturn(Optional.empty());

        // When, Then
        assertThrows(ChatNotFoundException.class, () -> userService.unregisterUserChat(nonExistingUserId));
    }

    @Test
    void checkThatUserChatExists_WhenUserExists_ShouldNotThrowException() {
        // Given
        Long existingUserId = 1L;
        when(userRepository.findById(existingUserId)).thenReturn(Optional.of(new User(existingUserId, "user", new HashSet<>())));

        // When, Then
        assertDoesNotThrow(() -> userService.checkThatUserChatExists(existingUserId));
    }

    @Test
    void checkThatUserChatExists_WhenUserDoesNotExist_ShouldThrowException() {
        // Given
        Long nonExistingUserId = 999L;
        when(userRepository.findById(nonExistingUserId)).thenReturn(Optional.empty());

        // When, Then
        assertThrows(ChatNotFoundException.class, () -> userService.checkThatUserChatExists(nonExistingUserId));
    }

    @Test
    void getAllUserChatIdsByLinkId_ShouldReturnListOfIds() {
        // Given
        Long linkId = 1L;
        List<Long> expectedChatIds = List.of(1L, 2L, 3L);
        when(userRepository.getAllUserChatIdsByLinkId(linkId)).thenReturn(expectedChatIds);

        // When
        List<Long> actualChatIds = userService.getAllUserChatIdsByLinkId(linkId);

        // Then
        assertEquals(expectedChatIds, actualChatIds);
    }

    @Test
    void addLinkForUser_WhenUserChatExistsAndLinkExists_ShouldAddLinkForUser() {
        // Given
        Long userId = 1L;
        Link existingLink = new Link(1L, "https://example.com", OffsetDateTime.now());

        when(userRepository.findById(userId)).thenReturn(Optional.of(new User(userId, new HashSet<>())));
        when(linkRepository.findByUrl(existingLink.getUrl())).thenReturn(Optional.of(existingLink));

        // When
        userService.addLinkForUser(userId, existingLink);

        // Then
        verify(userRepository).addLinkForUser(userId, existingLink.getId());
    }

    @Test
    void addLinkForUser_WhenUserChatExistsAndLinkDoesNotExist_ShouldAddLinkForUser() {
        // Given
        Long userId = 1L;
        Link newLink = new Link(1L, "https://example.com", OffsetDateTime.now());

        when(userRepository.findById(userId)).thenReturn(Optional.of(new User(userId, new HashSet<>())));
        when(linkRepository.findByUrl(newLink.getUrl())).thenReturn(Optional.empty());


        // When
        userService.addLinkForUser(userId, newLink);

        // Then
        verify(userRepository).addLinkForUser(userId, 1L);
    }

    @Test
    void addLinkForUser_WhenUserChatDoesNotExist_ShouldThrowChatNotFoundException() {
        // Given
        Long userId = 1L;
        Link newLink = new Link(1L, "https://example.com", OffsetDateTime.now());

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        // When, Then
        assertThrows(ChatNotFoundException.class, () -> userService.addLinkForUser(userId, newLink));
    }
}

