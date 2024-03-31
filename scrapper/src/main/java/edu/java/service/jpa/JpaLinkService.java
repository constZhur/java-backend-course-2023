package edu.java.service.jpa;

import edu.java.exception.ChatNotFoundException;
import edu.java.exception.LinkNotFoundException;
import edu.java.model.Link;
import edu.java.repository.jpa.JpaLinkRepository;
import edu.java.service.LinkService;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
public class JpaLinkService implements LinkService {
    private final JpaLinkRepository linkRepository;
    private final JpaUserService userService;

    @Override
    public void addLink(Link link) {
        linkRepository.save(link);
    }

    @Override
    public Optional<Link> getLinkById(Integer id) {
        Optional<Link> foundLink = linkRepository.findById(id);

        if (foundLink.isEmpty()) {
            throw new LinkNotFoundException();
        }

        return foundLink;
    }

    @Override
    public Optional<Link> getLinkByUrl(String url) {
        Optional<Link> foundLink = linkRepository.findByUrl(url);

        if (foundLink.isEmpty()) {
            throw new LinkNotFoundException();
        }

        return foundLink;
    }

    @Override
    public List<Link> getAllLinks() {
        return linkRepository.findAll();
    }

    @Override
    public List<Link> getAllUserLinks(Long userId) {
        if (!userService.checkThatUserChatExists(userId)) {
            throw new ChatNotFoundException();
        }

        return linkRepository.findAllUserLinks(userId);
    }

    @Override
    public List<Link> getOutdatedLinks(Long linksLimit, Long timeInterval) {
        return linkRepository.findAllOutdatedLinks(linksLimit, timeInterval);
    }

    @Override
    @Transactional
    public void removeLink(Integer id) {
        Optional<Link> foundLink = linkRepository.findById(id);
        if (foundLink.isEmpty()) {
            throw new LinkNotFoundException();
        }
        linkRepository.deleteById(id);
        linkRepository.deleteLinkChatRelationsByLinkId(id);
    }

    @Override
    public void removeUserLink(Long userId, Link link) {
        userService.checkThatUserChatExists(userId);

        Link linkToDelete = linkRepository.findUserLinkByUrl(userId, link.getUrl())
            .orElseThrow(LinkNotFoundException::new);

        linkRepository.removeUserLink(userId, linkToDelete.getId());
    }

    @Override
    public void updateLinkCheckedTime(Link link, OffsetDateTime newUpdateTime) {
        link.setCheckedAt(newUpdateTime);
        linkRepository.save(link);
    }
}
