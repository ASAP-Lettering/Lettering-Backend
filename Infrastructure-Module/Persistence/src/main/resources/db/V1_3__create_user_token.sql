CREATE TABLE user_token
(
    id         VARCHAR(255) NOT NULL,
    created_at datetime     NULL,
    updated_at datetime     NULL,
    token      VARCHAR(500) NOT NULL,
    CONSTRAINT pk_user_token PRIMARY KEY (id)
);

ALTER TABLE user_token
    ADD CONSTRAINT uc_user_token_token UNIQUE (token);

CREATE UNIQUE INDEX uk_token_index ON user_token (token);