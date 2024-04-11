package edu.java.scrapper.unit.rateLimiter;

import edu.java.configuration.rateLimit.RateLimitInterceptor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class RateLimitInterceptorTest {

    private RateLimitInterceptor rateLimitInterceptor;
    private MockHttpServletRequest request;
    private MockHttpServletResponse response;


    @BeforeEach
    void setUp() {
        rateLimitInterceptor = new RateLimitInterceptor(15, 30);

        request = new MockHttpServletRequest();
        response = new MockHttpServletResponse();
    }

    @Test
    void preHandle_ShouldAllowRequest_WhenBucketHasSufficientCapacity() {
        boolean result = rateLimitInterceptor.preHandle(request, response, null);

        assertThat(result).isTrue();
        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
    }

    @Test
    void preHandle_ShouldRejectRequest_WhenBucketIsFull() throws Exception {
        for (int i = 0; i < 15; i++) {
            rateLimitInterceptor.preHandle(request, response, null);
        }

        boolean result = rateLimitInterceptor.preHandle(request, response, null);

        assertThat(result).isFalse();
        assertThat(response.getStatus()).isEqualTo(HttpStatus.TOO_MANY_REQUESTS.value());
        assertThat(response.getErrorMessage()).isEqualTo("Слишком много запросов! Попробуйте позже..");
    }
}
