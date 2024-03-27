package edu.java.model;

import jakarta.validation.constraints.NotNull;
import java.time.OffsetDateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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

    public Link(String url, OffsetDateTime checkedAt) {
        this.url = url;
        this.checkedAt = checkedAt;
    }
}
