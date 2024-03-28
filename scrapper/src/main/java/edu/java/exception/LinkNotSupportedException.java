package edu.java.exception;

public class LinkNotSupportedException extends RuntimeException {
    public static final String DEFAULT_MESSAGE = "Данная ссылка не поддерживается";

    public LinkNotSupportedException() {
        super(DEFAULT_MESSAGE);
    }

    public LinkNotSupportedException(String message) {
        super(message);
    }
}
