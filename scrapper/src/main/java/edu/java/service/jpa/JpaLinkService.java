package edu.java.service.jpa;

import edu.java.model.Link;
import edu.java.repository.jpa.JpaLinkRepository;
import edu.java.service.LinkService;
import lombok.RequiredArgsConstructor;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
public class JpaLinkService implements LinkService {
    private final JpaLinkRepository linkRepository;
    private final JpaUserService userService;

    @Override
    public void addLink(Link link) {
        linkRepository.save(link);
    }

    @Override
    public void addLinkForUser(Long userId, Link link) {
        userService.checkThatUserChatExists(userId);

    }

    @Override
    public Optional<Link> getLinkById(Long id) {
        return linkRepository.findById(id);
    }

    @Override
    public Optional<Link> getLinkByUrl(String url) {
        return linkRepository.findByUrl(url);
    }

    @Override
    public List<Link> getAllLinks() {
        return linkRepository.findAll();
    }

    @Override
    public List<Link> getAllUserLinks(Long userId) {
        userService.checkThatUserChatExists(userId);
        return null;
    }

    @Override
    public List<Link> getOutdatedLinks(Long linksLimit, Long timeInterval) {
        return linkRepository.findAllOutdatedLinks(linksLimit, timeInterval);
    }

    @Override
    public void removeLink(Long id) {
        linkRepository.deleteById(id);
    }

    @Override
    public void removeUserLink(Long userId, Link link) {
        userService.checkThatUserChatExists(userId);
    }

    @Override
    public void updateLinkCheckedTime(Link link, OffsetDateTime newUpdateTime) {
        link.setCheckedAt(newUpdateTime);
        linkRepository.save(link);
    }
}
