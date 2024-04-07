package edu.java.service;

import edu.java.model.Link;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

public interface LinkService {

    void addLink(Link link);

    Optional<Link> getLinkById(Integer id);

    Optional<Link> getLinkByUrl(String url);

    List<Link> getAllLinks();

    List<Link> getAllUserLinks(Long userId);

    List<Link> getOutdatedLinks(Long linksLimit, Long timeInterval);

    void removeLink(Integer id);

    void removeUserLink(Long userId, Link link);

    void updateLinkCheckedTime(Link link, OffsetDateTime newUpdateTime);
}
