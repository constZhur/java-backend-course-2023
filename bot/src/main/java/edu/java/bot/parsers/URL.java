package edu.java.bot.parsers;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum URL {
    GITHUB("github.com"),
    STACK_OVERFLOW("stackoverflow.com");

    private final String hostName;
}
