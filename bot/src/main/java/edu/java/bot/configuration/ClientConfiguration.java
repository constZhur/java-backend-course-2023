package edu.java.bot.configuration;

import edu.java.bot.client.scrapper.ScrapperClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ClientConfiguration {

    @Value("${api.scrapper.url}")
    private String scrapperApiUrl;

    @Bean
    public ScrapperClient scrapperClient() {
        return new ScrapperClient(scrapperApiUrl);
    }
}
