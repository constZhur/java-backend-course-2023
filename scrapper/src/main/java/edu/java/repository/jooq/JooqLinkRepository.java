package edu.java.repository.jooq;

import edu.java.model.Link;
import edu.java.repository.LinkRepository;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import org.jooq.Field;
import static edu.java.repository.jooq.generated.Tables.LINK;
import static edu.java.repository.jooq.generated.Tables.LINK_CHAT_RELATIONS;
import static org.jooq.impl.DSL.field;

@RequiredArgsConstructor
public class JooqLinkRepository implements LinkRepository {
    private final DSLContext dslContext;

    @Override
    public void add(Link link) {
        dslContext.insertInto(LINK)
            .set(LINK.ID, link.getId())
            .set(LINK.URL, link.getUrl())
            .execute();
    }

    @Override
    public Optional<Link> findById(Integer id) {
        return dslContext.selectFrom(LINK)
            .where(LINK.ID.eq(id))
            .fetchOptionalInto(Link.class);
    }

    @Override
    public Optional<Link> findByUrl(String url) {
        return dslContext.selectFrom(LINK)
            .where(LINK.URL.eq(url))
            .fetchOptionalInto(Link.class);
    }

    @Override
    public Optional<Link> findUserLinkByUrl(Long userId, String url) {
        return dslContext.select(LINK.fields())
            .from(LINK)
            .join(LINK_CHAT_RELATIONS)
            .on(LINK_CHAT_RELATIONS.LINK_ID.eq(LINK.ID))
            .where(LINK_CHAT_RELATIONS.CHAT_ID.eq(userId)
                .and(LINK.URL.eq(url)))
            .fetchOptionalInto(Link.class);
    }

    @Override
    public List<Link> findAll() {
        return dslContext.selectFrom(LINK)
            .fetchInto(Link.class);
    }

    @Override
    public List<Link> findAllUserLinks(Long userId) {
        return dslContext.select(LINK.fields())
            .from(LINK)
            .join(LINK_CHAT_RELATIONS)
            .on(LINK_CHAT_RELATIONS.LINK_ID.eq(LINK.ID))
            .where(LINK_CHAT_RELATIONS.CHAT_ID.eq(userId))
            .fetchInto(Link.class);
    }

    @Override
    public List<Link> findOutdatedLinks(Long linksLimit, Long timeInterval) {
        Field<Long> intervalLastCheck = field(
            "EXTRACT(EPOCH FROM (CURRENT_TIMESTAMP - checked_at))",
            Long.class
        );
        return dslContext
            .select(LINK.fields()).from(LINK)
            .where(intervalLastCheck.greaterOrEqual(timeInterval))
            .or(LINK.CHECKED_AT.isNull())
            .limit(linksLimit.intValue())
            .fetchInto(Link.class);
    }

    @Override
    public void remove(Integer id) {
        dslContext.deleteFrom(LINK)
            .where(LINK.ID.eq(id))
            .execute();
        dslContext.deleteFrom(LINK_CHAT_RELATIONS)
            .where(LINK_CHAT_RELATIONS.LINK_ID.eq(id))
            .execute();
    }

    @Override
    public void removeUserLink(Long userId, Link link) {
        dslContext.deleteFrom(LINK_CHAT_RELATIONS)
            .where(LINK_CHAT_RELATIONS.CHAT_ID.eq(userId)
                .and(LINK_CHAT_RELATIONS.LINK_ID.eq(link.getId())))
            .execute();
    }

    @Override
    public void updateCheckedTime(Link link, OffsetDateTime newUpdateTime) {
        OffsetDateTime updatedTime = newUpdateTime.withOffsetSameInstant(OffsetDateTime.now().getOffset());
        dslContext.update(LINK)
            .set(LINK.CHECKED_AT, updatedTime.toLocalDateTime())
            .where(LINK.ID.eq(link.getId()))
            .execute();
    }
}
