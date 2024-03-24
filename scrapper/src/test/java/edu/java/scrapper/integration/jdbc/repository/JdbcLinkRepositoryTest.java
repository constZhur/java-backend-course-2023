package edu.java.scrapper.integration.jdbc.repository;

import edu.java.model.Link;
import edu.java.model.User;
import edu.java.repository.jdbc.JdbcLinkRepository;
import edu.java.repository.jdbc.JdbcUserRepository;
import edu.java.scrapper.integration.IntegrationTest;
import org.junit.Test;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import java.time.OffsetDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

@SpringBootTest
public class JdbcLinkRepositoryTest extends IntegrationTest {
    @Autowired
    private JdbcLinkRepository linkRepository;
    @Autowired
    private JdbcUserRepository userRepository;

    private static User user1 = new User(1L, "constZhur", new HashSet<>());
    private static User user2 = new User(1L, "lwbeamer", new HashSet<>());
    private static User user3 = new User(1L, "sanyarnd", new HashSet<>());

    private static Link link1 = new Link("https://github.com/lwbeamer/clound-project");
    private static Link link2 = new Link("https://stackoverflow.com/questions/46125417/how-to-mock-a-service-using-wiremock");
    private static Link link3 = new Link("https://github.com/constZhur/java-backend-course-2023");

    @BeforeEach
    public void setUp() {
        userRepository.add(user1);
        userRepository.add(user2);
        userRepository.add(user3);
    }

    @Test
    public void testAdd() {
        linkRepository.add(link1);
        Optional<Link> foundLink = linkRepository.findById(link1.getId());
        assertTrue(foundLink.isPresent());
        assertEquals(link1, foundLink.get());
    }

    @Test
    public void testFindById() {
        linkRepository.add(link2);
        Optional<Link> foundLink = linkRepository.findById(link2.getId());
        assertTrue(foundLink.isPresent());
        assertEquals(link2, foundLink.get());
    }

    @Test
    public void testFindLinkByUrl() {
        linkRepository.add(link3);
        Optional<Link> foundLink = linkRepository.findLinkByUrl(link3.getUrl());
        assertTrue(foundLink.isPresent());
        assertEquals(link3, foundLink.get());
    }

    @Test
    public void testFindAll() {
        linkRepository.add(link1);
        linkRepository.add(link2);
        linkRepository.add(link3);
        List<Link> allLinks = linkRepository.findAll();
        assertEquals(3, allLinks.size());
        assertTrue(allLinks.contains(link1));
        assertTrue(allLinks.contains(link2));
        assertTrue(allLinks.contains(link3));
    }

//    @Test
//    public void testFindAllUserLinks() {
//        linkRepository.add(link1);
//        linkRepository.add(link2);
//        linkRepository.add(link3);
//
//        List<Link> user1Links = linkRepository.findAllUserLinks(user1.getId());
//        assertEquals(0, user1Links.size());
//
//        linkRepository.addUserLink(user1.getId(), link1);
//        linkRepository.addUserLink(user1.getId(), link2);
//
//        user1Links = linkRepository.findAllUserLinks(user1.getId());
//        assertEquals(2, user1Links.size());
//        assertTrue(user1Links.contains(link1));
//        assertTrue(user1Links.contains(link2));
//    }

    @Test
    public void testFindOutdatedLinks() {
        linkRepository.add(link1);
        linkRepository.add(link2);
        linkRepository.add(link3);

        linkRepository.updateCheckedTime(link1, OffsetDateTime.now().minusDays(2));
        linkRepository.updateCheckedTime(link2, OffsetDateTime.now().minusHours(1));
        linkRepository.updateCheckedTime(link3, OffsetDateTime.now().minusMinutes(30));

        List<Link> outdatedLinks = linkRepository.findOutdatedLinks(2L, 3600L);
        assertEquals(2, outdatedLinks.size());
        assertTrue(outdatedLinks.contains(link1));
        assertTrue(outdatedLinks.contains(link3));
    }

    @Test
    public void testRemove() {
        linkRepository.add(link1);
        linkRepository.add(link2);

        linkRepository.remove(link1.getId());

        Optional<Link> removedLink = linkRepository.findById(link1.getId());
        assertTrue(removedLink.isEmpty());

        List<Link> allLinks = linkRepository.findAll();
        assertEquals(1, allLinks.size());
        assertEquals(link2, allLinks.get(0));
    }

//    @Test
//    public void testRemoveUserLink() {
//        linkRepository.add(link1);
//        linkRepository.add(link2);
//        linkRepository.add(link3);
//
//        linkRepository.addUserLink(user1.getId(), link1);
//        linkRepository.addUserLink(user1.getId(), link2);
//
//        linkRepository.removeUserLink(user1.getId(), link1);
//
//        List<Link> user1Links = linkRepository.findAllUserLinks(user1.getId());
//        assertEquals(1, user1Links.size());
//        assertFalse(user1Links.contains(link1));
//        assertTrue(user1Links.contains(link2));
//        assertTrue(user1Links.contains(link3));
//    }

    @Test
    public void testUpdateCheckedTime() {
        linkRepository.add(link1);
        OffsetDateTime newUpdateTime = OffsetDateTime.now().minusDays(1);
        linkRepository.updateCheckedTime(link1, newUpdateTime);

        Optional<Link> updatedLink = linkRepository.findById(link1.getId());
        assertTrue(updatedLink.isPresent());
        assertEquals(newUpdateTime, updatedLink.get().getCheckedAt());
    }


}
