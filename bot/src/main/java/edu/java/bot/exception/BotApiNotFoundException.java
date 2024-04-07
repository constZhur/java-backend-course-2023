package edu.java.bot.exception;

import edu.java.bot.dto.response.ApiErrorResponse;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class BotApiNotFoundException extends RuntimeException {
    private final ApiErrorResponse apiErrorResponse;
}
