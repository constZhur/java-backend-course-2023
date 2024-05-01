package edu.java.configuration.rateLimiter;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.Refill;
import java.time.Duration;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BucketConfiguration {
    @Value("${api.rate-limiter.requests}")
    private Integer requests;

    @Value("${api.rate-limiter.interval}")
    private Integer interval;

    @Bean
    public Bucket createBucket() {
        Bandwidth limit = Bandwidth.classic(requests,
            Refill.intervally(requests, Duration.ofMinutes(interval)));
        return Bucket.builder().addLimit(limit).build();
    }
}
