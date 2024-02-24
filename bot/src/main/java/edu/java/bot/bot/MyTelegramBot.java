package edu.java.bot.bot;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.UpdatesListener;
import com.pengrad.telegrambot.model.BotCommand;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.BaseRequest;
import com.pengrad.telegrambot.request.SendMessage;
import com.pengrad.telegrambot.request.SetMyCommands;
import com.pengrad.telegrambot.response.BaseResponse;
import edu.java.bot.commands.Command;
import edu.java.bot.configuration.ApplicationConfig;
import edu.java.bot.messageHandler.UserMessageProcessor;
import java.util.List;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@RequiredArgsConstructor
@Component
public class MyTelegramBot implements Bot {
    private TelegramBot bot;
    private final ApplicationConfig config;
    private final UserMessageProcessor userMessageProcessor;

    @Override
    public <T extends BaseRequest<T, R>, R extends BaseResponse> void execute(BaseRequest<T, R> request) {
        try {
            bot.execute(request);
            log.info("Запрос бота \"{}\" успешно выполнен. Идентификатор чата: {}",
                request.getMethod(), request.getParameters().get("chat_id"));
        } catch (Exception e) {
            log.error("Ошибка выполнения запроса бота: {}", e.getMessage(), e);
        }
    }

    @Override
    public int process(List<Update> updates) {
        updates.forEach(update -> {
            try {
                SendMessage msg = userMessageProcessor.process(update);
                execute(msg);
            } catch (Exception e) {
                log.error("Ошибка обработки обновления: {}", e.getMessage(), e);
            }
        });
        return UpdatesListener.CONFIRMED_UPDATES_ALL;
    }

    @Override
    @PostConstruct
    public void start() {
        bot = new TelegramBot(config.telegramToken());

        bot.execute(new SetMyCommands(userMessageProcessor
            .commands()
            .stream()
            .map(Command::toApiCommand)
            .toArray(BotCommand[]::new)));

        bot.setUpdatesListener(this::process);
        log.info("Бот успешно запущен");
    }

    @Override
    public void close() {
        bot.removeGetUpdatesListener();
        log.info("Слушатель обновлений успешно удален");
    }
}
