package edu.java.bot.commands;

import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendMessage;
import edu.java.bot.client.ScrapperClient;
import edu.java.bot.exception.BotApiBadRequestException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class StartCommand implements Command {
    private final ScrapperClient client;

    private static final String COMMAND = "/start";
    private static final String DESCRIPTION = "Зарегистироваться в боте";

    private static final String WELCOME_MESSAGE = "Добро пожаловать!\nЧтобы увидеть список команд, используйте /help";
    private static final String BOT_ALREADY_RUNNING_MESSAGE = "Бот уже запущен и готов к использованию";

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

        try {
            client.registerChat(chatId);
            log.info("Обработка команды /start для нового пользователя: {}", chatId);
            return new SendMessage(chatId, WELCOME_MESSAGE);
        } catch (BotApiBadRequestException e) {
            log.error("Обработка команды /start для существующего пользователя: {}", chatId);
            return new SendMessage(chatId, BOT_ALREADY_RUNNING_MESSAGE);
        }
    }
}
