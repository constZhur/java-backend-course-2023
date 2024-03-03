package edu.java.clients.interfaces;

import edu.java.clients.dto.stackoverflow.StackoverflowItemsResponse;

public interface WebClientStackoverflow {
    StackoverflowItemsResponse fetchStackOverflowQuestion(long id);
}
