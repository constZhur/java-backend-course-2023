package edu.java.scrapper.integration.repository.jdbc;

import edu.java.model.Link;
import edu.java.model.User;
import edu.java.repository.jdbc.JdbcLinkRepository;
import edu.java.repository.jdbc.JdbcUserRepository;
import edu.java.scrapper.integration.IntegrationTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.transaction.annotation.Transactional;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import static org.assertj.core.api.Assertions.*;

@Transactional
@SpringBootTest
@DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
public class JdbcUserRepositoryTest extends IntegrationTest {
    @Autowired
    JdbcUserRepository userRepository;

    private static User user1 = new User(1L, "constZhur", new HashSet<>());
    private static User user2 = new User(2L, "lwbeamer", new HashSet<>());

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
    void findAllUsersTest() {
        userRepository.add(user1);
        userRepository.add(user2);
        List<User> allUsers = userRepository.findAll();

        assertThat(allUsers.stream().map(User::getName).toList())
            .containsExactlyInAnyOrderElementsOf(List.of(user1.getName(), user2.getName()));
    }
}

