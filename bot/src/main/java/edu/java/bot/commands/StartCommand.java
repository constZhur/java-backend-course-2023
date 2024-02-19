package edu.java.bot.commands;

import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendMessage;
import edu.java.bot.models.User;
import edu.java.bot.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import java.util.HashSet;

@Slf4j
@Component
@RequiredArgsConstructor
public class StartCommand implements Command{
    private final UserRepository userRepository;

    private final String COMMAND = "/start";
    private final String DESCRIPTION = "Зарегистироваться в боте";

    private final String WELCOME_MESSAGE = "Добро пожаловать!\nЧтобы увидеть список команд, используйте /help";
    private final String BOT_ALREADY_RUNNING_MESSAGE = "Бот уже запущен и готов к использованию";

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
            userRepository.save(new User(chatId, new HashSet<>()));
            log.info("Обработка команды /start для нового пользователя: {}", chatId);
            return new SendMessage(update.message().chat().id(), WELCOME_MESSAGE);
        }
        log.info("Обработка команды /start для существующего пользователя: {}", chatId);
        return new SendMessage(update.message().chat().id(), BOT_ALREADY_RUNNING_MESSAGE);
    }


}
