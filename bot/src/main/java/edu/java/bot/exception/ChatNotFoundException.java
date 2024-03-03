package edu.java.bot.exception;

public class ChatNotFoundException extends RuntimeException {

    public static final String DEFAULT_MESSAGE = "Отсутствие чата: чат не найден";

    public ChatNotFoundException() {
        super(DEFAULT_MESSAGE);
    }

    public ChatNotFoundException(String message) {
        super(message);
    }
}
