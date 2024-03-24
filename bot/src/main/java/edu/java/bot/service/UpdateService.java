package edu.java.bot.service;

import edu.java.bot.dto.request.LinkUpdateRequest;
import edu.java.bot.exception.AddedLinkExistsException;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UpdateService {
    List<LinkUpdateRequest> updates = new ArrayList<>();

    public void addUpdate(LinkUpdateRequest request) {
        if (updates.contains(request)) {
            throw new AddedLinkExistsException();
        } else {
            updates.add(request);
        }
    }
}
