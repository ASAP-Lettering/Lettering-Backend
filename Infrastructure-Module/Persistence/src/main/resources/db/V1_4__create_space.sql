CREATE TABLE space
(
    id            VARCHAR(255) NOT NULL,
    created_at    datetime     NULL,
    updated_at    datetime     NULL,
    user_id       VARCHAR(255) NULL,
    name          VARCHAR(255) NULL,
    template_type INT          NOT NULL,
    `index`       INT          NOT NULL,
    space_status  VARCHAR(20)  NOT NULL,
    CONSTRAINT pk_space PRIMARY KEY (id)
);

ALTER TABLE space
    ADD CONSTRAINT FK_SPACE_ON_USER FOREIGN KEY (user_id) REFERENCES user (id);