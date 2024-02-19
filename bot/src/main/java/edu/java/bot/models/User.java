package edu.java.bot.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import java.util.Set;

@Data
@AllArgsConstructor
public class User {
    private Long id;
    private Set<String> links;
}
