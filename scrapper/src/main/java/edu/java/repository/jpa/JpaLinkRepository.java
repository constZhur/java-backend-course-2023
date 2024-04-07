package edu.java.repository.jpa;

import edu.java.model.Link;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

public interface JpaLinkRepository extends JpaRepository<Link, Integer> {
    Optional<Link> findByUrl(String url);

    @Query(nativeQuery = true,
           value = """
                SELECT * FROM link WHERE EXTRACT(EPOCH FROM (CURRENT_TIMESTAMP - checked_at)) >= :timeInterval
                LIMIT :linksLimit
                   """
    )
    List<Link> findAllOutdatedLinks(@Param("linksLimit") Long linksLimit, @Param("timeInterval") Long timeInterval);

    @Query(nativeQuery = true,
           value = "SELECT l.* FROM link l JOIN link_chat_relations r ON l.id = r.link_id "
               + "WHERE r.chat_id = :userId")
    List<Link> findAllUserLinks(@Param("userId") Long userId);

    @Query(nativeQuery = true,
           value = "SELECT l.* FROM link l JOIN link_chat_relations r ON l.id = r.link_id "
               + "WHERE r.chat_id = :userId AND l.url = :url")
    Optional<Link> findUserLinkByUrl(@Param("userId") Long userId, @Param("url") String url);

    @Transactional
    @Modifying
    @Query(nativeQuery = true,
           value = "DELETE FROM link_chat_relations WHERE chat_id = :userId AND link_id = :linkId")
    void removeUserLink(@Param("userId") Long userId, @Param("linkId") Integer linkId);

    @Modifying
    @Transactional
    @Query(nativeQuery = true,
           value = "DELETE FROM link_chat_relations WHERE link_id = :id")
    void deleteLinkChatRelationsByLinkId(@Param("id") Integer id);
}
