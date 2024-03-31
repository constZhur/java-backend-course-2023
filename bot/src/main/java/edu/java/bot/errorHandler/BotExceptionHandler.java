package edu.java.bot.errorHandler;

import edu.java.bot.dto.response.ApiErrorResponse;
import java.util.Arrays;
import java.util.stream.Collectors;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class BotExceptionHandler {
    @ExceptionHandler
    public ResponseEntity<?> handle(Exception exception) {
        return new ResponseEntity<>(
            new ApiErrorResponse(
                exception.getMessage(),
                HttpStatus.BAD_REQUEST.toString(),
                exception.getClass().getSimpleName(),
                exception.getMessage(),
                Arrays.stream(exception.getStackTrace())
                    .map(StackTraceElement::toString)
                    .collect(Collectors.toList())),
            HttpStatus.BAD_REQUEST
        );

    }
}
