package edu.java.configuration;

import edu.java.repository.jdbc.JdbcLinkRepository;
import edu.java.repository.jdbc.JdbcUserRepository;
import edu.java.service.jdbc.JdbcLinkService;
import edu.java.service.jdbc.JdbcUserService;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnProperty(prefix = "app", name = "database-access-type", havingValue = "jdbc")
public class JdbcAccessConfiguration {

    @Bean
    public JdbcUserService jdbcUserService(JdbcUserRepository userRepository) {
        return new JdbcUserService(userRepository);
    }

    @Bean
    public JdbcLinkService jdbcLinkService(JdbcLinkRepository linkRepository, JdbcUserService userService) {
        return new JdbcLinkService(linkRepository, userService);
    }
}
