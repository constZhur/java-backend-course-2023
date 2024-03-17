package edu.java.service;

import edu.java.model.Link;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

public interface LinkService {
    void addLink(Link link);
    void addLinkForUser(Long userId, Link link);
    Optional<Link> getLinkById(Long id);
    Optional<Link> getLinkByUrl(String url);
    List<Link> getAllLinks();
    List<Link> getAllUserLinks(Long userId);
    List<Link> getOutdatedLinks(Long linksLimit, Long timeInterval);
    void removeLink(Long id);
    void removeUserLink(Long userId, Link link);
    void updateLinkCheckedTime(Link link, OffsetDateTime newUpdateTime);
}
