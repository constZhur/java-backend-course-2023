package edu.java.scrapper.unit.controller;

import edu.java.controller.ScrapperApiController;
import edu.java.dto.request.AddLinkRequest;
import edu.java.dto.request.RemoveLinkRequest;
import edu.java.dto.response.LinkResponse;
import edu.java.dto.response.ListLinksResponse;
import edu.java.model.Link;
import edu.java.model.User;
import edu.java.service.LinkService;
import edu.java.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import java.net.URI;
import java.net.URISyntaxException;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class ScrapperApiControllerTest {
    @Mock
    private Link mockLink;

    @Mock
    private UserService userService;

    @Mock
    private LinkService linkService;

    @InjectMocks
    private ScrapperApiController scrapperController;

    private final Long testChatId = 1L;
    private final String testUrl = "http://github.com/user/repo";

    @BeforeEach
    void setUp() {
        when(mockLink.getId()).thenReturn(1);
        when(mockLink.getUrl()).thenReturn(testUrl);
    }

    @Test
    void testDeleteLink() {
        RemoveLinkRequest removeLinkRequest = new RemoveLinkRequest(testUrl);
        Link linkToDelete = new Link(removeLinkRequest.link());


        doNothing().when(linkService).removeUserLink(testChatId, linkToDelete);

        ResponseEntity<LinkResponse> responseEntity = scrapperController.deleteLink(testChatId, removeLinkRequest);

        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        verify(linkService, times(1)).removeUserLink(eq(testChatId), any(Link.class));
    }

    @Test
    void testGetLinks() {
        List<Link> mockLinks = List.of(mockLink);
        when(linkService.getAllUserLinks(testChatId)).thenReturn(mockLinks);

        ResponseEntity<ListLinksResponse> responseEntity = scrapperController.getLinks(testChatId);

        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(responseEntity.getBody()).isNotNull();
        assertThat(responseEntity.getBody().size()).isEqualTo(1);
        assertThat(responseEntity.getBody().links()).hasSize(1);
    }

    @Test
    void testAddLink() throws URISyntaxException {
        AddLinkRequest addLinkRequest = new AddLinkRequest(testUrl);
        Link newLink = new Link(1, testUrl, OffsetDateTime.now());

        doNothing().when(userService).addLinkForUser(eq(testChatId), any(Link.class)); // Используем any для Link
        when(linkService.getLinkByUrl(eq(addLinkRequest.link()))).thenReturn(Optional.of(newLink));

        ResponseEntity<LinkResponse> responseEntity = scrapperController.addLink(testChatId, addLinkRequest);

        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(responseEntity.getBody()).isNotNull();
        assertThat(responseEntity.getBody().id()).isEqualTo(1);
        assertThat(responseEntity.getBody().url()).isEqualTo(URI.create(testUrl));

        verify(userService, times(1)).addLinkForUser(eq(testChatId), any(Link.class)); // Используем any для Link
        verify(linkService, times(1)).getLinkByUrl(addLinkRequest.link());
    }

    @Test
    void testDeleteChat() {
        doNothing().when(userService).unregisterUserChat(testChatId);

        ResponseEntity<Void> responseEntity = scrapperController.deleteChat(testChatId);

        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        verify(userService, times(1)).unregisterUserChat(testChatId);
    }

    @Test
    void testAddChat() {
        doNothing().when(userService).registerUserChat(new User(testChatId));

        ResponseEntity<Void> responseEntity = scrapperController.addChat(testChatId);

        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        verify(userService, times(1)).registerUserChat(any(User.class));
    }
}
