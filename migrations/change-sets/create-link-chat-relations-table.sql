-- liquibase formatted sql
-- changeset constZhur:create-link-chat-relations-table
CREATE TABLE IF NOT EXISTS link_chat_relations
(
    chat_id BIGINT NOT NULL,
    link_id BIGINT NOT NULL,
    FOREIGN KEY (chat_id) REFERENCES user_chat(id) ON DELETE CASCADE,
    FOREIGN KEY (link_id) REFERENCES link(id) ON DELETE CASCADE,
    PRIMARY KEY (chat_id, link_id)
);
--rollback drop table link_chat_relations;
