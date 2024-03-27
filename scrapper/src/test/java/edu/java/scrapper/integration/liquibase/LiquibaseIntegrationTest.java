package edu.java.scrapper.integration.liquibase;

import edu.java.scrapper.integration.IntegrationTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import static org.assertj.core.api.Assertions.assertThat;;

@SpringBootTest
@Transactional
public class LiquibaseIntegrationTest extends IntegrationTest {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private final Long chatId1 = 1L;
    private final Long chatId2 = 2L;
    private final Long chatId3 = 3L;

    private final String username1 = "constZhur";
    private final String username2 = "lwbeamer";
    private final String username3 = "sanyarnd";

    private final String link1 = "https://github.com/lwbeamer/clound-project";
    private final String link2 = "https://stackoverflow.com/questions/46125417/how-to-mock-a-service-using-wiremock";
    private final String link3 = "https://github.com/constZhur/java-backend-course-2023";

    @Test
    void testScrapperDBConnectionProperties() {
        assertThat(POSTGRES.isRunning()).isTrue();
        assertThat(POSTGRES.getUsername()).isEqualTo("postgres");
        assertThat(POSTGRES.getPassword()).isEqualTo("postgres");
        assertThat(POSTGRES.getDatabaseName()).isEqualTo("scrapper");
    }

    @Test
    void testScrapperDBUserChatTable() {
        insertUserChat(chatId1, username1);
        insertUserChat(chatId2, username2);

        List<String> actualUsernames = selectUsernamesFromChatTable();

        assertThat(actualUsernames.size()).isEqualTo(2);
        assertThat(actualUsernames).containsExactlyInAnyOrder(username1, username2);
    }

    @Test
    void testScrapperDBLinkTable() {
        insertLink(chatId1, link1);
        insertLink(chatId2, link2);

        List<String> actualLink = selectUrlsFromLinkTable();

        assertThat(actualLink.size()).isEqualTo(2);
        assertThat(actualLink).containsExactlyInAnyOrder(link1, link2);
    }

    @Test
    void testScrapperDBLinkChatRelations() {
        insertUserChat(chatId3, username3);
        insertLink(chatId3, link3);
        insertChatLink(chatId3, 3);

        int link3Count = selectChatLinkCountForLinkId(3);
        assertThat(link3Count).isEqualTo(1);
    }

    private void insertUserChat(long chatId, String name) {
        String insertSql = "INSERT INTO user_chat (id, name) VALUES (?, ?)";
        jdbcTemplate.update(insertSql, chatId, name);
    }

    private void insertLink(Long id, String url) {
        String insertSql = "INSERT INTO link (id, url) VALUES (?, ?)";
        jdbcTemplate.update(insertSql, id, url);
    }

    private void insertChatLink(long chatId, long linkId) {
        String insertSql = "INSERT INTO link_chat_relations (chat_id, link_id) VALUES (?, ?)";
        jdbcTemplate.update(insertSql, chatId, linkId);
    }

    private List<String> selectUsernamesFromChatTable() {
        String selectSql = "SELECT name FROM user_chat";
        return jdbcTemplate.query(selectSql, (rs, rowNum) -> rs.getString("name"));
    }

    private List<String> selectUrlsFromLinkTable() {
        String selectSql = "SELECT url FROM link";
        return jdbcTemplate.query(selectSql, (rs, rowNum) -> rs.getString("url"));
    }

    private int selectChatLinkCountForLinkId(long linkId) {
        String selectSql = "SELECT COUNT(*) FROM link_chat_relations WHERE link_id = ?";
        return jdbcTemplate.queryForObject(selectSql, Integer.class, linkId);
    }
}
