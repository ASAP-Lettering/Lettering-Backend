-- add is_main column to space table
alter table space
    add column is_main boolean not null default false;

-- set is_main to true for space with space_index = 0
update space
set is_main = true
where space_index = 0;

-- create trigger to update is_main column
create trigger update_space_is_main
    before update
    on space
    for each row
begin
    if new.space_index = 0 then
        set new.is_main = true;
    else
        set new.is_main = false;
    end if;
end;
