package edu.java.service;

import edu.java.model.User;

public interface UserService {
    void registerUserChat(User user);

    void unregisterUserChat(Long id);

    void checkThatUserChatExists(Long id);
}
