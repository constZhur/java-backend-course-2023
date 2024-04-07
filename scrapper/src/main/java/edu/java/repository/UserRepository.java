package edu.java.repository;

import edu.java.model.User;
import java.util.List;
import java.util.Optional;

public interface UserRepository {

    void add(User userChat);

    void addLinkForUser(Long userId, Integer linkId);

    Optional<User> findById(Long id);

    void remove(Long id);

    List<User> findAll();

    List<Long> getAllUserChatIdsByLinkId(Integer linkId);
}
