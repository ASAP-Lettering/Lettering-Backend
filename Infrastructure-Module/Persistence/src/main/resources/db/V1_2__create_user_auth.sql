CREATE TABLE user_auth
(
    id                    VARCHAR(255) NOT NULL,
    created_at            datetime     NULL,
    updated_at            datetime     NULL,
    user_id               VARCHAR(255) NOT NULL,
    social_id             VARCHAR(255) NOT NULL,
    social_login_provider VARCHAR(255) NOT NULL,
    CONSTRAINT pk_user_auth PRIMARY KEY (id)
);

ALTER TABLE user_auth
    ADD CONSTRAINT uc_user_auth_user UNIQUE (user_id);

CREATE INDEX idx_social_id_index ON user_auth (social_id);

ALTER TABLE user_auth
    ADD CONSTRAINT FK_USER_AUTH_ON_USER FOREIGN KEY (user_id) REFERENCES user (id);