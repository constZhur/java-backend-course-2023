package edu.java.service.update.handler.stackoverflow;

import edu.java.clients.dto.stackoverflow.StackoverflowItemsResponse;
import edu.java.clients.impl.StackoverflowClient;
import edu.java.dto.Update;
import edu.java.model.Link;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import edu.java.service.update.handler.UpdateHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class StackoverflowUpdateHandler implements UpdateHandler {
    private static final String HOSTNAME = "stackoverflow.com";

    private final int questionIndex = 4;

    private final StackoverflowClient stackoverflowClient;

    @Override
    public String hostName() {
        return HOSTNAME;
    }

    @Override
    public List<Optional<Update>> fetchUpdates(Link link) {
        String url = link.getUrl();
        long questionId = Long.parseLong(url.split("/")[questionIndex]);
        StackoverflowItemsResponse response = stackoverflowClient.fetchStackOverflowQuestionRetry(questionId);

        Optional<Update> update = Optional.empty();
        for (var item : response.items()) {
            if (item.lastActivityDate().isAfter(link.getCheckedAt())) {
                update = Optional.of(new Update(
                    link.getId(),
                    url,
                    "Обновления в вопросе"
                ));
            }
        }
        return Collections.singletonList(update);
    }
}
