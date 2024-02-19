package edu.java.bot.commands;

import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendMessage;
import edu.java.bot.models.User;
import edu.java.bot.parsers.LinkParser;
import edu.java.bot.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import java.net.URI;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class TrackCommand implements Command, LinkSupport{
    private final UserRepository userRepository;
    private final List<? extends LinkParser> links;

    private final String COMMAND = "/track";
    private final String DESCRIPTION = "Начать отслеживать ссылку";

    private final String REQUEST_LINK_MESSAGE = "Введите ссылку для отслеживания:";
    private final String USER_NOT_FOUND_MESSAGE = "Пользователь не найден.\n"
        + "Перезапустите бота с помощью /start";
    private final String WRONG_LINK_FORMAT_MESSAGE = "Неверный формат ссылки";
    private final String LINK_ADDED_SUCCESSFULLY_MESSAGE = "Ссылка добавлена!";
    private final String LINK_ALREADY_TRACKED_MESSAGE = "Данный ресурс уже отслеживается";
    private final String UNSUPPORTED_RESOURCE_MESSAGE = "Данный ресурс не поддерживается!";


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
        if (isInvalidInput(update)) {
            log.info("Неверный ввод при обработке команды /track для чата: {}", update.message().chat().id());
            return new SendMessage(update.message().chat().id(), REQUEST_LINK_MESSAGE);
        }
        long chatId = update.message().chat().id();
        String link = update.message().text();

        User user = userRepository.getById(chatId);
        if (user == null) {
            log.info("Пользователь с id чата {} не был найден", chatId);
            return new SendMessage(chatId, USER_NOT_FOUND_MESSAGE);
        }

        if (!(link.startsWith("http://") || link.startsWith("https://"))) {
            log.info("Неверный формат для чата: {}", chatId);
            return new SendMessage(chatId, WRONG_LINK_FORMAT_MESSAGE);
        }

        URI uri = URI.create(link);
        if (isSupportedLink(uri, links)) {
            boolean isLinkNew = user.getLinks().add(link);
            log.info("Обработка команды /track для чата: {}. Результат: {}", chatId, isLinkNew ?
                LINK_ADDED_SUCCESSFULLY_MESSAGE : LINK_ALREADY_TRACKED_MESSAGE);
            return new SendMessage(chatId, isLinkNew ?
                LINK_ADDED_SUCCESSFULLY_MESSAGE : LINK_ALREADY_TRACKED_MESSAGE);
        } else {
            log.info("Обработка команды /track для чата: {}. Результат: {}", chatId, UNSUPPORTED_RESOURCE_MESSAGE);
            return new SendMessage(chatId, UNSUPPORTED_RESOURCE_MESSAGE);
        }
    }
}
