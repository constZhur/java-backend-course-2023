package edu.java.bot.commands;

import com.pengrad.telegrambot.request.SendMessage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.junit.jupiter.api.Assertions;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class HelpCommandTest extends AbstractCommandTest {

    @InjectMocks
    private HelpCommand helpCommand;

    @Test
    public void testCommand() {
        Assertions.assertEquals("/help", helpCommand.command());
    }

    @Test
    public void testDescription(){
        Assertions.assertEquals("Показать рекомендации по использованию бота",
            helpCommand.description());
    }

    @Test
    public void testHandle() {
        setUpMocks();

        String expectedText = "Этот бот позволяет получать уведомления от различных ресурсов в интернете.\n"
            + "В данный момент бот поддерживает оповещения об обновлениях на ресурсах: GitHub, StackOverflow.\n\n"
            + "Вам предоставляется следующий набор команд для управления:\n\n"
            + "/start - зарегистрирвоваться и начать работу с ботом;\n"
            + "/help - получить информацию о взаимойдествии с ботом;\n"
            + "/track - начать отслеживать ссылку;\n"
            + "/untrack - прекратить отслеживание ссылки;\n"
            + "/list - получить список всех отслеживаемых ресурсов.";
        SendMessage msg = helpCommand.handle(mockUpdate);

        assertMessage(expectedText, msg);
    }


}
