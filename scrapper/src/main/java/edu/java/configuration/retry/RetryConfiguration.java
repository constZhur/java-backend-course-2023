package edu.java.configuration.retry;

import edu.java.clients.retry.RetryConfigProxy;
import io.github.resilience4j.core.IntervalFunction;
import io.github.resilience4j.retry.Retry;
import io.github.resilience4j.retry.RetryConfig;
import java.time.Duration;
import java.util.Objects;
import org.springframework.web.reactive.function.client.WebClientResponseException;

public class RetryConfiguration {

    private RetryConfiguration() { }

    public static Retry start(RetryConfigProxy proxy) {
        RetryConfig config = switch (proxy.getPolicy()) {
            case CONSTANT -> startConstantRetry(proxy);
            case LINEAR -> startLinearRetry(proxy);
            case EXPONENTIAL -> startExponentialRetry(proxy);
            default -> throw new IllegalArgumentException("Invalid retry policy: " + proxy.getPolicy());
        };

        return Retry.of("retry", Objects.requireNonNull(config));
    }

    private static RetryConfig startConstantRetry(RetryConfigProxy proxy) {
        return RetryConfig.<WebClientResponseException>custom()
            .maxAttempts(proxy.getMaxRetries())
            .waitDuration(Duration.ofSeconds(proxy.getRetryDelay()))
            .retryOnException(response -> proxy.getHttpStatuses().contains(
                ((WebClientResponseException) response).getStatusCode()))
            .build();
    }

    private static RetryConfig startLinearRetry(RetryConfigProxy proxy) {
        return RetryConfig.<WebClientResponseException>custom()
            .maxAttempts(proxy.getMaxRetries())
            .intervalFunction(IntervalFunction.of(
                proxy.getRetryDelay(),
                attempt -> proxy.getRetryDelay() + attempt + proxy.getIncrement())
            )
            .retryOnException(response -> proxy.getHttpStatuses().contains(
                ((WebClientResponseException) response).getStatusCode()))
            .build();
    }

    private static RetryConfig startExponentialRetry(RetryConfigProxy proxy) {
        return RetryConfig.<WebClientResponseException>custom()
            .maxAttempts(proxy.getMaxRetries())
            .intervalFunction(IntervalFunction
                .ofExponentialRandomBackoff(
                    Duration.ofSeconds(proxy.getRetryDelay()),
                    proxy.getIncrement()
                )
            )
            .retryOnException(response -> proxy.getHttpStatuses().contains(
                ((WebClientResponseException) response).getStatusCode()))
            .build();
    }
}
