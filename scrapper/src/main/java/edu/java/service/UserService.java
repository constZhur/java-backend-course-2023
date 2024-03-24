package edu.java.service;

import edu.java.model.User;
import java.util.List;

public interface UserService {
    void registerUserChat(User user);

    void unregisterUserChat(Long id);

    void checkThatUserChatExists(Long id);

    List<Long> getAllUserChatIdsByLinkId(Long linkId);
}
