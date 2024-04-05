package edu.java.repository.jooq;

import edu.java.model.User;
import edu.java.repository.UserRepository;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import static edu.java.repository.jooq.generated.Tables.LINK_CHAT_RELATIONS;
import static edu.java.repository.jooq.generated.Tables.USER_CHAT;

@RequiredArgsConstructor
public class JooqUserRepository implements UserRepository {
    private final DSLContext dslContext;

    @Override
    public void add(User userChat) {
        dslContext.insertInto(USER_CHAT)
            .set(USER_CHAT.ID, userChat.getId())
            .set(USER_CHAT.NAME, userChat.getName())
            .execute();
    }

    @Override
    public void addLinkForUser(Long userId, Integer linkId) {
        dslContext.insertInto(LINK_CHAT_RELATIONS)
            .set(LINK_CHAT_RELATIONS.CHAT_ID, userId)
            .set(LINK_CHAT_RELATIONS.LINK_ID, linkId)
            .execute();
    }

    @Override
    public Optional<User> findById(Long id) {
        return dslContext.selectFrom(USER_CHAT)
            .where(USER_CHAT.ID.eq(id))
            .fetchOptionalInto(User.class);
    }

    @Override
    public void remove(Long id) {
        dslContext.deleteFrom(USER_CHAT)
            .where(USER_CHAT.ID.eq(id))
            .execute();
    }

    @Override
    public List<User> findAll() {
        return dslContext.selectFrom(USER_CHAT)
            .fetchInto(User.class);
    }

    @Override
    public List<Long> getAllUserChatIdsByLinkId(Integer linkId) {
        return dslContext.select(LINK_CHAT_RELATIONS.CHAT_ID)
            .from(LINK_CHAT_RELATIONS)
            .where(LINK_CHAT_RELATIONS.LINK_ID.eq(linkId))
            .fetchInto(Long.class);
    }
}
