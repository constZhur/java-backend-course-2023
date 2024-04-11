package edu.java.configuration.rateLimit;

import io.github.bucket4j.Bucket;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import lombok.SneakyThrows;
import org.springframework.http.HttpStatus;
import org.springframework.web.servlet.HandlerInterceptor;

public class RateLimitInterceptor implements HandlerInterceptor {

    private final Integer requests;
    private final Duration interval;
    private final Map<String, Bucket> buckets = new ConcurrentHashMap<>();

    public RateLimitInterceptor(Integer requests, Integer interval) {
        this.requests = requests;
        this.interval = Duration.ofSeconds(interval);
    }

    @SneakyThrows
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        String ip = request.getRemoteAddr();
        Bucket bucket = buckets.computeIfAbsent(ip, this::createBucket);
        if (bucket.tryConsume(1)) {
            return true;
        } else {
            response.sendError(HttpStatus.TOO_MANY_REQUESTS.value(), "Слишком много запросов! Попробуйте позже..");
            return false;
        }
    }

    private Bucket createBucket(String ipAddress) {
        return Bucket.builder()
            .addLimit(limit -> limit.capacity(requests)
                .refillIntervally(requests, interval))
            .build();
    }
}
