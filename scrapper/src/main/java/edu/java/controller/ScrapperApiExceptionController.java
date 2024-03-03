package edu.java.controller;

import edu.java.dto.response.ApiErrorResponse;
import edu.java.exception.AddedLinkExistsException;
import edu.java.exception.ChatNotFoundException;
import edu.java.exception.LinkNotFoundException;
import edu.java.exception.RegisteredUserExistsException;
import jakarta.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;

@RestControllerAdvice
public class ScrapperApiExceptionController {
    private ApiErrorResponse createError(@NotNull Throwable exception, String description, HttpStatus httpStatus) {
        List<String> stacktrace = new ArrayList<>(exception.getStackTrace().length);
        for (StackTraceElement line : exception.getStackTrace()) {
            stacktrace.add(line.toString());
        }
        return new ApiErrorResponse(
            description, Integer.toString(httpStatus.value()),
            httpStatus.getReasonPhrase(), exception.getMessage(), stacktrace
        );
    }

    @ExceptionHandler(ChatNotFoundException.class)
    public ResponseEntity<ApiErrorResponse> chatNotFound(@NotNull ChatNotFoundException error) {
        return ResponseEntity.status(BAD_REQUEST)
            .body(createError(error, error.getMessage(), BAD_REQUEST));
    }

    @ExceptionHandler(LinkNotFoundException.class)
    public ResponseEntity<ApiErrorResponse> linkNotFound(@NotNull LinkNotFoundException error) {
        return ResponseEntity.status(BAD_REQUEST)
            .body(createError(error, error.getMessage(), BAD_REQUEST));
    }

    @ExceptionHandler(AddedLinkExistsException.class)
    public ResponseEntity<ApiErrorResponse> linkAlreadyExist(@NotNull AddedLinkExistsException error) {
        return ResponseEntity.status(BAD_REQUEST)
            .body(createError(error, error.getMessage(), BAD_REQUEST));
    }

    @ExceptionHandler(RegisteredUserExistsException.class)
    public ResponseEntity<ApiErrorResponse> registeredUserExists(@NotNull RegisteredUserExistsException error) {
        return ResponseEntity.status(BAD_REQUEST)
            .body(createError(error, error.getMessage(), BAD_REQUEST));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiErrorResponse> otherException(@NotNull Exception error) {
        return ResponseEntity.status(INTERNAL_SERVER_ERROR)
            .body(createError(error, error.getMessage(), INTERNAL_SERVER_ERROR));
    }
}
