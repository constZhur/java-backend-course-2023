package edu.java.bot.repositories;

import edu.java.bot.models.User;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Repository;

@Repository
public class UserRepositoryImpl implements UserRepository {
    private final Map<Long, User> users = new HashMap<>();

    @Override
    public User save(User user) {
        users.put(user.getId(), user);
        return user;
    }

    @Override
    public User getById(Long id) {
        return users.get(id);
    }

    @Override
    public List<User> getAll() {
        return users.values().stream().toList();
    }

    @Override
    public void deleteById(Long id) {
        users.remove(id);
    }
}
