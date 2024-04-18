package edu.java.bot.messageHandler;

import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendMessage;
import edu.java.bot.commands.Command;
import io.micrometer.core.annotation.Counted;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@RequiredArgsConstructor
@Component
public class UserMessageProcessorImpl implements UserMessageProcessor {
    private final List<? extends Command> commands;

    public static final String HELP_COMMAND_MESSAGE =
        "Ознакомиться с правилами использования бота при помощи /help";
    private static final String ONLY_TEXT_COMMANDS_SUPPORTED_MESSAGE =
        "Бот поддерживает обработку только текстовых сообщений\n"
        + HELP_COMMAND_MESSAGE;
    private static final String UNSUPPORTED_COMMAND_MESSAGE =
        "Такая команда не поддерживается ботом\n"
        + HELP_COMMAND_MESSAGE;
    private static final String ERROR_MESSAGE =
        "Возникла ошибка, попробуйте перезапустить телеграмм бота";


    @Override
    public List<? extends Command> commands() {
        return commands;
    }

    @Override
    @Counted(value = "bot_requests_counter", description = "Number of request to Telegram bot")
    public SendMessage process(Update update) {
        Long chatId = update.message().chat().id();

        if (isNotText(update)) {
            return new SendMessage(chatId, ONLY_TEXT_COMMANDS_SUPPORTED_MESSAGE);
        }

        try {
            for (Command command : commands) {
                if (command.supports(update)) {
                    return command.handle(update);
                }
            }
        } catch (Exception e) {
            log.error("Возникла ошибка в течение выполнения обработки комманды", e);
            return new SendMessage(chatId, ERROR_MESSAGE);
        }

        return new SendMessage(chatId, UNSUPPORTED_COMMAND_MESSAGE);
    }

    private boolean isNotText(Update update) {
        String text = update.message().text();
        return text == null || text.isEmpty();
    }
}
