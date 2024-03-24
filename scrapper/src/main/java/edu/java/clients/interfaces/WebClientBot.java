package edu.java.clients.interfaces;

import edu.java.dto.request.LinkUpdateRequest;
import org.springframework.http.HttpStatus;

public interface WebClientBot {
    HttpStatus sendUpdates(LinkUpdateRequest linkUpdate);
}
