package edu.java.bot.commands;

import com.pengrad.telegrambot.request.SendMessage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
public class HelpCommandTest extends AbstractCommandTest {

    private HelpCommand helpCommand;

    @BeforeEach
    public void setUp() {
        super.setUp();
        helpCommand = new HelpCommand();
    }

    @Test
    public void testCommand() {
        assertThat(helpCommand.command()).isEqualTo("/help");
    }

    @Test
    public void testDescription(){
        assertThat(helpCommand.description()).isEqualTo("Показать рекомендации по использованию бота");
    }

    @Test
    public void testHandle() {
        String expectedText =
            "Этот бот позволяет получать уведомления от различных ресурсов в интернете.\n"
            + "В данный момент бот поддерживает оповещения об обновлениях на ресурсах: GitHub, StackOverflow.\n\n"
            + "Вам предоставляется следующий набор команд для управления:\n\n"
            + "/start - зарегистрирвоваться и начать работу с ботом;\n"
            + "/help - получить информацию о взаимойдествии с ботом;\n"
            + "/track <ссылка> - начать отслеживать ссылку;\n"
            + "/untrack <ссылка> - прекратить отслеживание ссылки;\n"
            + "/list - получить список всех отслеживаемых ресурсов.";;
        SendMessage msg = helpCommand.handle(mockUpdate);

        assertMessage(expectedText, msg);
    }


}
