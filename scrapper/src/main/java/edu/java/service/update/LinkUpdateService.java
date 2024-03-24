package edu.java.service.update;

import edu.java.dto.Update;
import edu.java.dto.request.LinkUpdateRequest;
import edu.java.exception.LinkNotSupportedException;
import edu.java.model.Link;
import edu.java.service.LinkService;
import edu.java.service.UserService;
import edu.java.service.update.handler.UpdateHandler;
import java.net.URI;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LinkUpdateService {
    private final UserService userService;
    private final LinkService linkService;
    private final List<UpdateHandler> updateHandlers;

    public List<LinkUpdateRequest> fetchAllUpdates(Long linksLimit, Long interval) {
        List<Link> links = linkService.getOutdatedLinks(linksLimit, interval);

        List<Update> updates = new ArrayList<>();
        for (var link : links) {
            updateHandlers.stream()
                .filter(handler -> handler.supports(URI.create(link.getUrl())))
                .findAny()
                .orElseThrow(LinkNotSupportedException::new)
                .fetchUpdate(link)
                .ifPresent(upd -> {
                    updates.add(upd);
                    linkService.updateLinkCheckedTime(link, OffsetDateTime.now());
                });
        }
        return updatesToLinkUpdateRequests(updates);
    }

    private List<LinkUpdateRequest> updatesToLinkUpdateRequests(List<Update> updates) {
        List<LinkUpdateRequest> linkUpdateRequests = new ArrayList<>();

        for (var update : updates) {
            List<Long> chatIds = userService.getAllUserChatIdsByLinkId(update.linkId());
            linkUpdateRequests.add(new LinkUpdateRequest(
                update.linkId(),
                URI.create(update.url()),
                update.description(),
                chatIds
            ));
        }
        return linkUpdateRequests;
    }
}
