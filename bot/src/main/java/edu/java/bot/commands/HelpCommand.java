package edu.java.bot.commands;

import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class HelpCommand implements Command {
    private static final String COMMAND = "/help";
    private static final String DESCRIPTION = "Показать рекомендации по использованию бота";

    private static final String BOT_USAGE_INFORMATION =
        "Этот бот позволяет получать уведомления от различных ресурсов в интернете.\n"
        + "В данный момент бот поддерживает оповещения об обновлениях на ресурсах: GitHub, StackOverflow.\n\n"
        + "Вам предоставляется следующий набор команд для управления:\n\n"
        + "/start - зарегистрирвоваться и начать работу с ботом;\n"
        + "/help - получить информацию о взаимойдествии с ботом;\n"
        + "/track - начать отслеживать ссылку;\n"
        + "/untrack - прекратить отслеживание ссылки;\n"
        + "/list - получить список всех отслеживаемых ресурсов.";


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
        log.info("Обработка команды /help для чата: {}", update.message().chat().id());
        return new SendMessage(update.message().chat().id(), BOT_USAGE_INFORMATION);
    }
}
