package edu.java.configuration.dbAccess.jdbc;

import edu.java.repository.LinkRepository;
import edu.java.repository.UserRepository;
import edu.java.repository.jdbc.JdbcLinkRepository;
import edu.java.repository.jdbc.JdbcUserRepository;
import edu.java.service.LinkService;
import edu.java.service.UserService;
import edu.java.service.jdbc.JdbcLinkService;
import edu.java.service.jdbc.JdbcUserService;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;

@Configuration
@ConditionalOnProperty(prefix = "app", name = "database-access-type", havingValue = "jdbc")
public class JdbcAccessConfiguration {

    private final JdbcTemplate jdbcTemplate;

    public JdbcAccessConfiguration(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Bean
    public UserRepository jdbcUserRepository() {
        return new JdbcUserRepository(jdbcTemplate);
    }

    @Bean
    public LinkRepository jdbcLinkRepository() {
        return new JdbcLinkRepository(jdbcTemplate);
    }

    @Bean
    public UserService jdbcUserService(UserRepository userRepository, LinkRepository linkRepository) {
        return new JdbcUserService(
            (JdbcUserRepository) userRepository,
            (JdbcLinkRepository) linkRepository
        );
    }

    @Bean
    public LinkService jdbcLinkService(LinkRepository linkRepository, UserService userService) {
        return new JdbcLinkService(
            (JdbcLinkRepository) linkRepository,
            (JdbcUserService) userService
        );
    }
}
