package edu.java.bot.configuration;

import edu.java.bot.client.retry.RetryConfigProxy;
import edu.java.bot.client.retry.RetryPolicy;
import edu.java.bot.client.scrapper.ScrapperClient;
import io.github.resilience4j.retry.Retry;
import java.util.List;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;

@Configuration
public class ClientConfiguration {

    @Value("${api.scrapper.url}")
    private String scrapperApiUrl;

    @Value("${api.scrapper.retry-policy}")
    private RetryPolicy retryPolicy;
    @Value("${api.scrapper.max-retries}")
    private Integer maxRetries;
    @Value("${api.scrapper.retry-delay}")
    private Long retryDelay;
    @Value("${api.scrapper.increment}")
    private Integer increment;
    @Value("${api.scrapper.http-codes}")
    private List<HttpStatus> httpCodes;

    @Bean
    public ScrapperClient scrapperClient() {
        Retry retry = createRetry(retryPolicy, maxRetries, retryDelay, increment, httpCodes);
        return new ScrapperClient(retry, scrapperApiUrl);
    }

    private Retry createRetry(RetryPolicy retryPolicy, Integer maxRetries, Long retryDelay,
        Integer increment, List<HttpStatus> httpCodes) {
        RetryConfigProxy proxy = RetryConfigProxy.builder()
            .policy(retryPolicy)
            .maxRetries(maxRetries)
            .retryDelay(retryDelay)
            .increment(increment)
            .httpStatuses(httpCodes)
            .build();
        return RetryConfiguration.start(proxy);
    }
}
