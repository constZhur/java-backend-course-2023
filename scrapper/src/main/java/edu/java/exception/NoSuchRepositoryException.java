package edu.java.exception;

import edu.java.dto.response.ErrorResponse;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class NoSuchRepositoryException extends RuntimeException {
    private final ErrorResponse response;
}
