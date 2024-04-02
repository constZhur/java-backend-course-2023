package edu.java.bot.controller;

import edu.java.bot.dto.request.LinkUpdateRequest;
import edu.java.bot.service.UpdateService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import java.net.URI;
import java.util.List;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class BotApiControllerTest {
    @Mock
    private UpdateService updateService;

    @InjectMocks
    private BotApiController botApiController;

    @Test
    public void testUpdatesPost_ValidRequest_SuccessResponse() {
        LinkUpdateRequest request = new LinkUpdateRequest(1L,
            URI.create("https://github.com/"),
            "github description", List.of(1L, 2L)
        );

        ResponseEntity<Void> responseEntity = botApiController.updatesPost(request);

        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        verify(updateService, times(1)).processUpdate(request);
    }
}
