package edu.java.scrapper.unit.service;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.time.OffsetDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import edu.java.exception.ChatNotFoundException;
import edu.java.exception.LinkNotFoundException;
import edu.java.model.Link;
import edu.java.repository.jdbc.JdbcLinkRepository;
import edu.java.service.jdbc.JdbcLinkService;
import edu.java.service.jdbc.JdbcUserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class JdbcLinkServiceTest {

    @Mock
    private JdbcLinkRepository linkRepository;

    @Mock
    private JdbcUserService userService;

    @InjectMocks
    private JdbcLinkService linkService;

    private Link link;

    @BeforeEach
    void setUp() {
        link = new Link(1L, "https://example.com", OffsetDateTime.now());
    }

    @Test
    void addLink_ShouldCallRepositoryAdd() {
        // When
        linkService.addLink(link);

        // Then
        verify(linkRepository).add(link);
    }

    @Test
    void getLinkById_WhenLinkExists_ShouldReturnLink() {
        // Given
        Long linkId = 1L;
        when(linkRepository.findById(linkId)).thenReturn(Optional.of(link));

        // When
        Optional<Link> actualLink = linkService.getLinkById(linkId);

        // Then
        assertTrue(actualLink.isPresent());
        assertEquals(link, actualLink.get());
    }

    @Test
    void getLinkById_WhenLinkDoesNotExist_ShouldThrowException() {
        // Given
        Long linkId = 1L;
        when(linkRepository.findById(linkId)).thenReturn(Optional.empty());

        // When, Then
        assertThatThrownBy(() -> linkService.getLinkById(linkId))
            .isInstanceOf(LinkNotFoundException.class);
    }

    @Test
    void getLinkByUrl_WhenLinkExists_ShouldReturnLink() {
        // Given
        String url = "https://example.com";
        when(linkRepository.findByUrl(url)).thenReturn(Optional.of(link));

        // When
        Optional<Link> actualLink = linkService.getLinkByUrl(url);

        // Then
        assertTrue(actualLink.isPresent());
        assertEquals(link, actualLink.get());
    }

    @Test
    void getLinkByUrl_WhenLinkDoesNotExist_ShouldThrowException() {
        // Given
        String url = "https://example.com";
        when(linkRepository.findByUrl(url)).thenReturn(Optional.empty());

        // When, Then
        assertThrows(LinkNotFoundException.class, () -> {
            linkService.getLinkByUrl(url);
        });
        verify(linkRepository).findByUrl(url);
    }

    @Test
    void getAllLinks_ShouldReturnAllLinks() {
        // Given
        List<Link> expectedLinks = Arrays.asList(
            new Link(1L, "https://example1.com", OffsetDateTime.now()),
            new Link(2L, "https://example2.com", OffsetDateTime.now())
        );
        when(linkRepository.findAll()).thenReturn(expectedLinks);

        // When
        List<Link> actualLinks = linkService.getAllLinks();

        // Then
        assertEquals(expectedLinks.size(), actualLinks.size());
        assertTrue(actualLinks.containsAll(expectedLinks));
    }

    @Test
    void getAllUserLinks_WhenUserChatExists_ShouldReturnAllUserLinks() throws ChatNotFoundException {
        // Given
        Long userId = 1L;
        List<Link> expectedLinks = List.of(link);
        when(userService.checkThatUserChatExists(userId)).thenReturn(true);
        when(linkRepository.findAllUserLinks(userId)).thenReturn(expectedLinks);

        // When
        List<Link> actualLinks = linkService.getAllUserLinks(userId);

        // Then
        assertThat(actualLinks).isEqualTo(expectedLinks);
    }

    @Test
    void getAllUserLinks_WhenUserChatDoesNotExist_ShouldThrowException() {
        // Given
        Long userId = 1L;
        when(userService.checkThatUserChatExists(userId)).thenReturn(false); // Предположим, что пользователя не существует

        // When, Then
        assertThrows(ChatNotFoundException.class, () -> linkService.getAllUserLinks(userId));
    }

    @Test
    void getOutdatedLinks_ShouldReturnOutdatedLinks() {
        // Given
        Long linksLimit = 10L;
        Long timeIntervalSeconds = 24 * 60 * 60L;
        List<Link> expectedOutdatedLinks = List.of(link);
        when(linkRepository.findOutdatedLinks(linksLimit, timeIntervalSeconds)).thenReturn(expectedOutdatedLinks);

        // When
        List<Link> actualOutdatedLinks = linkService.getOutdatedLinks(linksLimit, timeIntervalSeconds);

        // Then
        assertThat(actualOutdatedLinks).isEqualTo(expectedOutdatedLinks);
    }

    @Test
    void removeLink_WhenLinkExists_ShouldRemoveLink() throws LinkNotFoundException {
        // Given
        Long linkId = 1L;
        when(linkRepository.findById(linkId)).thenReturn(Optional.of(new Link(linkId, "https://example.com", OffsetDateTime.now())));

        // When
        linkService.removeLink(linkId);

        // Then
        verify(linkRepository, times(1)).remove(linkId);
    }

    @Test
    void removeLink_WhenLinkDoesNotExist_ShouldThrowException() {
        // Given
        Long linkId = 1L;
        when(linkRepository.findById(linkId)).thenReturn(Optional.empty());

        // When, Then
        assertThrows(LinkNotFoundException.class, () -> linkService.removeLink(linkId));
    }

    @Test
    void removeUserLink_WhenUserChatExistsAndLinkExistsForUser_ShouldRemoveUserLink() throws ChatNotFoundException, LinkNotFoundException {
        // Given
        Long userId = 1L;
        Link userLink = new Link(1L, "https://example.com", OffsetDateTime.now());
        when(userService.checkThatUserChatExists(userId)).thenReturn(true); // Предположим, что пользователь существует
        when(linkRepository.findUserLinkByUrl(userId, userLink.getUrl())).thenReturn(Optional.of(userLink)); // Предположим, что ссылка пользователя существует

        // When
        linkService.removeUserLink(userId, userLink);

        // Then
        verify(linkRepository).removeUserLink(userId, userLink);
    }

    @Test
    void removeUserLink_WhenUserChatDoesNotExist_ShouldThrowException() {
        // Given
        Long userId = 1L;
        Link userLink = new Link(1L, "https://example.com", OffsetDateTime.now());
        when(userService.checkThatUserChatExists(userId)).thenReturn(false);

        // When, Then
        assertThrows(LinkNotFoundException.class, () -> linkService.removeUserLink(userId, userLink));
    }

    @Test
    void updateLinkCheckedTime_ShouldUpdateCheckedTime() {
        // Given
        Link linkToUpdate = new Link(1L, "https://example.com", OffsetDateTime.now());
        OffsetDateTime newUpdateTime = OffsetDateTime.now().plusHours(1);

        // When
        linkService.updateLinkCheckedTime(linkToUpdate, newUpdateTime);

        // Then
        verify(linkRepository).updateCheckedTime(linkToUpdate, newUpdateTime);
    }
}

