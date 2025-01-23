CREATE TABLE receive_draft_letters
(
    id          VARCHAR(255) NOT NULL,
    created_at  datetime     NULL,
    updated_at  datetime     NULL,
    content     VARCHAR(255) NULL,
    sender_name VARCHAR(255) NULL,
    owner_id    VARCHAR(255) NOT NULL,
    images      TEXT         NOT NULL,
    type        VARCHAR(20)  NOT NULL,
    CONSTRAINT pk_receive_draft_letters PRIMARY KEY (id)
);

ALTER TABLE receive_draft_letters
    ADD CONSTRAINT FK_RECEIVE_DRAFT_LETTERS_ON_OWNER FOREIGN KEY (owner_id) REFERENCES user (id);