package edu.java.scrapper.integration.repository.jooq;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import edu.java.model.User;
import edu.java.repository.jooq.JooqUserRepository;
import edu.java.scrapper.integration.IntegrationTest;
import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.conf.RenderNameCase;
import org.jooq.conf.RenderQuotedNames;
import org.jooq.impl.DSL;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.jooq.JooqTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.transaction.annotation.Transactional;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import static org.assertj.core.api.Assertions.assertThat;

@Transactional
@JooqTest
@DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
public class JooqUserRepositoryTest extends IntegrationTest {

    private static JooqUserRepository userRepository;

    private static User user1 = new User(1L, "constZhur", new HashSet<>());
    private static User user2 = new User(2L, "lwbeamer", new HashSet<>());

    @BeforeAll
    static void setUp() {
        userRepository = new JooqUserRepository(dslContext);
    }

    @BeforeEach
    void beforeEach() {
        userRepository.remove(user1.getId());
        userRepository.remove(user2.getId());
    }

    @Test
    void addUserTest() {
        userRepository.add(user1);
        Optional<User> foundUser = userRepository.findById(user1.getId());

        assertThat(foundUser).isPresent();
        assertThat(foundUser.get().getName()).isEqualTo(user1.getName());
    }

    @Test
    void removeUserTest() {
        userRepository.add(user1);
        userRepository.remove(user1.getId());
        Optional<User> foundUser = userRepository.findById(user1.getId());

        assertThat(foundUser).isEmpty();
    }

    @Test
    void findUserByIdTest() {
        userRepository.add(user1);
        Optional<User> foundUser = userRepository.findById(user1.getId());

        assertThat(foundUser).isPresent();
        assertThat(foundUser.get().getId()).isEqualTo(user1.getId());
    }

    @Test
    void findAllUsersTest() {;
        userRepository.add(user1);
        userRepository.add(user2);
        List<User> allUsers = userRepository.findAll();

        assertThat(allUsers.stream().map(User::getName).toList())
            .containsExactlyInAnyOrderElementsOf(List.of(user1.getName(), user2.getName()));
    }
}
