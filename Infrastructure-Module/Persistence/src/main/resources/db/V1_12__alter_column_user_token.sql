alter table user_token drop constraint uk_token_index;
alter table user_token drop constraint uc_user_token_token;


alter table user_token modify column token text not null;

create unique index uk_token_index on user_token (token(756));