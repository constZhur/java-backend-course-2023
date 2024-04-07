package edu.java.service.jdbc;

import edu.java.exception.ChatNotFoundException;
import edu.java.exception.RegisteredUserExistsException;
import edu.java.model.Link;
import edu.java.model.User;
import edu.java.repository.jdbc.JdbcLinkRepository;
import edu.java.repository.jdbc.JdbcUserRepository;
import edu.java.service.UserService;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class JdbcUserService implements UserService {

    private final JdbcUserRepository userRepository;
    private final JdbcLinkRepository linkRepository;


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
    public boolean checkThatUserChatExists(Long id) {
        userRepository.findById(id).orElseThrow(
            ChatNotFoundException::new
        );
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
                linkRepository.add(link);
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
