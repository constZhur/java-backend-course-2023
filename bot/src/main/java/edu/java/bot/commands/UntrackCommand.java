package edu.java.bot.commands;

import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendMessage;
import edu.java.bot.client.ScrapperClient;
import edu.java.bot.dto.request.RemoveLinkRequest;
import edu.java.bot.exception.BotApiBadRequestException;
import edu.java.bot.exception.BotApiNotFoundException;
import java.net.URI;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class UntrackCommand implements Command {
    private final ScrapperClient client;

    private static final String COMMAND = "/untrack";
    private static final String DESCRIPTION = "Прекратить отслеживание ссылки";

    private static final String INSUFFICIENT_PARAMETERS_MESSAGE =
        "Недостаточно параметров. Введите все необходимые данные!";
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
        Long chatId = update.message().chat().id();
        String[] commandParts = update.message().text().split(" +", 2);

        if (commandParts.length < 2) {
            log.error("Недостаточно параметров при обработке команды /untrack для чата: {}", chatId);
            return new SendMessage(chatId, INSUFFICIENT_PARAMETERS_MESSAGE);
        }

        String link = commandParts[1];

        try {
            client.deleteLink(chatId, new RemoveLinkRequest(URI.create(link)));
            log.info("Обработка команды /untrack для чата: {}. Результат: {}", chatId,
                LINK_REMOVED_SUCCESSFULLY_MESSAGE);
            return new SendMessage(chatId, LINK_REMOVED_SUCCESSFULLY_MESSAGE);
        } catch (BotApiBadRequestException | BotApiNotFoundException e) {
            log.error("Ошибка при обработке команды /untrack для чата: {}. Ошибка: {}", chatId,
                NO_SUCH_TRACKED_LINK_MESSAGE);
            return new SendMessage(chatId, NO_SUCH_TRACKED_LINK_MESSAGE);
        }
    }
}
