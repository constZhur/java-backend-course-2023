package edu.java.configuration.rateLimit;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RateLimitConfiguration {
    @Value("${api.rate-limiter.requests}")
    private Integer requests;

    @Value("${api.rate-limiter.interval}")
    private Integer interval;

    @Bean
    public RateLimitInterceptor interceptor() {
        return new RateLimitInterceptor(requests, interval);
    }
}
