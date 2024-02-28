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
public class UntrackCommand implements Command {
    private final UserRepository userRepository;

    private static final String COMMAND = "/untrack";
    private static final String DESCRIPTION = "Прекратить отслеживание ссылки";

    private static final String INSUFFICIENT_PARAMETERS_MESSAGE =
        "Недостаточно параметров. Введите все необходимые данные!";
    private static final String USER_NOT_FOUND_MESSAGE = "Пользователь не найден.\n"
        + "Перезапустите бота с помощью /start";
    private static final String LINK_REMOVED_SUCCESSFULLY_MESSAGE = "Отслеживание ссылки прекращено!";
    private static final String NO_SUCH_TRACKED_LINK_MESSAGE = "Данного ресурса нет среди отслеживаемых";

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
            log.info("Недостаточно параметров при обработке команды /untrack для чата: {}", chatId);
            return new SendMessage(chatId, INSUFFICIENT_PARAMETERS_MESSAGE);
        }

        String link = commandParts[1];
        User user = userRepository.getById(chatId);

        if (user == null) {
            log.info("Пользователь с id чата {} не был найден", chatId);
            return new SendMessage(chatId, USER_NOT_FOUND_MESSAGE);
        }

        boolean isLinkTraced = user.getLinks().remove(link);
        log.info("Обработка команды /untrack для чата: {}. Результат: {}", chatId,
            isLinkTraced ? LINK_REMOVED_SUCCESSFULLY_MESSAGE
                : NO_SUCH_TRACKED_LINK_MESSAGE);
        return new SendMessage(chatId, isLinkTraced ? LINK_REMOVED_SUCCESSFULLY_MESSAGE
            : NO_SUCH_TRACKED_LINK_MESSAGE);

    }
}
