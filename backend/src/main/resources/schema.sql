CREATE DATABASE IF NOT EXISTS Test;
SET time_zone='+8:00';  -- 不生效
SET global time_zone='+8:00';

 drop table if exists paper;
-- drop table if exists user_conference;
 drop table if exists user_session;
 drop table if exists session;
-- drop table if exists message;
-- drop table if exists chatroom;
-- drop table if exists note;
-- drop table if exists user;
-- drop table if exists conference;

CREATE TABLE IF NOT EXISTS chatroom(
    chatroom_id int primary key auto_increment,
    participant_num int,
    record_num int
) default charset=utf8;

CREATE TABLE IF NOT EXISTS user(
    user_id int primary key auto_increment,
	username varchar(20) unique,
	password varchar(80),
	user_type int,
	phone char(11),
	signature varchar(200),
	avatar varchar(100)
) default charset=utf8;

CREATE TABLE IF NOT EXISTS conference(
    conference_id int primary key auto_increment,
    name varchar(127),
    organization varchar(255),
    introduction TEXT,
    date date,
    chairs varchar(128),
    place varchar(128),
    start_time datetime,
    end_time datetime,
    tags varchar(128),
    visible int,
    establisher_id int,
    foreign key(establisher_id) references user(user_id) on delete cascade on update cascade,
    fulltext(name, organization, introduction) with parser ngram
) default charset=utf8mb4;

CREATE TABLE IF NOT EXISTS message(
    message_id int primary key auto_increment,
    details varchar(512),
    time timestamp,
    chatroom_id int,
    username varchar(128),
    foreign key(chatroom_id) references chatroom(chatroom_id) on delete cascade on update cascade,
    foreign key(username) references user(username) on delete cascade on update cascade
) default charset=utf8;

CREATE TABLE IF NOT EXISTS note(
    note_id int primary key auto_increment,
    title varchar(60),
    text varchar(8192),
    create_time datetime,
    update_time datetime,
    user_id int
) default charset=utf8mb4;

CREATE TABLE IF NOT EXISTS session(
    session_id int primary key auto_increment,
    name varchar(500),
    topic varchar(300),
    description text,
    start_time datetime,
    end_time datetime,
    reporters varchar(500),
    rating int,
    visible int,
    conference_visible int,
    conference_id int,
    establisher_id int,
    foreign key(conference_id) references conference(conference_id) on delete cascade on update cascade,
    foreign key(establisher_id) references user(user_id) on delete cascade on update cascade
) default charset=utf8;

CREATE TABLE IF NOT EXISTS paper(
    paper_id int primary key auto_increment,
    title varchar(30),
    authors varchar(300),
    abstr varchar(600),
    content varchar(200),
    visible int,
    session_visible int,
    conference_visible int,
    session_id int,
    establisher_id int,
    foreign key(session_id) references session(session_id) on delete cascade on update cascade,
    foreign key(establisher_id) references user(user_id) on delete cascade on update cascade
) default charset=utf8;

CREATE TABLE IF NOT EXISTS user_conference(
    user_id int,
    conference_id int,
    uctype enum('favors', 'dislikes', 'reminds'),
    foreign key(user_id) references user(user_id) on delete cascade on update cascade,
    foreign key(conference_id) references conference(conference_id) on delete cascade on update cascade,
    primary key(user_id, conference_id, uctype)
) default charset=utf8;

CREATE TABLE IF NOT EXISTS user_session(
    user_id int,
    session_id int,
    rating float(2,1),
    foreign key(user_id) references user(user_id) on delete cascade on update cascade,
    foreign key(session_id) references session(session_id) on delete cascade on update cascade,
    primary key(user_id, session_id)
) default charset=utf8;
