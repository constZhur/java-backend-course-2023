package edu.java.scrapper.integration.repository.jpa;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import edu.java.model.Link;
import edu.java.model.User;
import edu.java.repository.jpa.JpaLinkRepository;
import edu.java.repository.jpa.JpaUserRepository;
import edu.java.scrapper.integration.IntegrationTest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class JpaLinkRepositoryTest extends IntegrationTest {

    @Autowired
    private JpaLinkRepository linkRepository;

    @Autowired
    private JpaUserRepository userRepository;

    static User user;
    static Link link1, link2, link3;

    @BeforeAll
    public static void SetUp() {
        user = new User(1L, "constZhur", new HashSet<>());

        link1 = new Link(1,
            "https://github.com/constZhur/java-backend-course-2023",
            OffsetDateTime.of(2024, 3, 20, 21, 0, 0, 0, ZoneOffset.UTC));
        link2 = new Link(2,
            "https://github.com/lwbeamer/clound-project",
            OffsetDateTime.of(2024, 3, 20, 21, 0, 0, 0, ZoneOffset.UTC));
        link3 = new Link(3,
            "https://stackoverflow.com/questions/46125417/how-to-mock-a-service-using-wiremock",
            OffsetDateTime.of(2024, 3, 20, 21, 0, 0, 0, ZoneOffset.UTC));
    }

    @BeforeEach
    void beforeEach() {
        userRepository.deleteById(user.getId());

        linkRepository.deleteById(link1.getId());
        linkRepository.deleteById(link2.getId());
        linkRepository.deleteById(link3.getId());
    }

    @Test
    public void testFindByUrl() {
        String url = "https://github.com/constZhur/java-backend-course-2023";
        linkRepository.save(link1);

        Optional<Link> foundLink = linkRepository.findByUrl(url);

        assertThat(foundLink).isPresent();
        assertThat(url).isEqualTo(foundLink.get().getUrl());
    }

    @Test
    public void testFindAllOutdatedLinks() {
        List<Link> outdatedLinks = new ArrayList<>(List.of(new Link(
            4, "first_url",
            OffsetDateTime.of(2024, 3, 20, 21, 0, 0, 0, ZoneOffset.UTC)
        ), new Link(
            5, "second_url",
            OffsetDateTime.of(2024, 3, 20, 21, 0, 0, 0, ZoneOffset.UTC)
        )));
        linkRepository.saveAll(outdatedLinks);

        Long timeIntervalSeconds = 1L;
        Long linksLimit = 100L;

        List<Link> retrievedOutdatedLinks = linkRepository.findAllOutdatedLinks(linksLimit, timeIntervalSeconds);
        System.out.println(retrievedOutdatedLinks);

        assertThat(retrievedOutdatedLinks).isNotNull();
        assertThat(retrievedOutdatedLinks.isEmpty()).isFalse();
        assertThat(retrievedOutdatedLinks.size()).isEqualTo(2);
    }

    @Test
    public void testFindAllUserLinks() {
        userRepository.save(user);
        linkRepository.saveAll(List.of(link2, link3));
        userRepository.addLinkForUser(user.getId(), link2.getId());
        userRepository.addLinkForUser(user.getId(), link3.getId());

        List<Link> userLinks = linkRepository.findAllUserLinks(user.getId());

        assertThat(userLinks).isNotNull();
        assertThat(userLinks.size()).isEqualTo(2);
    }

    @Test
    public void testFindUserLinkByUrl() {
        String url = "https://github.com/constZhur/java-backend-course-2023";
        userRepository.save(user);
        linkRepository.save(link1);
        userRepository.addLinkForUser(user.getId(), link1.getId());

        Optional<Link> foundLink = linkRepository.findUserLinkByUrl(user.getId(), url);

        assertThat(foundLink).isNotNull();
        System.out.println(foundLink.get().getUrl());
        System.out.println(url);
        assertThat(foundLink.get().getUrl()).isEqualTo(url);
        assertThat(foundLink.get().getId()).isEqualTo(link1.getId());
    }

    @Test
    public void testRemoveUserLink() {
        userRepository.save(user);
        linkRepository.save(link1);
        userRepository.addLinkForUser(user.getId(), link1.getId());

        linkRepository.removeUserLink(user.getId(), link1.getId());
        List<Link> userLinks = linkRepository.findAllUserLinks(user.getId());

        assertThat(userLinks).isNotNull();
        assertThat(userLinks.isEmpty()).isTrue();
    }

    @Test
    public void testDeleteLinkChatRelationsByLinkId() {
        userRepository.save(user);
        linkRepository.save(link1);

        userRepository.addLinkForUser(user.getId(), link1.getId());


        linkRepository.deleteLinkChatRelationsByLinkId(link1.getId());
        List<Link> userLinks = linkRepository.findAllUserLinks(user.getId());

        assertThat(userLinks).isEmpty();
    }

}

