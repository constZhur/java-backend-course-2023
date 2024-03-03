package edu.java.service;

import edu.java.exception.AddedLinkExistsException;
import edu.java.exception.ChatNotFoundException;
import edu.java.exception.LinkNotFoundException;
import edu.java.exception.RegisteredUserExistsException;
import edu.java.model.Link;
import edu.java.model.User;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

@Service
public class UserService {
    private final Map<Long, User> users = new HashMap<>();

    public void addUser(Long id) {
        if (users.containsKey(id)) {
            throw new RegisteredUserExistsException();
        }

        User newUser = new User(id, new HashSet<>());
        users.put(id, newUser);
    }

    public void deleteUser(Long id) {
        if (!users.containsKey(id)) {
            throw new ChatNotFoundException();
        }

        users.remove(id);
    }

    public User getUser(Long id) {
        User user = users.get(id);
        if (user == null) {
            throw new ChatNotFoundException();
        }

        return user;
    }

    public Link addLink(Long id, String link) {
        User user = getUser(id);

        if (user.getLinks().stream().anyMatch(existingLink -> existingLink.getLink().equals(link))) {
            throw new AddedLinkExistsException();
        }

        Link newLink = new Link(link);
        user.getLinks().add(newLink);
        return newLink;
    }

    public void deleteLink(Long id, String link) {
        User user = getUser(id);

        if (!user.getLinks().removeIf(existingLink -> existingLink.getLink().equals(link))) {
            throw new LinkNotFoundException();
        }
    }

    public List<Link> getLinks(Long chatId) {
        User user = getUser(chatId);
        return new ArrayList<>(user.getLinks());
    }
}
