package edu.java.configuration.dbAccess.jooq;

import edu.java.repository.LinkRepository;
import edu.java.repository.UserRepository;
import edu.java.repository.jooq.JooqLinkRepository;
import edu.java.repository.jooq.JooqUserRepository;
import edu.java.service.LinkService;
import edu.java.service.UserService;
import edu.java.service.jooq.JooqLinkService;
import edu.java.service.jooq.JooqUserService;
import org.jooq.DSLContext;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnProperty(prefix = "app", name = "database-access-type", havingValue = "jooq")
public class JooqAccessConfiguration {
    private final DSLContext dslContext;

    public JooqAccessConfiguration(DSLContext dslContext) {
        this.dslContext = dslContext;
    }

    @Bean
    public UserRepository jooqUserRepository() {
        return new JooqUserRepository(dslContext);
    }

    @Bean
    public LinkRepository jooqLinkRepository() {
        return new JooqLinkRepository(dslContext);
    }

    @Bean
    public UserService jooqUserService(UserRepository userRepository, LinkRepository linkRepository) {
        return new JooqUserService(
            (JooqUserRepository) userRepository,
            (JooqLinkRepository) linkRepository
        );
    }

    @Bean
    public LinkService jooqLinkService(LinkRepository linkRepository, UserService userService) {
        return new JooqLinkService(
            (JooqLinkRepository) linkRepository,
            (JooqUserService) userService
        );
    }
}
