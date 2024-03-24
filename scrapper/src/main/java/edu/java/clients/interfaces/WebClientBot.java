package edu.java.clients.interfaces;

import edu.java.dto.response.LinkUpdateResponse;
import org.springframework.http.HttpStatus;

public interface WebClientBot {
    HttpStatus sendUpdates(LinkUpdateResponse linkUpdate);
}
