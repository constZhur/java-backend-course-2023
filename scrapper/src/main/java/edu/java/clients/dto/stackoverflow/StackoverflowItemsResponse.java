package edu.java.clients.dto.stackoverflow;

import jakarta.validation.constraints.NotNull;
import java.util.List;

public record StackoverflowItemsResponse(@NotNull List<StackoverflowResponse> items) {
}
