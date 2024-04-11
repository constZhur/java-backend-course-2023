package edu.java.configuration;

import edu.java.clients.impl.BotClient;
import edu.java.clients.impl.GithubClient;
import edu.java.clients.impl.StackoverflowClient;
import edu.java.clients.retry.RetryConfigProxy;
import edu.java.clients.retry.RetryPolicy;
import io.github.resilience4j.retry.Retry;
import java.util.List;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;

@Configuration
public class ClientConfiguration {

    @Value("${api.github.url}")
    private String githubApiUrl;

    @Value("${api.stackoverflow.url}")
    private String stackoverflowApiUrl;

    @Value("${api.bot.url}")
    private String botApiUrl;

    @Value("${api.github.retry-policy}")
    private RetryPolicy githubRetryPolicy;

    @Value("${api.stackoverflow.retry-policy}")
    private RetryPolicy stackoverflowRetryPolicy;

    @Value("${api.bot.retry-policy}")
    private RetryPolicy botRetryPolicy;

    @Value("${api.github.max-retries}")
    private Integer githubMaxRetries;

    @Value("${api.stackoverflow.max-retries}")
    private Integer stackoverflowMaxRetries;

    @Value("${api.bot.max-retries}")
    private Integer botMaxRetries;

    @Value("${api.github.retry-delay}")
    private Long githubRetryDelay;

    @Value("${api.stackoverflow.retry-delay}")
    private Long stackoverflowRetryDelay;

    @Value("${api.bot.retry-delay}")
    private Long botRetryDelay;

    @Value("${api.github.increment}")
    private Integer githubIncrement;

    @Value("${api.stackoverflow.increment}")
    private Integer stackoverflowIncrement;

    @Value("${api.bot.increment}")
    private Integer botIncrement;

    @Value("${api.github.http-codes}")
    private List<HttpStatus> githubHttpCodes;

    @Value("${api.stackoverflow.http-codes}")
    private List<HttpStatus> stackoverflowHttpCodes;

    @Value("${api.bot.http-codes}")
    private List<HttpStatus> botHttpCodes;

    @Bean
    public GithubClient githubClient() {
        Retry githubRetry = createRetry(githubRetryPolicy, githubMaxRetries,
            githubRetryDelay, githubIncrement, githubHttpCodes);
        return new GithubClient(githubRetry, githubApiUrl);
    }

    @Bean
    public StackoverflowClient stackoverflowClient() {
        Retry stackoverflowRetry = createRetry(stackoverflowRetryPolicy,
            stackoverflowMaxRetries, stackoverflowRetryDelay, stackoverflowIncrement, stackoverflowHttpCodes);
        return new StackoverflowClient(stackoverflowRetry, stackoverflowApiUrl);
    }

    @Bean
    @ConditionalOnProperty(prefix = "app", name = "use-queue", havingValue = "false")
    public BotClient botClient() {
        Retry botRetry = createRetry(botRetryPolicy, botMaxRetries,
            botRetryDelay, botIncrement, botHttpCodes);
        return new BotClient(botRetry, botApiUrl);
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

