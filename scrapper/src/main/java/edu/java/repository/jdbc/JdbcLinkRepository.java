package edu.java.repository.jdbc;

import edu.java.model.Link;
import edu.java.repository.LinkRepository;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
public class JdbcLinkRepository implements LinkRepository {
    private final JdbcTemplate jdbcTemplate;

    @Override
    public void add(Link link) {
        jdbcTemplate.update("INSERT INTO link (id, url) VALUES (?, ?)", link.getId(), link.getUrl());
    }

    @Override
    public Optional<Link> findById(Integer id) {
        List<Link> links = jdbcTemplate.query("SELECT * FROM link WHERE id = ?", this::mapLink, id);
        return links.isEmpty() ? Optional.empty() : Optional.of(links.get(0));
    }

    @Override
    public Optional<Link> findByUrl(String url) {
        List<Link> links = jdbcTemplate.query("SELECT * FROM link WHERE url = ?", this::mapLink, url);
        return links.isEmpty() ? Optional.empty() : Optional.of(links.get(0));
    }

    @Override
    public Optional<Link> findUserLinkByUrl(Long id, String url) {
        List<Link> links = jdbcTemplate.query("SELECT l.* FROM link l JOIN link_chat_relations cl ON cl.chat_id = ? "
            + "AND cl.link_id = l.id WHERE l.url = ?", this::mapLink, url);
        return links.isEmpty() ? Optional.empty() : Optional.of(links.get(0));
    }

    @Override
    public List<Link> findAll() {
        return jdbcTemplate.query("SELECT * FROM link", this::mapLink);
    }

    @Override
    public List<Link> findAllUserLinks(Long userId) {
        return jdbcTemplate.query(
            "SELECT l.* FROM link l \n"
                + "JOIN link_chat_relations r ON l.id = r.link_id \n"
                + "WHERE r.chat_id = ?",
            this::mapLink, userId
        );
    }

    @Override
    public List<Link> findOutdatedLinks(Long linksLimit, Long timeInterval) {
        return jdbcTemplate.query("""
                    SELECT * FROM Link WHERE EXTRACT(EPOCH FROM (CURRENT_TIMESTAMP - checked_at)) >= ?
                    LIMIT ?
                    """,
            new BeanPropertyRowMapper<>(Link.class), timeInterval, linksLimit
        );
    }

    @Override
    @Transactional
    public void remove(Integer id) {
        jdbcTemplate.update("DELETE FROM link WHERE id = ?", id);
        jdbcTemplate.update("DELETE FROM link_chat_relations WHERE link_id = ?", id);
    }

    @Override
    public void removeUserLink(Long userId, Link link) {
        jdbcTemplate.update("""
                DELETE FROM link_chat_relations WHERE chat_id = ? AND link_id = ?
                """, userId, link.getId());
    }

    @Override
    public void updateCheckedTime(Link link, OffsetDateTime newUpdateTime) {
        jdbcTemplate.update("""
                UPDATE link SET checked_at = ? WHERE id = ?
                """, newUpdateTime, link.getId());
    }

    private Link mapLink(ResultSet rs, int rowNum) throws SQLException {
        Link link = new Link();
        link.setId(rs.getInt("id"));
        link.setUrl(rs.getString("url"));
        link.setCheckedAt(rs.getTimestamp("checked_at").toInstant().atOffset(ZoneOffset.UTC));
        return link;
    }
}
