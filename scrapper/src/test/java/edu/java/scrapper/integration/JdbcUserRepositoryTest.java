package edu.java.scrapper.integration;

import edu.java.model.User;
import edu.java.repository.jdbc.JdbcUserRepository;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import java.util.List;

public class JdbcUserRepositoryTest extends IntegrationTest {
    private static JdbcUserRepository userRepository;
    private static List<User> users;

    @BeforeAll
    public static void setUp() {
        userRepository = new JdbcUserRepository(jdbcTemplate);
    }

    @Test
    void addUserTest() {

    }

    @Test
    void removeUserTest() {

    }

    @Test
    void findUserByIdTest() {

    }

    @Test
    void FindAllUsersTest() {

    }
}
