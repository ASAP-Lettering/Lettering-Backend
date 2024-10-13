alter table user_token add column user_id varchar(255);

ALTER TABLE user_token
    ADD CONSTRAINT FK_USER_TOKEN_ON_USER FOREIGN KEY (user_id) REFERENCES user (id);