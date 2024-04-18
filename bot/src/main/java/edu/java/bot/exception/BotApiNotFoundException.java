package edu.java.bot.exception;

import edu.java.bot.dto.response.ApiErrorResponse;
import java.nio.charset.Charset;
import lombok.Getter;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.web.reactive.function.client.WebClientResponseException;

@Getter
public class BotApiNotFoundException extends WebClientResponseException {
    private final ApiErrorResponse apiErrorResponse;

    public BotApiNotFoundException(ApiErrorResponse apiErrorResponse) {
        super(HttpStatus.NOT_FOUND.value(), "Not found", HttpHeaders.EMPTY, null, Charset.defaultCharset());
        this.apiErrorResponse = apiErrorResponse;
    }
}
