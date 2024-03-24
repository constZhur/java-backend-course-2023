package edu.java.bot.configuration;

import edu.java.bot.client.ScrapperClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ClientConfiguration {

    @Value("${api.scrapper.url}")
    private String scrapperApiUrl;

    @Bean
    public ScrapperClient githubClient() {
        return new ScrapperClient(scrapperApiUrl);
    }
}
