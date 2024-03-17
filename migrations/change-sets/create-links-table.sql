-- liquibase formatted sql
-- changeset constZhur:create-links-table
CREATE TABLE IF NOT EXISTS link
(
    id SERIAL PRIMARY KEY,
    url VARCHAR(255) UNIQUE NOT NULL,
    checked_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);
--rollback drop table link;
