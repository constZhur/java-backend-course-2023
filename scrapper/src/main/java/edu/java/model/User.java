package edu.java.model;

import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class User {
    private Long id;
    private String name;
    private Set<Link> links;

    public User(Long id) {
        this.id = id;
    }

    public User(Long id, Set<Link> links) {
        this.id = id;
        this.links = links;
    }
}
