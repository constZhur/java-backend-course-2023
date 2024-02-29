package edu.java.bot.repositories;

import edu.java.bot.models.User;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import java.util.HashSet;
import java.util.List;

public class UserRepositoryImplTest {
    private UserRepositoryImpl userRepository;
    private User firstUser, secondUser;

    @BeforeEach
    public void setUp(){
        userRepository = new UserRepositoryImpl();
        firstUser = new User(1L, new HashSet<>(
            List.of("https://google.com", "https://github.com",
                "https://openai.com", "https://stackoverflow.com"))
        );
        secondUser = new User(2L, new HashSet<>(
            List.of("https://edu.tinkoff.ru", "https://vk.com",
                "https://www.youtube.com/"))
        );
    }

    @Test
    public void testSaveUser() {
        userRepository.save(firstUser);
        User retrievedUser = userRepository.getById(firstUser.getId());

        Assertions.assertEquals(firstUser, retrievedUser);
    }

    @Test
    public void testGetByIdExistingUser() {
        userRepository.save(firstUser);
        User retrievedUser = userRepository.getById(firstUser.getId());

        Assertions.assertEquals(firstUser, retrievedUser);
    }

    @Test
    public void testGetByIdNonExistingUser() {
        User retrievedUser = userRepository.getById(3L);

        Assertions.assertNull(retrievedUser);
    }

    @Test
    public void testGetAllUsers() {
        userRepository.save(firstUser);
        userRepository.save(secondUser);
        List<User> users = userRepository.getAll();

        Assertions.assertEquals(2, users.size());
        Assertions.assertTrue(users.contains(firstUser));
        Assertions.assertTrue(users.contains(secondUser));
    }

    @Test
    public void testDeleteByIdExistingUser() {
        userRepository.save(firstUser);
        userRepository.deleteById(firstUser.getId());

        User retrievedUser = userRepository.getById(firstUser.getId());

        Assertions.assertNull(retrievedUser);
    }

    @Test
    public void testDeleteByIdNonExistingUser() {
        userRepository.save(firstUser);
        userRepository.deleteById(firstUser.getId());

        User retrievedUser = userRepository.getById(firstUser.getId());

        Assertions.assertNull(retrievedUser);
    }
}
