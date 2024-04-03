package edu.java.scrapper.unit.service.update;

import edu.java.dto.Update;
import edu.java.dto.request.LinkUpdateRequest;
import edu.java.model.Link;
import edu.java.service.LinkService;
import edu.java.service.UserService;
import edu.java.service.update.LinkUpdateService;
import edu.java.service.update.handler.UpdateHandler;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Optional;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class LinkUpdateServiceTest {
    @Mock
    private UserService userService;

    @Mock
    private LinkService linkService;

    @Mock
    private UpdateHandler updateHandler;

    @Mock
    List<UpdateHandler> updateHandlers;

    @InjectMocks
    private LinkUpdateService linkUpdateService;

    @BeforeEach
    void setUp() {
        updateHandlers = List.of(updateHandler);
        linkUpdateService = new LinkUpdateService(userService, linkService, updateHandlers);
    }

    @Test
    void testFetchAllUpdates() {
        List<Link> links = List.of(new Link(1,
            "https://github.com",
            OffsetDateTime.of(2024, 3, 20, 21, 0, 0, 0, ZoneOffset.UTC))
        );

        when(updateHandler.supports(any())).thenReturn(true);
        when(updateHandler.fetchUpdate(any())).thenReturn(
            Optional.of(new Update(1, "https://github.com", "test description", OffsetDateTime.now()))
        );

        when(linkService.getOutdatedLinks(anyLong(), anyLong())).thenReturn(links);

        List<LinkUpdateRequest> updateRequests = linkUpdateService.fetchAllUpdates(10L, 1000L);

        assertThat(updateRequests).hasSize(1);
    }
}
