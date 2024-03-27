package edu.java.service.jdbc;

import edu.java.exception.ChatNotFoundException;
import edu.java.exception.LinkNotFoundException;
import edu.java.model.Link;
import edu.java.repository.jdbc.JdbcLinkRepository;
import edu.java.service.LinkService;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class JdbcLinkService implements LinkService {
    private final JdbcLinkRepository linkRepository;
    private final JdbcUserService userService;

    @Override
    public void addLink(Link link) {
        linkRepository.add(link);
    }

    @Override
    public Optional<Link> getLinkById(Long id) {
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
        return linkRepository.findOutdatedLinks(linksLimit, timeInterval);
    }

    @Override
    public void removeLink(Long id) {
        Optional<Link> foundLink = linkRepository.findById(id);
        if (foundLink.isEmpty()) {
            throw new LinkNotFoundException("Link with id " + id + " not found");
        }
        linkRepository.remove(id);
    }

    @Override
    public void removeUserLink(Long userId, Link link) {
        userService.checkThatUserChatExists(userId);

        Link linkToDelete = linkRepository.findUserLinkByUrl(userId, link.getUrl())
            .orElseThrow(LinkNotFoundException::new);

        linkRepository.removeUserLink(userId, linkToDelete);
    }

    @Override
    public void updateLinkCheckedTime(Link link, OffsetDateTime newUpdateTime) {
        linkRepository.updateCheckedTime(link, newUpdateTime);
    }
}
