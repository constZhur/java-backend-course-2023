package edu.java.scrapper.unit.service.jpa;

import edu.java.exception.ChatNotFoundException;
import edu.java.exception.LinkNotFoundException;
import edu.java.model.Link;
import edu.java.model.User;
import edu.java.repository.jpa.JpaLinkRepository;
import edu.java.service.jpa.JpaLinkService;
import edu.java.service.jpa.JpaUserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
class JpaLinkServiceTest {

    @Mock
    private JpaLinkRepository linkRepository;

    @Mock
    private JpaUserService userService;

    @InjectMocks
    private JpaLinkService linkService;

    private User user;
    private Link link;

    @BeforeEach
    void setUp() {
        user = new User(1L, "constZhur", new HashSet<>());

        link = new Link(1,
            "https://github.com/constZhur/java-backend-course-2023",
            OffsetDateTime.of(2024, 3, 20, 21, 0, 0, 0, ZoneOffset.UTC));

    }

    @Test
    void addLink_ShouldCallRepositorySave() {
        linkService.addLink(link);

        verify(linkRepository).save(link);
    }

    @Test
    void getLinkById_WhenLinkExists_ShouldReturnLink() {
        Integer linkId = link.getId();
        when(linkRepository.findById(linkId)).thenReturn(Optional.of(link));

        Optional<Link> actualLink = linkService.getLinkById(linkId);

        assertTrue(actualLink.isPresent());
        assertEquals(link, actualLink.get());
    }

    @Test
    void getLinkById_WhenLinkDoesNotExist_ShouldThrowException() {
        Integer linkId = link.getId();
        when(linkRepository.findById(linkId)).thenReturn(Optional.empty());

        assertThrows(LinkNotFoundException.class, () -> linkService.getLinkById(linkId));
    }

    @Test
    void getLinkByUrl_WhenLinkExists_ShouldReturnLink() {
        String url = link.getUrl();
        when(linkRepository.findByUrl(url)).thenReturn(Optional.of(link));

        Optional<Link> actualLink = linkService.getLinkByUrl(url);

        assertTrue(actualLink.isPresent());
        assertEquals(link, actualLink.get());
    }

    @Test
    void getLinkByUrl_WhenLinkDoesNotExist_ShouldThrowException() {
        String url = link.getUrl();
        when(linkRepository.findByUrl(url)).thenReturn(Optional.empty());

        assertThrows(LinkNotFoundException.class, () -> linkService.getLinkByUrl(url));
    }

    @Test
    void getAllLinks_ShouldReturnAllLinks() {
        List<Link> expectedLinks = Arrays.asList(
            new Link(1, "https://example1.com", OffsetDateTime.now()),
            new Link(2, "https://example2.com", OffsetDateTime.now())
        );
        when(linkRepository.findAll()).thenReturn(expectedLinks);

        List<Link> actualLinks = linkService.getAllLinks();

        assertEquals(expectedLinks.size(), actualLinks.size());
        assertTrue(actualLinks.containsAll(expectedLinks));
    }

    @Test
    void getAllUserLinks_WhenUserChatExists_ShouldReturnAllUserLinks() throws ChatNotFoundException {
        Long userId = user.getId();
        List<Link> expectedLinks = List.of(link);
        when(userService.checkThatUserChatExists(userId)).thenReturn(true);
        when(linkRepository.findAllUserLinks(userId)).thenReturn(expectedLinks);

        List<Link> actualLinks = linkService.getAllUserLinks(userId);

        assertThat(actualLinks).isEqualTo(expectedLinks);
    }

    @Test
    void getAllUserLinks_WhenUserChatDoesNotExist_ShouldThrowException() {
        Long userId = user.getId();
        when(userService.checkThatUserChatExists(userId)).thenReturn(false);

        assertThrows(ChatNotFoundException.class, () -> linkService.getAllUserLinks(userId));
    }

    @Test
    void getOutdatedLinks_ShouldReturnOutdatedLinks() {
        Long linksLimit = 10L;
        Long timeIntervalSeconds = 24 * 60 * 60L;
        List<Link> expectedOutdatedLinks = List.of(link);
        when(linkRepository.findAllOutdatedLinks(linksLimit, timeIntervalSeconds)).thenReturn(expectedOutdatedLinks);

        List<Link> actualOutdatedLinks = linkService.getOutdatedLinks(linksLimit, timeIntervalSeconds);

        assertThat(actualOutdatedLinks).isEqualTo(expectedOutdatedLinks);
    }

    @Test
    void removeLink_WhenLinkExists_ShouldRemoveLink() throws LinkNotFoundException {
        Integer linkId = link.getId();
        when(linkRepository.findById(linkId)).thenReturn(Optional.of(new Link(linkId, "https://example.com", OffsetDateTime.now())));

        linkService.removeLink(linkId);

        verify(linkRepository, times(1)).deleteById(linkId);
        verify(linkRepository, times(1)).deleteLinkChatRelationsByLinkId(linkId);
    }

    @Test
    void removeLink_WhenLinkDoesNotExist_ShouldThrowException() {
        Integer linkId = link.getId();
        when(linkRepository.findById(linkId)).thenReturn(Optional.empty());

        assertThrows(LinkNotFoundException.class, () -> linkService.removeLink(linkId));
    }

    @Test
    void removeUserLink_WhenUserChatExistsAndLinkExistsForUser_ShouldRemoveUserLink() throws ChatNotFoundException, LinkNotFoundException {
        Long userId = user.getId();
        when(userService.checkThatUserChatExists(userId)).thenReturn(true);
        when(linkRepository.findUserLinkByUrl(userId, link.getUrl())).thenReturn(Optional.of(link));

        linkService.removeUserLink(userId, link);

        verify(linkRepository).removeUserLink(userId, link.getId());
    }

    @Test
    void removeUserLink_WhenUserChatDoesNotExist_ShouldThrowException() {
        Long userId = user.getId();;
        when(userService.checkThatUserChatExists(userId)).thenReturn(false);

        assertThrows(LinkNotFoundException.class, () -> linkService.removeUserLink(userId, link));
    }

    @Test
    void updateLinkCheckedTime_ShouldUpdateCheckedTime() {
        Link linkToUpdate = new Link(1, "https://example.com", OffsetDateTime.now());
        OffsetDateTime newUpdateTime = OffsetDateTime.now().plusHours(1);

        linkService.updateLinkCheckedTime(linkToUpdate, newUpdateTime);

        assertEquals(newUpdateTime, linkToUpdate.getCheckedAt());
        verify(linkRepository).save(linkToUpdate);
    }
}
