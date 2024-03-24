package edu.java.service.jpa;

import edu.java.exception.ChatNotFoundException;
import edu.java.exception.RegisteredUserExistsException;
import edu.java.model.User;
import edu.java.repository.jpa.JpaUserRepository;
import edu.java.service.UserService;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class JpaUserService implements UserService {
    private final JpaUserRepository userRepository;

    @Override
    public void registerUserChat(User user) {
        if (userRepository.existsById(user.getId())) {
            throw new RegisteredUserExistsException();
        }
        userRepository.save(user);
    }

    @Override
    public void unregisterUserChat(Long id) {
        checkThatUserChatExists(id);
        userRepository.deleteById(id);
    }

    @Override
    public void checkThatUserChatExists(Long id) {
        if (!userRepository.existsById(id)){
            throw new ChatNotFoundException();
        }
    }
}
