package edu.java.scrapper.integration.repository.jdbc;

import edu.java.model.User;
import edu.java.repository.jdbc.JdbcUserRepository;
import edu.java.scrapper.integration.IntegrationTest;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import static org.assertj.core.api.Assertions.*;

@Transactional
@SpringBootTest
@DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
@TestPropertySource(properties = {"GITHUB_ACCESS_TOKEN=test_token"})
public class JdbcUserRepositoryTest extends IntegrationTest {
    private static JdbcUserRepository userRepository;

    private static User user1 = new User(1L, new HashSet<>());
    private static User user2 = new User(2L, new HashSet<>());

    @BeforeAll
    static void setUp() {
        userRepository = new JdbcUserRepository(jdbcTemplate);
    }

    @BeforeEach
    void beforeEach(){
        userRepository.remove(user1.getId());
        userRepository.remove(user2.getId());
    }

    @Test
    void addUserTest() {
        userRepository.add(user1);
        Optional<User> foundUser = userRepository.findById(user1.getId());

        assertThat(foundUser).isPresent();
        assertThat(foundUser.get().getId()).isEqualTo(user1.getId());
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
    void findAllUsersTest() {
        userRepository.add(user1);
        userRepository.add(user2);
        List<User> allUsers = userRepository.findAll();

        assertThat(allUsers.stream().map(User::getId).toList())
            .containsExactlyInAnyOrderElementsOf(List.of(user1.getId(), user2.getId()));
    }
}

