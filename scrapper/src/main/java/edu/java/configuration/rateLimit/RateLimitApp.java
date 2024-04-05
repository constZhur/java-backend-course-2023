package edu.java.configuration.rateLimit;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import java.util.List;

@Component
@RequiredArgsConstructor
public class RateLimitApp implements WebMvcConfigurer {
    private  final RateLimitInterceptor interceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(interceptor).
            addPathPatterns(List.of("/links", "/tg-chat/**"));
    }
}
