CREATE TABLE letter_logs
(
    id               VARCHAR(255) NOT NULL,
    created_at       datetime     NULL,
    updated_at       datetime     NULL,
    target_letter_id VARCHAR(255) NOT NULL,
    logged_at        datetime     NULL,
    log_type         VARCHAR(20)  NOT NULL,
    content          VARCHAR(500) NOT NULL,
    CONSTRAINT pk_letter_logs PRIMARY KEY (id)
);

ALTER TABLE letter_logs
    ADD CONSTRAINT FK_LETTER_LOGS_ON_TARGET_LETTER FOREIGN KEY (target_letter_id) REFERENCES send_letter (id);