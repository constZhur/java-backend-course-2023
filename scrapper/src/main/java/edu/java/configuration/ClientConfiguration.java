package edu.java.configuration;

import edu.java.clients.impl.BotClient;
import edu.java.clients.impl.GithubClient;
import edu.java.clients.impl.StackoverflowClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ClientConfiguration {

    @Value("${api.github.url}")
    private String githubApiUrl;

    @Value("${api.stackoverflow.url}")
    private String stackoverflowApiUrl;

    @Value("${api.bot.url}")
    private String botApiUrl;

    @Bean
    public GithubClient githubClient() {
        return new GithubClient(githubApiUrl);
    }

    @Bean
    public StackoverflowClient stackoverflowClient() {
        return new StackoverflowClient(stackoverflowApiUrl);
    }

    @Bean
    public BotClient botClient() {
        return new BotClient(botApiUrl);
    }
}
