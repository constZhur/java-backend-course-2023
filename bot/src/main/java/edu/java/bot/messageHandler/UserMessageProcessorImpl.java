package edu.java.bot.messageHandler;

import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendMessage;
import edu.java.bot.commands.Command;
import edu.java.bot.commands.TrackCommand;
import edu.java.bot.commands.UntrackCommand;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class UserMessageProcessorImpl implements UserMessageProcessor {
    private Command currentCommand;
    private final List<? extends Command> commands;

    public static final String HELP_COMMAND_MESSAGE =
        "Ознакомиться с правилами использования бота при помощи /help";
    private static final String ONLY_TEXT_COMMANDS_SUPPORTED_MESSAGE =
        "Бот поддерживает обработку только текстовых сообщений\n"
        + HELP_COMMAND_MESSAGE;
    private static final String UNSUPPORTED_COMMAND_MESSAGE = "Такая команда не поддерживается ботом\n"
        + HELP_COMMAND_MESSAGE;


    @Override
    public List<? extends Command> commands() {
        return commands;
    }

    @Override
    public SendMessage process(Update update) {
        SendMessage message = null;

        if (isNotText(update)) {
            message = new SendMessage(update.message().chat().id(), ONLY_TEXT_COMMANDS_SUPPORTED_MESSAGE);
        } else if (isCommand(update)) {
            message = handleCommand(update);
        } else if (isInputRequired()) {
            message = handleInput(update);
        }

        return message != null ? message : new SendMessage(update.message().chat().id(), UNSUPPORTED_COMMAND_MESSAGE);
    }

    private boolean isNotText(Update update) {
        return update.message().text() == null;
    }

    private boolean isCommand(Update update) {
        String text = update.message().text();
        return text != null && text.strip().startsWith("/");
    }

    private boolean isInputRequired() {
        return currentCommand instanceof TrackCommand || currentCommand instanceof UntrackCommand;
    }

    private SendMessage handleCommand(Update update) {
        SendMessage msg = null;
        for (Command command : commands) {
            if (command.supports(update)) {
                msg = command.handle(update);
                currentCommand = command;
                break;
            }
        }
        return msg;
    }

    private SendMessage handleInput(Update update) {
        var message = currentCommand.handle(update);
        currentCommand = null;
        return message;
    }
}
