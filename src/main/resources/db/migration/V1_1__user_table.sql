create table t_application_user (
    id uuid primary key,
    c_username varchar(255) not null ,
    c_password text
);

create unique index idx_application_username on t_application_user(c_username);

