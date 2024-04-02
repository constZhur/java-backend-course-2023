package edu.java.scrapper.unit.controller;

import edu.java.controller.ScrapperApiController;
import edu.java.dto.request.AddLinkRequest;
import edu.java.dto.request.RemoveLinkRequest;
import edu.java.dto.response.LinkResponse;
import edu.java.dto.response.ListLinksResponse;
import edu.java.model.Link;
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
import java.util.List;
import static org.assertj.core.api.Assertions.*;
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
//        RemoveLinkRequest removeLinkRequest = new RemoveLinkRequest(testUrl);
//        doNothing().when(linkService).remove(testChatId, removeLinkRequest.link());
//
//        ResponseEntity<LinkResponse> responseEntity = scrapperController.deleteLink(testChatId, removeLinkRequest);
//
//        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
//        verify(linkService, times(1)).remove(testChatId, removeLinkRequest.link());
    }

    @Test
    void testGetLinks() {
//        List<Link> mockLinks = List.of(mockLink);
//        when(linkService.findAll(testChatId)).thenReturn(mockLinks);
//
//        ResponseEntity<ListLinksResponse> responseEntity = scrapperController.getLinks(testChatId);
//
//        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
//        assertThat(responseEntity.getBody()).isNotNull();
//        assertThat(responseEntity.getBody().size()).isEqualTo(1);
//        assertThat(responseEntity.getBody().links()).hasSize(1);
    }

    @Test
    void testAddLink() {
//        AddLinkRequest addLinkRequest = new AddLinkRequest(testUrl);
//        when(linkService.add(testChatId, addLinkRequest.link())).thenReturn(mockLink);
//
//        ResponseEntity<LinkResponse> responseEntity = scrapperController.addLink(testChatId, addLinkRequest);
//
//        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
//        assertThat(responseEntity.getBody()).isNotNull();
    }

    @Test
    void testDeleteChat() {
//        doNothing().when(userService).unregister(testChatId);
//
//        ResponseEntity<Void> responseEntity = scrapperController.deleteChat(testChatId);
//
//        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
//        verify(chatService, times(1)).unregister(testChatId);
    }

    @Test
    void testAddChat() {
//        doNothing().when(userService).register(testChatId);
//
//        ResponseEntity<Void> responseEntity = scrapperController.addChat(testChatId);
//
//        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
//        verify(chatService, times(1)).register(testChatId);
    }
}
