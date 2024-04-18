package edu.java.clients.interfaces;

import edu.java.dto.request.LinkUpdateRequest;

public interface UpdateSender {
    void send(LinkUpdateRequest update);
}
