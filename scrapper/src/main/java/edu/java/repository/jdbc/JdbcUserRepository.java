package edu.java.repository.jdbc;

import edu.java.model.User;
import edu.java.repository.UserRepository;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
public class JdbcUserRepository implements UserRepository {
    private final JdbcTemplate jdbcTemplate;

    @Override
    public void add(User userChat) {
        jdbcTemplate.update("INSERT INTO user_chat (id) VALUES (?)", userChat.getId());
    }

    @Override
    public void addLinkForUser(Long userId, Integer linkId) {
        jdbcTemplate.update("INSERT INTO link_chat_relations (chat_id, link_id) VALUES (?, ?)", userId, linkId);
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

    @Override
    public List<Long> getAllUserChatIdsByLinkId(Integer linkId) {
        return jdbcTemplate.queryForList(
            "SELECT chat_id FROM link_chat_relations WHERE link_id = ?", Long.class, linkId
        );
    }
}
