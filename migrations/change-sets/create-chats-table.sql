-- liquibase formatted sql
-- changeset constZhur:create-user-chats-table
CREATE TABLE IF NOT EXISTS user_chat
(
    id BIGINT PRIMARY KEY,
    name VARCHAR(255) NOT NULL
);
--rollback drop table user_chat;
