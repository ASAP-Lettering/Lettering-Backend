CREATE TABLE receive_letter
(
    id            VARCHAR(255) NOT NULL,
    created_at    datetime     NULL,
    updated_at    datetime     NULL,
    content       TEXT         NOT NULL,
    images        JSON         NOT NULL,
    template_type INT          NOT NULL,
    sender_id     VARCHAR(255) NULL,
    sender_name   VARCHAR(255) NULL,
    receiver_id   VARCHAR(255) NOT NULL,
    receive_date  date         NULL,
    space_id      VARCHAR(255) NULL,
    entity_status SMALLINT     NULL,
    CONSTRAINT pk_receive_letter PRIMARY KEY (id)
);



ALTER TABLE receive_letter
    ADD CONSTRAINT FK_RECEIVE_LETTER_ON_RECEIVER FOREIGN KEY (receiver_id) REFERENCES user (id);

ALTER TABLE receive_letter
    ADD CONSTRAINT FK_RECEIVE_LETTER_ON_SENDER FOREIGN KEY (sender_id) REFERENCES user (id);

ALTER TABLE receive_letter
    ADD CONSTRAINT FK_RECEIVE_LETTER_ON_SPACE FOREIGN KEY (space_id) REFERENCES space (id);


CREATE TABLE send_letter
(
    id            VARCHAR(255) NOT NULL,
    created_at    datetime     NULL,
    updated_at    datetime     NULL,
    content       TEXT         NOT NULL,
    images        JSON         NOT NULL,
    template_type INT          NOT NULL,
    sender_id     VARCHAR(255) NOT NULL,
    receiver_id   VARCHAR(255) NULL,
    receiver_name VARCHAR(255) NULL,
    letter_code   VARCHAR(255) NULL,
    letter_status VARCHAR(20)  NOT NULL,
    entity_status VARCHAR(20)  NOT NULL,
    CONSTRAINT pk_send_letter PRIMARY KEY (id)
);

CREATE UNIQUE INDEX idx_letter_code ON send_letter (letter_code);

ALTER TABLE send_letter
    ADD CONSTRAINT FK_SEND_LETTER_ON_RECEIVER FOREIGN KEY (receiver_id) REFERENCES user (id);

ALTER TABLE send_letter
    ADD CONSTRAINT FK_SEND_LETTER_ON_SENDER FOREIGN KEY (sender_id) REFERENCES user (id);