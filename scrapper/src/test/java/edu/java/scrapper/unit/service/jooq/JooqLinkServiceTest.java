package edu.java.scrapper.unit.service.jooq;

import edu.java.exception.ChatNotFoundException;
import edu.java.exception.LinkNotFoundException;
import edu.java.model.Link;
import edu.java.repository.jooq.JooqLinkRepository;
import edu.java.service.jooq.JooqLinkService;
import edu.java.service.jooq.JooqUserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.time.OffsetDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class JooqLinkServiceTest {

    @Mock
    private JooqLinkRepository linkRepository;

    @Mock
    private JooqUserService userService;

    @InjectMocks
    private JooqLinkService linkService;

    private Link link;

    @BeforeEach
    void setUp() {
        link = new Link(1, "https://example.com", OffsetDateTime.now());
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
        Integer linkId = 1;
        when(linkRepository.findById(linkId)).thenReturn(Optional.of(link));

        // When
        Optional<Link> actualLink = linkService.getLinkById(linkId);

        // Then
        assertThat(actualLink).isPresent().contains(link);
    }

    @Test
    void getLinkById_WhenLinkDoesNotExist_ShouldThrowException() {
        // Given
        Integer linkId = 1;
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
        assertThat(actualLink).isPresent().contains(link);
    }

    @Test
    void getLinkByUrl_WhenLinkDoesNotExist_ShouldThrowException() {
        // Given
        String url = "https://example.com";
        when(linkRepository.findByUrl(url)).thenReturn(Optional.empty());

        // When, Then
        assertThatThrownBy(() -> linkService.getLinkByUrl(url))
            .isInstanceOf(LinkNotFoundException.class);
    }

    @Test
    void getAllLinks_ShouldReturnAllLinks() {
        // Given
        List<Link> expectedLinks = Arrays.asList(
            new Link(1, "https://example1.com", OffsetDateTime.now()),
            new Link(2, "https://example2.com", OffsetDateTime.now())
        );
        when(linkRepository.findAll()).thenReturn(expectedLinks);

        // When
        List<Link> actualLinks = linkService.getAllLinks();

        // Then
        assertThat(actualLinks).containsExactlyElementsOf(expectedLinks);
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
        when(userService.checkThatUserChatExists(userId)).thenReturn(false);

        // When, Then
        assertThatThrownBy(() -> linkService.getAllUserLinks(userId))
            .isInstanceOf(ChatNotFoundException.class);
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
        Integer linkId = 1;
        when(linkRepository.findById(linkId)).thenReturn(Optional.of(new Link(linkId, "https://example.com", OffsetDateTime.now())));

        // When
        linkService.removeLink(linkId);

        // Then
        verify(linkRepository, times(1)).remove(linkId);
    }

    @Test
    void removeLink_WhenLinkDoesNotExist_ShouldThrowException() {
        // Given
        Integer linkId = 1;
        when(linkRepository.findById(linkId)).thenReturn(Optional.empty());

        // When, Then
        assertThatThrownBy(() -> linkService.removeLink(linkId))
            .isInstanceOf(LinkNotFoundException.class);
    }

    @Test
    void removeUserLink_WhenUserChatExistsAndLinkExistsForUser_ShouldRemoveUserLink() throws ChatNotFoundException, LinkNotFoundException {
        // Given
        Long userId = 1L;
        Link userLink = new Link(1, "https://example.com", OffsetDateTime.now());
        when(userService.checkThatUserChatExists(userId)).thenReturn(true);
        when(linkRepository.findUserLinkByUrl(userId, userLink.getUrl())).thenReturn(Optional.of(userLink));

        // When
        linkService.removeUserLink(userId, userLink);

        // Then
        verify(linkRepository).removeUserLink(userId, userLink);
    }

    @Test
    void removeUserLink_WhenUserChatDoesNotExist_ShouldThrowException() {
        // Given
        Long userId = 1L;
        Link userLink = new Link(1, "https://example.com", OffsetDateTime.now());
        when(userService.checkThatUserChatExists(userId)).thenReturn(false);
        when(linkRepository.findUserLinkByUrl(userId, userLink.getUrl())).thenReturn(Optional.empty());

        // When, Then
        assertThatThrownBy(() -> linkService.removeUserLink(userId, userLink))
            .isInstanceOf(LinkNotFoundException.class);
    }

    @Test
    void updateLinkCheckedTime_ShouldUpdateCheckedTime() {
        // Given
        Link linkToUpdate = new Link(1, "https://example.com", OffsetDateTime.now());
        OffsetDateTime newUpdateTime = OffsetDateTime.now().plusHours(1);

        // When
        linkService.updateLinkCheckedTime(linkToUpdate, newUpdateTime);

        // Then
        verify(linkRepository).updateCheckedTime(linkToUpdate, newUpdateTime);
    }
}
