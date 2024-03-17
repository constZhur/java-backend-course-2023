package edu.java.repository.jdbc;

import edu.java.model.User;
import edu.java.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class JdbcUserRepository implements UserRepository {
    private final JdbcTemplate jdbcTemplate;

    @Override
    public void add(User userChat) {
        jdbcTemplate.update("INSERT INTO user_chat (name) VALUES ?",
            userChat.getName());
    }

    @Override
    public Optional<User> findById(Long id) {
        return jdbcTemplate
            .query("SELECT * FROM user_chat WHERE id = ?", new BeanPropertyRowMapper<>(User.class), id)
            .stream().findAny();
    }

    @Override
    @Transactional
    public void remove(Long id) {
        jdbcTemplate.update("DELETE FROM user_chat WHERE id = ?", id);
        jdbcTemplate.update("DELETE FROM link_chat_relations WHERE chat_id = ?", id);
    }

    @Override
    public List<User> findAll() {
        return jdbcTemplate.query("SELECT * FROM user_chat", new BeanPropertyRowMapper<>(User.class));
    }
}
