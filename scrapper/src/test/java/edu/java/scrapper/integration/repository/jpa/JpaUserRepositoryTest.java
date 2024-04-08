package edu.java.scrapper.integration.repository.jpa;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.HashSet;
import java.util.List;
import edu.java.model.Link;
import edu.java.model.User;
import edu.java.repository.jpa.JpaLinkRepository;
import edu.java.repository.jpa.JpaUserRepository;
import edu.java.scrapper.integration.IntegrationTest;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class JpaUserRepositoryTest extends IntegrationTest {

    @Autowired
    private JpaUserRepository userRepository;

    @Autowired
    private JpaLinkRepository linkRepository;

    static User user1, user2, user3;
    static Link link1, link2;

    @BeforeAll
    public static void SetUp() {
        link1 = new Link(3,
            "https://github.com/constZhur/java-backend-course-2023",
            OffsetDateTime.of(2024, 3, 20, 21, 0, 0, 0, ZoneOffset.UTC));
        link2 = new Link(1,
            "https://github.com/lwbeamer/clound-project",
            OffsetDateTime.of(2024, 3, 20, 21, 0, 0, 0, ZoneOffset.UTC));

        user1 = new User(1L, new HashSet<>());
        user2 = new User(2L, new HashSet<>());
        user3 = new User(3L, new HashSet<>());;
    }

    @BeforeEach
    void beforeEach(){
        userRepository.deleteById(user1.getId());
        userRepository.deleteById(user2.getId());
        userRepository.deleteById(user3.getId());

        linkRepository.deleteById(link1.getId());
        linkRepository.deleteById(link2.getId());
    }

    @Test
    public void testAddLinkForUser() {
        userRepository.save(user1);
        linkRepository.save(link1);


        userRepository.addLinkForUser(user1.getId(), link1.getId());


        List<Long> chatIds = userRepository.getAllUserChatIdsByLinkId(link1.getId());
        assertThat(chatIds).contains(user1.getId());
    }

    @Test
    public void testGetAllUserChatIdsByLinkId() {
        userRepository.save(user2);
        userRepository.save(user3);

        linkRepository.save(link2);

        userRepository.addLinkForUser(user2.getId(), link2.getId());
        userRepository.addLinkForUser(user3.getId(), link2.getId());

        List<Long> chatIds = userRepository.getAllUserChatIdsByLinkId(link2.getId());

        assertThat(chatIds).isNotNull();
        assertThat(chatIds.isEmpty()).isFalse();
        assertThat(chatIds.size()).isEqualTo(2);
        assertThat(chatIds).contains(user2.getId());
        assertThat(chatIds).contains(user3.getId());
    }
}
