package edu.java.service.jpa;

import edu.java.exception.ChatNotFoundException;
import edu.java.exception.RegisteredUserExistsException;
import edu.java.model.Link;
import edu.java.model.User;
import edu.java.repository.jpa.JpaLinkRepository;
import edu.java.repository.jpa.JpaUserRepository;
import edu.java.service.UserService;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class JpaUserService implements UserService {
    private final JpaUserRepository userRepository;
    private final JpaLinkRepository linkRepository;

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
    public boolean checkThatUserChatExists(Long id) {
        if (!userRepository.existsById(id)) {
            throw new ChatNotFoundException();
        }
        return true;
    }

    @Override
    public List<Long> getAllUserChatIdsByLinkId(Integer linkId) {
        return userRepository.getAllUserChatIdsByLinkId(linkId);
    }

    @Override
    public void addLinkForUser(Long userId, Link link) {
        if (!checkThatUserChatExists(userId)) {
            throw new ChatNotFoundException();
        }

        Optional<User> foundUser = userRepository.findById(userId);
        if (foundUser.isPresent()) {
            Optional<Link> foundLink = linkRepository.findByUrl(link.getUrl());
            Integer linkId;
            if (foundLink.isEmpty()) {
                linkRepository.save(link);
                linkId = link.getId();
            } else {
                linkId = foundLink.get().getId();
            }

            userRepository.addLinkForUser(userId, linkId);
        } else {
            throw new ChatNotFoundException();
        }
    }
}
