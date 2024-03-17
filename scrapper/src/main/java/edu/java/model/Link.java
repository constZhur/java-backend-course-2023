package edu.java.model;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.OffsetDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class Link {
    private Long id;
    @NotNull
    private String url;
    OffsetDateTime checkedAt;

    public Link(String url) {
        this.url = url;
    }
}
