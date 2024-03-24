package edu.java.service.jdbc;

import edu.java.exception.ChatNotFoundException;
import edu.java.exception.RegisteredUserExistsException;
import edu.java.model.User;
import edu.java.repository.jdbc.JdbcUserRepository;
import edu.java.service.UserService;
import lombok.RequiredArgsConstructor;
import java.util.Optional;

@RequiredArgsConstructor
public class JdbcUserService implements UserService {

    private final JdbcUserRepository userRepository;

    @Override
    public void registerUserChat(User user) {
        Optional<User> foundUserChat = userRepository.findById(user.getId());
        foundUserChat.ifPresentOrElse(
            foundChat -> {
                throw new RegisteredUserExistsException();
            },
            () -> userRepository.add(user)
        );
    }

    @Override
    public void unregisterUserChat(Long id) {
        Optional<User> foundUserChat = userRepository.findById(id);
        foundUserChat.ifPresentOrElse(
            user -> userRepository.remove(id),
            () -> {
                throw new ChatNotFoundException();
            }
        );

    }

    @Override
    public void checkThatUserChatExists(Long id) {
        userRepository.findById(id).orElseThrow(
            () -> new ChatNotFoundException()
        );
    }
}
