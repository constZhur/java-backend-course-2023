package edu.java.bot.client;

import edu.java.bot.dto.request.AddLinkRequest;
import edu.java.bot.dto.request.RemoveLinkRequest;
import edu.java.bot.dto.response.LinkResponse;
import edu.java.bot.dto.response.ListLinksResponse;

public interface WebScrapperClient {
    ListLinksResponse getAllLinks(Long tgChatId);

    LinkResponse addLink(Long tgChatId, AddLinkRequest addLinkRequest);

    LinkResponse deleteLink(Long tgChatId, RemoveLinkRequest removeLinkRequest);

    void registerChat(Long id);

    void deleteChat(Long id);

}
