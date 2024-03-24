package edu.java.exception;

public class RegisteredUserExistsException extends RuntimeException {
    public static final String DEFAULT_MESSAGE = "Повторная регистрация: пользователь уже существует";

    public RegisteredUserExistsException() {
        super(DEFAULT_MESSAGE);
    }

    public RegisteredUserExistsException(String message) {
        super(message);
    }
}
