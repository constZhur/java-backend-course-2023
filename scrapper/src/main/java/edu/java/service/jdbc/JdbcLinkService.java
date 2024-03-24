package edu.java.service.jdbc;

import edu.java.exception.AddedLinkExistsException;
import edu.java.exception.ChatNotFoundException;
import edu.java.exception.LinkNotFoundException;
import edu.java.model.Link;
import edu.java.repository.jdbc.JdbcLinkRepository;
import edu.java.service.LinkService;
import lombok.RequiredArgsConstructor;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
public class JdbcLinkService implements LinkService {
    private final JdbcLinkRepository linkRepository;
    private final JdbcUserService userService;

    @Override
    public void addLink(Link link) {
        linkRepository.add(link);
    }

    @Override
    public void addLinkForUser(Long userId, Link link) {
        userService.checkThatUserChatExists(userId);

        Optional<Link> foundLink = linkRepository.findUserLinkByUrl(userId, link.getUrl());
        if (foundLink.isPresent()) {
            throw new AddedLinkExistsException();
        }

        foundLink = linkRepository.findLinkByUrl(link.getUrl());
        if (foundLink.isEmpty()){

        }
    }

    @Override
    public Optional<Link> getLinkById(Long id) throws LinkNotFoundException {
        return linkRepository.findById(id);
    }

    @Override
    public Optional<Link> getLinkByUrl(String url) {
        return Optional.empty();
    }

    @Override
    public List<Link> getAllLinks() {
        return linkRepository.findAll();
    }

    @Override
    public List<Link> getAllUserLinks(Long userId) throws ChatNotFoundException {
        userService.checkThatUserChatExists(userId);
        return linkRepository.findAllUserLinks(userId);
    }

    @Override
    public List<Link> getOutdatedLinks(Long linksLimit, Long timeInterval) {
        return linkRepository.findOutdatedLinks(linksLimit, timeInterval);
    }

    @Override
    public void removeLink(Long id) throws LinkNotFoundException{
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
