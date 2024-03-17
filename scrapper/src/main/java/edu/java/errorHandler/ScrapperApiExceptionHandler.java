package edu.java.errorHandler;

import edu.java.dto.response.ApiErrorResponse;
import edu.java.exception.AddedLinkExistsException;
import edu.java.exception.ChatNotFoundException;
import edu.java.exception.LinkNotFoundException;
import edu.java.exception.RegisteredUserExistsException;
import jakarta.validation.constraints.NotNull;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;

@RestControllerAdvice
public class ScrapperApiExceptionHandler {
    private ApiErrorResponse createError(@NotNull Throwable exception, String description, HttpStatus httpStatus) {
        List<String> stacktrace = Arrays.stream(exception.getStackTrace())
            .map(StackTraceElement::toString)
            .collect(Collectors.toList());
        return new ApiErrorResponse(
            description, Integer.toString(httpStatus.value()),
            httpStatus.getReasonPhrase(), exception.getMessage(), stacktrace
        );
    }

    @ExceptionHandler({ChatNotFoundException.class,
        LinkNotFoundException.class,
        AddedLinkExistsException.class,
        RegisteredUserExistsException.class}
    )
    public ResponseEntity<ApiErrorResponse> handleException(Exception error) {
        HttpStatus status = (error instanceof ChatNotFoundException || error instanceof LinkNotFoundException
            || error instanceof AddedLinkExistsException || error instanceof RegisteredUserExistsException)
            ? BAD_REQUEST : INTERNAL_SERVER_ERROR;
        return ResponseEntity.status(status)
            .body(createError(error, error.getMessage(), status));
    }
}
