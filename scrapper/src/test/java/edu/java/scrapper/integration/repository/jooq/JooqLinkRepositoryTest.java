package edu.java.scrapper.integration.repository.jooq;

import edu.java.model.Link;
import edu.java.model.User;
import edu.java.repository.jooq.JooqLinkRepository;
import edu.java.repository.jooq.JooqUserRepository;
import edu.java.scrapper.integration.IntegrationTest;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.jooq.JooqTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.transaction.annotation.Transactional;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import static org.assertj.core.api.Assertions.assertThat;

@Transactional
@JooqTest
@DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
public class JooqLinkRepositoryTest extends IntegrationTest {

    private static JooqLinkRepository linkRepository;
    private static JooqUserRepository userRepository;

    private static User user1 = new User(1L, new HashSet<>());
    private static User user2 = new User(2L, new HashSet<>());

    private static Link link1 = new Link(1,
        "https://github.com/lwbeamer/clound-project",
        OffsetDateTime.of(2024, 3, 20, 21, 0, 0, 0, ZoneOffset.UTC));
    private static Link link2 = new Link(2,
        "https://stackoverflow.com/questions/46125417/how-to-mock-a-service-using-wiremock",
        OffsetDateTime.of(2024, 3, 20, 21, 0, 0, 0, ZoneOffset.UTC));
    private static Link link3 = new Link(3,
        "https://github.com/constZhur/java-backend-course-2023",
        OffsetDateTime.of(2024, 3, 20, 21, 0, 0, 0, ZoneOffset.UTC));

    @BeforeAll
    static void setUp() {
        linkRepository = new JooqLinkRepository(dslContext);
        userRepository = new JooqUserRepository(dslContext);
    }


    @BeforeEach
    void beforeEach() {
        userRepository.remove(user1.getId());
        userRepository.remove(user2.getId());

        linkRepository.remove(link1.getId());
        linkRepository.remove(link2.getId());
        linkRepository.remove(link3.getId());
    }


    @Test
    public void testAdd() {
        linkRepository.remove(link1.getId());
        linkRepository.add(link1);

        List<Link> links = linkRepository.findAll();

        assertThat(links.size()).isEqualTo(1);
        assertThat(links.stream().map(Link::getUrl).toList())
            .containsExactlyInAnyOrderElementsOf(List.of(link1.getUrl()));
    }

    @Test
    public void testAddLinkForUser() {
        linkRepository.remove(link1.getId());
        userRepository.remove(user1.getId());

        linkRepository.add(link1);
        userRepository.add(user1);


        userRepository.addLinkForUser(user1.getId(), link1.getId());
        List<Link> user1Links = linkRepository.findAllUserLinks(user1.getId());

        assertThat(user1Links).hasSize(1);
        assertThat(user1Links.get(0).getId()).isEqualTo(link1.getId());
    }

    @Test
    public void testFindById() {
        linkRepository.remove(link2.getId());

        linkRepository.add(link2);

        Optional<Link> foundLink = linkRepository.findById(link2.getId());

        assertThat(foundLink).isPresent();
        assertThat(foundLink.get().getId()).isEqualTo(link2.getId());
    }

    @Test
    public void testFindLinkByUrl() {
        linkRepository.remove(link1.getId());

        linkRepository.add(link1);

        Optional<Link> foundLink = linkRepository.findByUrl(link1.getUrl());

        assertThat(foundLink).isPresent();
        assertThat(foundLink.get().getUrl()).isEqualTo(link1.getUrl());
    }

    @Test
    public void testFindAll() {
        linkRepository.remove(link1.getId());
        linkRepository.remove(link2.getId());
        linkRepository.remove(link3.getId());


        linkRepository.add(link1);
        linkRepository.add(link2);
        linkRepository.add(link3);

        List<Link> links = linkRepository.findAll();

        assertThat(links).hasSize(3);
        assertThat(links.stream().map(Link::getUrl).toList())
            .containsExactlyInAnyOrderElementsOf(List.of(link1.getUrl(), link2.getUrl(), link3.getUrl()));
    }

    @Test
    public void testFindAllUserLinks() {
        linkRepository.add(link1);
        linkRepository.add(link2);
        linkRepository.add(link3);
        userRepository.add(user1);
        userRepository.add(user2);
        userRepository.addLinkForUser(user1.getId(), link1.getId());
        userRepository.addLinkForUser(user1.getId(), link2.getId());
        userRepository.addLinkForUser(user2.getId(), link2.getId());
        userRepository.addLinkForUser(user2.getId(), link3.getId());

        List<Link> user1Links = linkRepository.findAllUserLinks(user1.getId());
        List<Link> user2Links = linkRepository.findAllUserLinks(user2.getId());

        assertThat(user1Links).hasSize(2);
        assertThat(user2Links).hasSize(2);
        assertThat(user1Links.stream().map(Link::getUrl).toList())
            .containsExactlyInAnyOrderElementsOf(List.of(link1.getUrl(), link2.getUrl()));
        assertThat(user2Links.stream().map(Link::getUrl).toList())
            .containsExactlyInAnyOrderElementsOf(List.of(link2.getUrl(), link3.getUrl()));;
    }

    @Test
    public void testFindOutdatedLinks() {
        linkRepository.add(link1);
        linkRepository.add(link2);
        linkRepository.add(link3);

        Long linksLimit = 10L;
        Long timeIntervalSeconds = 24 * 60 * 60L;

        List<Link> outdatedLinks = linkRepository.findOutdatedLinks(linksLimit, timeIntervalSeconds);

        assertThat(outdatedLinks).isEmpty();
    }

    @Test
    public void testUpdateCheckedTime() {
        linkRepository.add(link1);

        OffsetDateTime newUpdateTime = OffsetDateTime.of(2024, 3, 21, 12, 0, 0, 0, ZoneOffset.UTC);
        linkRepository.updateCheckedTime(link1, newUpdateTime);

        Optional<Link> updatedLink = linkRepository.findById(link1.getId());
        OffsetDateTime expectedTime = newUpdateTime.withOffsetSameInstant(updatedLink.get().getCheckedAt().getOffset());

        assertThat(updatedLink).isPresent();
        assertThat(updatedLink.get().getCheckedAt()).isEqualTo(expectedTime);
    }

    @Test
    public void testRemove() {
        linkRepository.add(link1);

        linkRepository.remove(link1.getId());
        Optional<Link> deletedLink = linkRepository.findById(link1.getId());

        assertThat(deletedLink).isEmpty();
    }

    @Test
    public void testRemoveUserLink() {
        linkRepository.add(link1);
        user1.setLinks(Set.of(link1));
        userRepository.add(user1);

        linkRepository.removeUserLink(user1.getId(), link1);
        List<Link> user1Links = linkRepository.findAllUserLinks(user1.getId());

        assertThat(user1Links).doesNotContain(link1);
    }
}
