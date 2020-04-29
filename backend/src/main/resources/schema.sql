CREATE DATABASE IF NOT EXISTS Test;

-- drop table if exists paper;
-- drop table if exists user_conference;
-- drop table if exists user_session;
-- drop table if exists session;
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

CREATE TABLE IF NOT EXISTS conference(
    conference_id int primary key auto_increment,
    organization varchar(100),
    introduction varchar(100),
    date date,
    chairs varchar(128),
    place varchar(60),
    start_time datetime,
    end_time datetime,
    tags varchar(128),
    visible int
) default charset=utf8;

CREATE TABLE IF NOT EXISTS message(
    message_id int primary key auto_increment,
    details varchar(512),
    time timestamp,
    chatroom_id int,
    foreign key(chatroom_id) references chatroom(chatroom_id) on delete cascade on update cascade
) default charset=utf8;

CREATE TABLE IF NOT EXISTS note(
    note_id int primary key auto_increment,
    title varchar(60),
    text varchar(8192),
    create_time datetime,
    update_time datetime,
    user_id int
) default charset=utf8;

CREATE TABLE IF NOT EXISTS session(
    session_id int primary key auto_increment,
    topic varchar(100),
    description varchar(500),
    start_time datetime,
    end_time datetime,
    reporters varchar(300),
    rating int,
    conference_id int,
    foreign key(conference_id) references conference(conference_id) on delete cascade on update cascade
) default charset=utf8;

CREATE TABLE IF NOT EXISTS paper(
    paper_id int primary key auto_increment,
    title varchar(30),
    authors varchar(300),
    abstr varchar(600),
    content varchar(100),
    session_id int,
    foreign key(session_id) references session(session_id) on delete cascade on update cascade
) default charset=utf8;

CREATE TABLE IF NOT EXISTS user(
    user_id int primary key auto_increment,
	username varchar(20),
	password varchar(80),
	user_type int,
	phone char(11),
	signature varchar(200),
	avatar varchar(100)
) default charset=utf8;

CREATE TABLE IF NOT EXISTS user_conference(
    user_id int,
    conference_id int,
    uctype enum('favor', 'dislike', 'remind', 'establish'),
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