package edu.java.clients.retry;

import jakarta.validation.constraints.NotNull;
import java.util.List;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;

@Getter
@Setter
@Builder
public class RetryConfigProxy {
    @NotNull
    private RetryPolicy policy;

    @NotNull
    private Integer maxRetries;

    @NotNull
    private Long retryDelay;

    @NotNull
    private Integer increment;

    @NotNull
    private List<HttpStatus> httpStatuses;
}
