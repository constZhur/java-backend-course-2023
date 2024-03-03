package edu.java.bot.exception;

public class AddedLinkExistsException extends RuntimeException {
    public static final String DEFAULT_MESSAGE = "Повторное добавление ссылки: ссылка уже существует";

    public AddedLinkExistsException() {
        super(DEFAULT_MESSAGE);
    }

    public AddedLinkExistsException(String message) {
        super(message);
    }
}
