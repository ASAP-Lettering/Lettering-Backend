CREATE TABLE user
(
    id                 VARCHAR(255) NOT NULL,
    created_at         datetime     NULL,
    updated_at         datetime     NULL,
    username           VARCHAR(255) NOT NULL,
    profile_image      VARCHAR(255) NOT NULL,
    user_permission_id VARCHAR(255) NULL,
    birthday           date         NULL,
    CONSTRAINT pk_user PRIMARY KEY (id)
);

CREATE TABLE user_permission
(
    id                   VARCHAR(255) NOT NULL,
    created_at           datetime     NULL,
    updated_at           datetime     NULL,
    service_permission   BIT(1)       NOT NULL,
    private_permission   BIT(1)       NOT NULL,
    marketing_permission BIT(1)       NOT NULL,
    CONSTRAINT pk_user_permission PRIMARY KEY (id)
);

ALTER TABLE user
    ADD CONSTRAINT uc_user_user_permission UNIQUE (user_permission_id);

ALTER TABLE user
    ADD CONSTRAINT FK_USER_ON_USER_PERMISSION FOREIGN KEY (user_permission_id) REFERENCES user_permission (id);