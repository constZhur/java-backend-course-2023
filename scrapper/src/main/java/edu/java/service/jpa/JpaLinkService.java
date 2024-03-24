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

    @Override
    public void addLink(Link link) {
    }

    @Override
    public void addLinkForUser(Long userId, Link link) {

    }

    @Override
    public Optional<Link> getLinkById(Long id) {
        return linkRepository.findById(id);
    }

    @Override
    public Optional<Link> getLinkByUrl(String url) {
    }

    @Override
    public List<Link> getAllLinks() {
        return linkRepository.findAll();
    }

    @Override
    public List<Link> getAllUserLinks(Long userId) {
        return null;
    }

    @Override
    public List<Link> getOutdatedLinks(Long linksLimit, Long timeInterval) {
        return null;
    }

    @Override
    public void removeLink(Long id) {
        linkRepository.deleteById(id);
    }

    @Override
    public void removeUserLink(Long userId, Link link) {

    }

    @Override
    public void updateLinkCheckedTime(Link link, OffsetDateTime newUpdateTime) {
        Link foundLink = linkRepository.findById(link.getId()).get();
        foundLink.setCheckedAt(newUpdateTime);
        linkRepository.save(foundLink);
    }
}
