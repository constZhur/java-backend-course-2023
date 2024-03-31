package edu.java.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import java.time.OffsetDateTime;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "link")
public class Link {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @NotNull
    @Column(name = "url")
    private String url;

    @Column(name = "checked_at")
    OffsetDateTime checkedAt;

    @ManyToMany(mappedBy = "links")
    private List<User> tgChats;

    public Link(String url) {
        this.url = url;
    }

    public Link(String url, OffsetDateTime checkedAt) {
        this.url = url;
        this.checkedAt = checkedAt;
    }

    public Link(Integer id, String url, OffsetDateTime checkedAt) {
        this.id = id;
        this.url = url;
        this.checkedAt = checkedAt;
    }
}
