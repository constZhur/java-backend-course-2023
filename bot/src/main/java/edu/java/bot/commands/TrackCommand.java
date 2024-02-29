package edu.java.bot.commands;

import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendMessage;
import edu.java.bot.models.User;
import edu.java.bot.parsers.LinkParser;
import edu.java.bot.repositories.UserRepository;
import java.net.URI;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class TrackCommand implements Command {
    private final UserRepository userRepository;
    private final List<? extends LinkParser> links;

    private static final String COMMAND = "/track";
    private static final String DESCRIPTION = "Начать отслеживать ссылку";

    private static final String INSUFFICIENT_PARAMETERS_MESSAGE =
        "Недостаточно параметров. Введите все необходимые данные!";
    private static final String USER_NOT_FOUND_MESSAGE = "Пользователь не найден.\n"
        + "Перезапустите бота с помощью /start";
    private static final String WRONG_COMMAND_FORMAT_MESSAGE = "Неверный формат команды";
    private static final String LINK_ADDED_SUCCESSFULLY_MESSAGE = "Ссылка добавлена!";
    private static final String LINK_ALREADY_TRACKED_MESSAGE = "Данный ресурс уже отслеживается";
    private static final String UNSUPPORTED_RESOURCE_MESSAGE = "Данный ресурс не поддерживается!";

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
        long chatId = update.message().chat().id();
        String[] commandParts = update.message().text().split(" +", 2);

        if (commandParts.length < 2) {
            log.info("Недостаточно параметров при обработке команды /track для чата: {}", chatId);
            return new SendMessage(chatId, INSUFFICIENT_PARAMETERS_MESSAGE);
        }

        String link = commandParts[1];

        User user = userRepository.getById(chatId);
        if (user == null) {
            log.info("Пользователь с id чата {} не был найден", chatId);
            return new SendMessage(chatId, USER_NOT_FOUND_MESSAGE);
        }

        return addLinkToTrack(user, link);
    }

    private SendMessage addLinkToTrack(User user, String link) {
        long chatId = user.getId();

        URI uri = URI.create(link);
        for (LinkParser l : links) {
            if (l.parseLink(uri)) {
                boolean isLinkNew = user.getLinks().add(link);
                return new SendMessage(chatId, isLinkNew ? LINK_ADDED_SUCCESSFULLY_MESSAGE
                    : LINK_ALREADY_TRACKED_MESSAGE);
            }
        }
        return new SendMessage(chatId, WRONG_COMMAND_FORMAT_MESSAGE);
    }
}
