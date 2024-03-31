package edu.java.repository.jpa;

import edu.java.model.User;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

public interface JpaUserRepository extends JpaRepository<User, Long> {
    @Query(nativeQuery = true,
           value = "SELECT chat_id FROM link_chat_relations WHERE link_id = :linkId")
    List<Long> getAllUserChatIdsByLinkId(@Param("linkId") Integer linkId);

    @Transactional
    @Modifying
    @Query(nativeQuery = true,
           value = "INSERT INTO link_chat_relations (chat_id, link_id) VALUES (:userId, :linkId)")
    void addLinkForUser(@Param("userId") Long userId, @Param("linkId") Integer linkId);
}
