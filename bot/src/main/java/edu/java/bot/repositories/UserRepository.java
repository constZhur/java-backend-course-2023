package edu.java.bot.repositories;

import edu.java.bot.models.User;
import java.util.List;

public interface UserRepository {
    User save(User user);

    User getById(Long id);

    List<User> getAll();

    void deleteById(Long id);
}
