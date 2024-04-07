package edu.java.bot.commands;

import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendMessage;
import edu.java.bot.client.ScrapperClient;
import edu.java.bot.dto.request.AddLinkRequest;
import edu.java.bot.exception.BotApiBadRequestException;
import edu.java.bot.parsers.LinkParser;
import java.net.URI;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class TrackCommand implements Command {
    private final ScrapperClient client;
    private final List<? extends LinkParser> links;

    private static final String COMMAND = "/track";
    private static final String DESCRIPTION = "Начать отслеживать ссылку";

    private static final String INSUFFICIENT_PARAMETERS_MESSAGE =
        "Недостаточно параметров. Введите все необходимые данные!";
    private static final String WRONG_COMMAND_FORMAT_MESSAGE = "Неверный формат команды";
    private static final String LINK_ADDED_SUCCESSFULLY_MESSAGE = "Ссылка добавлена!";
    private static final String LINK_ALREADY_TRACKED_MESSAGE = "Данный ресурс уже отслеживается";

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
            log.error("Недостаточно параметров при обработке команды /track для чата: {}", chatId);
            return new SendMessage(chatId, INSUFFICIENT_PARAMETERS_MESSAGE);
        }

        String link = commandParts[1];

        return addLinkToTrack(chatId, link);
    }

    private SendMessage addLinkToTrack(Long chatId, String link) {
        URI uri = URI.create(link);
        for (LinkParser l : links) {
            if (l.parseLink(uri)) {
                try {
                    client.addLink(chatId, new AddLinkRequest(uri));
                    return new SendMessage(chatId, LINK_ADDED_SUCCESSFULLY_MESSAGE);
                } catch (BotApiBadRequestException e) {
                    return new SendMessage(chatId, LINK_ALREADY_TRACKED_MESSAGE);
                }
            }
        }
        return new SendMessage(chatId, WRONG_COMMAND_FORMAT_MESSAGE);
    }
}
