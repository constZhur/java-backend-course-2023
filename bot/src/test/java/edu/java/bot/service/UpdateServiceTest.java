package edu.java.bot.service;

import com.pengrad.telegrambot.request.SendMessage;
import edu.java.bot.bot.MyTelegramBot;
import edu.java.bot.dto.request.LinkUpdateRequest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.net.URI;
import java.util.List;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class UpdateServiceTest {

    @Mock
    private MyTelegramBot mockBot;

    @InjectMocks
    private UpdateService updateService;

    @Test
    public void testProcessUpdate() {
        LinkUpdateRequest update = new LinkUpdateRequest(1L, URI.create("http://github.com"), "Description", List.of(1L, 2L));

        updateService.processUpdate(update);

        verify(mockBot, times(2)).execute(any(SendMessage.class));
    }
}
