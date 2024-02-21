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
public class UntrackCommand implements Command, LinkSupport {
    private final UserRepository userRepository;

    private static final String COMMAND = "/untrack";
    private static final String DESCRIPTION = "Прекратить отслеживание ссылки";

    private static final String REQUEST_LINK_MESSAGE = "Введите ссылку, за которой необходимо перестать следить:";
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
        if (isInvalidInput(update)) {
            log.info("Неверный ввод при обработке команды /untrack для чата: {}", update.message().chat().id());
            return new SendMessage(update.message().chat().id(), REQUEST_LINK_MESSAGE);
        }

        long chatId = update.message().chat().id();
        String link = update.message().text();

        User user = userRepository.getById(chatId);
        if (user == null) {
            log.info("Пользователь с id чата {} не был найден", chatId);
            return new SendMessage(chatId, USER_NOT_FOUND_MESSAGE);
        }

        boolean isLinkTraced = user.getLinks().remove(link);
        log.info("Обработка команды /track для чата: {}. Результат: {}", chatId,
            isLinkTraced ? LINK_REMOVED_SUCCESSFULLY_MESSAGE : NO_SUCH_TRACKED_LINK_MESSAGE);
        return new SendMessage(chatId, isLinkTraced ? LINK_REMOVED_SUCCESSFULLY_MESSAGE : NO_SUCH_TRACKED_LINK_MESSAGE);
    }
}
