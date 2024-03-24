package edu.java.configuration;

import edu.java.repository.jpa.JpaLinkRepository;
import edu.java.repository.jpa.JpaUserRepository;
import edu.java.service.jpa.JpaLinkService;
import edu.java.service.jpa.JpaUserService;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnProperty(prefix = "app", name = "database-access-type", havingValue = "jpa")
public class JpaAccessConfiguration {

    @Bean
    public JpaUserService jpaUserService(JpaUserRepository userRepository) {
        return new JpaUserService(userRepository);
    }

    @Bean
    public JpaLinkService linkService(JpaLinkRepository linkRepository) {
        return new JpaLinkService(linkRepository);
    }
}
