package edu.java.exception;

public class LinkNotFoundException extends RuntimeException {
    public static final String DEFAULT_MESSAGE = "Ссылка не найдена: искомая ссылка не существует";

    public LinkNotFoundException() {
        super(DEFAULT_MESSAGE);
    }

    public LinkNotFoundException(String message) {
        super(message);
    }
}
