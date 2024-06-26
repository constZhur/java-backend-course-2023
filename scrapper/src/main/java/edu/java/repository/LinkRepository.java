package edu.java.repository;

import edu.java.model.Link;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

public interface LinkRepository {
    void add(Link link);

    Optional<Link> findById(Integer id);

    Optional<Link> findByUrl(String url);

    Optional<Link> findUserLinkByUrl(Long userId, String url);

    List<Link> findAll();

    List<Link> findAllUserLinks(Long userId);

    List<Link> findOutdatedLinks(Long linksLimit, Long timeInterval);

    void remove(Integer id);

    void removeUserLink(Long userId, Link link);

    void updateCheckedTime(Link link, OffsetDateTime newUpdateTime);
}
