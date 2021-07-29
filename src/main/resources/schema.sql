create table country
(
    country_id bigint auto_increment primary key,
    name       varchar(255) null
);

create table language
(
    language_id bigint auto_increment primary key,
    abbr        varchar(255) null,
    name        varchar(255) null
);

create table users
(
    user_id       bigint auto_increment primary key,
    content       varchar(2000) null,
    email         varchar(255)  not null,
    facebook_url  varchar(255)  null,
    image_url     varchar(1000) null,
    instagram_url varchar(255)  null,
    name          varchar(255)  not null,
    password      varchar(255)  not null,
    reg_date      DATETIME default CURRENT_TIMESTAMP,
    role          varchar(255)  not null,
    school        varchar(255)  not null,
    views         bigint        not null,
    web_email     varchar(255)  not null,
    unique (email)
);

create table follow
(
    follow_id bigint auto_increment primary key,
    reg_date  DATETIME DEFAULT CURRENT_TIMESTAMP,
    user_from bigint      null,
    user_to   bigint      null,
    foreign key (user_to) references users (user_id),
    foreign key (user_from) references users (user_id)
);

create table VIEW
(
    VIEW_ID   BIGINT auto_increment primary key,
    USER_FROM BIGINT,
    USER_TO   BIGINT,
    foreign key (USER_TO) references USERS (USER_ID),
    foreign key (USER_FROM) references USERS (USER_ID)
);



create table user_country
(
    user_country_id bigint auto_increment primary key,
    country_id      bigint null,
    user_id         bigint null,
    foreign key (country_id) references country (country_id),
    foreign key (user_id) references users (user_id)
);

create table user_language
(
    user_language_id bigint auto_increment primary key,
    language_id      bigint null,
    user_id          bigint null,
    foreign key (user_id) references users (user_id),
    foreign key (language_id) references language (language_id)
);

create table user_hope_language
(
    user_hope_language_id bigint auto_increment primary key,
    language_id           bigint null,
    user_id               bigint null,
    foreign key (user_id) references users (user_id),
    foreign key (language_id) references language (language_id)
);



