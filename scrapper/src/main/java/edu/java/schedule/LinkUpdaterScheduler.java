package edu.java.schedule;

import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@EnableScheduling
@Slf4j
@Component
public class LinkUpdaterScheduler {

    @Scheduled(fixedDelayString = "#{@scheduler.interval().toMillis()}")
    public void update() {
        log.info("Началось обновление ссылок");
        // Когда-то здесь будет обновление ссылок
        log.info("Обновление ссылок завершено");
    }
}
