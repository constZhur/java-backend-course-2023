package edu.java.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LinkUpdateService {
    private final UserService userService;
    private final LinkService linkService;
}
