package edu.java.controller;

import edu.java.dto.request.AddLinkRequest;
import edu.java.dto.request.RemoveLinkRequest;
import edu.java.dto.response.ApiErrorResponse;
import edu.java.dto.response.LinkResponse;
import edu.java.dto.response.ListLinksResponse;
import edu.java.model.Link;
import edu.java.model.User;
import edu.java.service.LinkService;
import edu.java.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.net.URI;
import java.net.URISyntaxException;
import java.time.OffsetDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Validated
public class ScrapperApiController {
    private final LinkService linkService;
    private final UserService userService;

    @Operation(
        summary = "Убрать отслеживание ссылки",
        responses = {
            @ApiResponse(responseCode = "200", description = "Ссылка успешно убрана", content = {
                @Content(mediaType = "application/json", schema = @Schema(implementation = LinkResponse.class))
            }),
            @ApiResponse(responseCode = "400", description = "Некорректные параметры запроса", content = {
                @Content(mediaType = "application/json", schema = @Schema(implementation = ApiErrorResponse.class))
            }),
            @ApiResponse(responseCode = "404", description = "Ссылка не найдена", content = {
                @Content(mediaType = "application/json", schema = @Schema(implementation = ApiErrorResponse.class))
            }),
            @ApiResponse(responseCode = "429", description = "Превышен лимит запросов", content = {
                @Content(mediaType = "application/json", schema = @Schema(implementation = ApiErrorResponse.class))
            })
        }
    )
    @DeleteMapping("/links")
    public ResponseEntity<LinkResponse> deleteLink(
        @NotNull @RequestHeader(value = "Tg-Chat-Id") Long tgChatId,
        @Valid @RequestBody RemoveLinkRequest removeLinkRequest
    ) {
        linkService.removeUserLink(tgChatId, new Link(removeLinkRequest.link()));
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @Operation(
        summary = "Получить все отслеживаемые ссылки",
        responses = {
            @ApiResponse(responseCode = "200", description = "Ссылки успешно получены", content = {
                @Content(mediaType = "application/json", schema = @Schema(implementation = ListLinksResponse.class))
            }),
            @ApiResponse(responseCode = "400", description = "Некорректные параметры запроса", content = {
                @Content(mediaType = "application/json", schema = @Schema(implementation = ApiErrorResponse.class))
            }),
            @ApiResponse(responseCode = "429", description = "Превышен лимит запросов", content = {
                @Content(mediaType = "application/json", schema = @Schema(implementation = ApiErrorResponse.class))
            })
        }
    )
    @GetMapping("/links")
    public ResponseEntity<ListLinksResponse> getLinks(@NotNull @RequestHeader(value = "Tg-Chat-Id") Long tgChatId) {
        List<Link> links =  linkService.getAllUserLinks(tgChatId);
        List<LinkResponse> linksResponses = links.stream().map(
            link -> {
                try {
                    return new LinkResponse(link.getId(), new URI(link.getUrl()));
                } catch (URISyntaxException e) {
                    throw new RuntimeException(e);
                }
            }
        ).toList();
        return new ResponseEntity<>(
            new ListLinksResponse(linksResponses, linksResponses.size()),
            HttpStatus.OK
        );
    }

    @Operation(
        operationId = "linksPost",
        summary = "Добавить отслеживание ссылки",
        responses = {
            @ApiResponse(responseCode = "200", description = "Ссылка успешно добавлена", content = {
                @Content(mediaType = "application/json", schema = @Schema(implementation = LinkResponse.class))
            }),
            @ApiResponse(responseCode = "400", description = "Некорректные параметры запроса", content = {
                @Content(mediaType = "application/json", schema = @Schema(implementation = ApiErrorResponse.class))
            }),
            @ApiResponse(responseCode = "429", description = "Превышен лимит запросов", content = {
                @Content(mediaType = "application/json", schema = @Schema(implementation = ApiErrorResponse.class))
            })
        }
    )
    @PostMapping("/links")
    public ResponseEntity<LinkResponse> addLink(
        @NotNull @RequestHeader(value = "Tg-Chat-Id") Long tgChatId,
        @Valid @RequestBody AddLinkRequest addLinkRequest
    ) throws URISyntaxException {
        userService.addLinkForUser(tgChatId, new Link(addLinkRequest.link(), OffsetDateTime.now()));
        Link newLink = linkService.getLinkByUrl(addLinkRequest.link()).get();
        return new ResponseEntity<>(
            new LinkResponse(newLink.getId(), new URI(newLink.getUrl())),
            HttpStatus.OK
        );
    }

    @Operation(
        summary = "Удалить чат",
        responses = {
            @ApiResponse(responseCode = "200", description = "Чат успешно удалён"),
            @ApiResponse(responseCode = "400", description = "Некорректные параметры запроса", content = {
                @Content(mediaType = "application/json", schema = @Schema(implementation = ApiErrorResponse.class))
            }),
            @ApiResponse(responseCode = "404", description = "Чат не существует", content = {
                @Content(mediaType = "application/json", schema = @Schema(implementation = ApiErrorResponse.class))
            }),
            @ApiResponse(responseCode = "429", description = "Превышен лимит запросов", content = {
                @Content(mediaType = "application/json", schema = @Schema(implementation = ApiErrorResponse.class))
            })
        }
    )
    @DeleteMapping("/tg-chat/{id}")
    public ResponseEntity<Void> deleteChat(
        @PathVariable Long id
    ) {
        userService.unregisterUserChat(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }


    @Operation(
        summary = "Зарегистрировать чат",
        responses = {
            @ApiResponse(responseCode = "200", description = "Чат зарегистрирован"),
            @ApiResponse(responseCode = "400", description = "Некорректные параметры запроса", content = {
                @Content(mediaType = "application/json", schema = @Schema(implementation = ApiErrorResponse.class))
            }),
            @ApiResponse(responseCode = "429", description = "Превышен лимит запросов", content = {
                @Content(mediaType = "application/json", schema = @Schema(implementation = ApiErrorResponse.class))
            })
        }
    )
    @PostMapping("/tg-chat/{id}")
    public ResponseEntity<Void> addChat(
        @PathVariable Long id
    ) {
        userService.registerUserChat(new User(id));
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
