package edu.java.schedule;

import edu.java.clients.interfaces.UpdateSender;
import edu.java.dto.request.LinkUpdateRequest;
import edu.java.service.update.LinkUpdateService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@EnableScheduling
@Slf4j
@Component
@RequiredArgsConstructor
public class LinkUpdaterScheduler {

    @Value("${api.link-updater.interval}")
    private Long interval;
    @Value("${api.link-updater.limit}")
    private Long linksLimit;

    private final UpdateSender sender;
    private final LinkUpdateService updateService;

    @Scheduled(fixedDelayString = "#{@scheduler.interval().toMillis()}")
    public void update() {
        log.info("Началось обновление ссылок");

        List<LinkUpdateRequest> updateRequests = updateService.fetchAllUpdates(linksLimit, interval);
        for (var updateRequest : updateRequests) {
            sender.send(updateRequest);
        }

        log.info("Обновление ссылок завершено");
    }
}
