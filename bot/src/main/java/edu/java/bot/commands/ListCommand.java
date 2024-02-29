package edu.java.bot.commands;

import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendMessage;
import edu.java.bot.models.User;
import edu.java.bot.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class ListCommand implements Command {
    private final UserRepository userRepository;

    private static final String COMMAND = "/list";
    private static final String DESCRIPTION = "Показать список всех отслеживаемых ссылок";

    private static final String USER_NOT_FOUND = "Пользователь не был найден.\n"
        + "Запустите бота заново, использя команду /start";
    private static final String EMPTY_LIST_MESSAGE = "В данный момент вы не отслеживание никакие ресурсы.\n"
        + "Начать отслеживание ссылки можно при помощи команды /track";
    private static final String TRACKED_LINKS_HEADER = "Список отслеживаемых ссылок:\n";


    @Override
    public String command() {
        return COMMAND;
    }

    @Override
    public String description() {
        return DESCRIPTION;
    }

    @Override
    public SendMessage handle(Update update) {
        Long chatId = update.message().chat().id();
        User currentUser = userRepository.getById(chatId);

        if (currentUser == null) {
            log.info("Пользователь с id чата {} не был найден", chatId);
            return new SendMessage(chatId, USER_NOT_FOUND);
        }

        StringBuilder userLinks = new StringBuilder();
        currentUser.getLinks().forEach(link -> userLinks.append("- ").append(link).append("\n"));


        log.info("Обработка команды /list для чата: {}", chatId);
        return new SendMessage(chatId,
            userLinks.isEmpty() ? EMPTY_LIST_MESSAGE : TRACKED_LINKS_HEADER + userLinks)
            .disableWebPagePreview(true);
    }
}
