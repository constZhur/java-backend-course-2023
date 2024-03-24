package edu.java.scrapper.integration.jdbc.repository;

import edu.java.repository.jdbc.JdbcLinkRepository;
import edu.java.repository.jdbc.JdbcUserRepository;
import edu.java.scrapper.integration.IntegrationTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class JdbcUserRepositoryTest extends IntegrationTest {
    @Autowired
    JdbcLinkRepository linkRepository;
    @Autowired
    JdbcUserRepository userRepository;

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
