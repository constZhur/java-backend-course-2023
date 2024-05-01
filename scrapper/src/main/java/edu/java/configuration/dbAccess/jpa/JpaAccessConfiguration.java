package edu.java.configuration.dbAccess.jpa;

import edu.java.repository.jpa.JpaLinkRepository;
import edu.java.repository.jpa.JpaUserRepository;
import edu.java.service.LinkService;
import edu.java.service.UserService;
import edu.java.service.jpa.JpaLinkService;
import edu.java.service.jpa.JpaUserService;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Configuration
@ConditionalOnProperty(prefix = "app", name = "database-access-type", havingValue = "jpa")
@EnableJpaRepositories(basePackages = "edu.java.repository.jpa")
public class JpaAccessConfiguration {
    private final JpaUserRepository jpaUserRepository;
    private final JpaLinkRepository jpaLinkRepository;

    public JpaAccessConfiguration(JpaUserRepository jpaUserRepository, JpaLinkRepository jpaLinkRepository) {
        this.jpaUserRepository = jpaUserRepository;
        this.jpaLinkRepository = jpaLinkRepository;
    }

    @Bean
    public UserService jpaUserService() {
        return new JpaUserService(jpaUserRepository, jpaLinkRepository);
    }

    @Bean
    public LinkService jpaLinkService() {
        return new JpaLinkService(jpaLinkRepository, (JpaUserService) jpaUserService());
    }
}
