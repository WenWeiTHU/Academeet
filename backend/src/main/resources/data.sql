--insert into user (username, password, user_type)
--select 'user1', '$2a$10$ewnEXgHkgcVX.kTLa4sXq.xJN4wMcC4hF2MmcowQ.z0H2pbwv6RYK', 0 from dual
--where not exists (select * from user where username = 'user1');
--
--insert into user (username, password, user_type)
--select 'user2', '$2a$10$ewnEXgHkgcVX.kTLa4sXq.xJN4wMcC4hF2MmcowQ.z0H2pbwv6RYK', 0 from dual
--where not exists (select * from user where username = 'user2');
--
--insert into user (username, password, user_type)
--select 'user3', '$2a$10$ewnEXgHkgcVX.kTLa4sXq.xJN4wMcC4hF2MmcowQ.z0H2pbwv6RYK', 0 from dual
--where not exists (select * from user where username = 'user3');
--
--insert into user (username, password, user_type)
--select 'admin1', '$2a$10$ewnEXgHkgcVX.kTLa4sXq.xJN4wMcC4hF2MmcowQ.z0H2pbwv6RYK', 0 from dual
--where not exists (select * from user where username = 'admin1');
--
--insert into user (username, password, user_type)
--select 'admin2', '$2a$10$ewnEXgHkgcVX.kTLa4sXq.xJN4wMcC4hF2MmcowQ.z0H2pbwv6RYK', 0 from dual
--where not exists (select * from user where username = 'admin2');

--delete from user_conference;

insert into conference (organization, introduction, date, chairs, place, start_time, end_time, tags, visible)
select 'organization1', 'introduction1', '2020-05-01', '"chair1-1", "chair1-2"', 'Apartment1',
'2020-05-01 08:00:00', '2020-05-01 10:00:00', '"algo", "part", "visual"', 0  from dual
where not exists (select * from conference where
(organization, introduction) = ('organization1', 'introduction1'));

insert into conference (organization, introduction, date, chairs, place, start_time, end_time, tags, visible)
select 'organization2', 'introduction2', '2020-05-02', '"chair2-1", "chair2-2"', 'Apartment2',
'2020-05-02 08:00:00', '2020-05-02 10:00:00', '"algo2", "part2", "visual2"', 0 from dual
where not exists (select * from conference where
(organization, introduction) = ('organization2', 'introduction2'));

insert into conference (organization, introduction, date, chairs, place, start_time, end_time, tags, visible)
select 'organization3', 'introduction3', '2020-05-03', '"chair3-1", "chair3-2"', 'Apartment3',
'2020-05-03 08:00:00', '2020-05-03 10:00:00', '"algo2", "part3"', 0 from dual
where not exists (select * from conference where
(organization, introduction) = ('organization3', 'introduction3'));

insert into user_conference (user_id, conference_id)
select user_id, conference_id from user, conference
where user.username='admin1' and conference.organization='organization1'
and not exists (select * from user_conference natural join conference where organization = 'organization1');

insert into user_conference (user_id, conference_id)
select user_id, conference_id from user, conference
where user.username='admin1' and conference.organization='organization2'
and not exists (select * from user_conference natural join conference where organization = 'organization2');

insert into user_conference (user_id, conference_id)
select user_id, conference_id from user, conference
where user.username='admin2' and conference.organization='organization3'
and not exists (select * from user_conference natural join conference where organization = 'organization3');

insert into session (topic, description, start_time, end_time, reporters, rating, conference_id)
select 'topic1-1', 'description1-1', '2020-05-01 08:10:00', '2020-05-01 08:30:00',
'"reportor1", "reportor2"', 9, conference_id from conference where organization='organization1'
and not exists (select * from session where topic = 'topic1-1');

insert into session (topic, description, start_time, end_time, reporters, rating, conference_id)
select 'topic1-2', 'description1-2', '2020-05-01 08:40:00', '2020-05-01 09:00:00',
'"reportor1", "reportor2"', 10, conference_id from conference where organization='organization1'
and not exists (select * from session where topic = 'topic1-2');

insert into session (topic, description, start_time, end_time, reporters, rating, conference_id)
select 'topic2-1', 'description2-1', '2020-05-02 08:40:00', '2020-05-01 09:00:00',
'"reportor1", "reportor2"', 10, conference_id from conference where organization='organization2'
and not exists (select * from session where topic = 'topic2-1');

insert into paper (title, authors, abstr, content, session_id)
select 'title1-1', '"author1", "author2"', 'abstr1', 'content1', session_id
from session where topic='topic1-1' and not exists (select * from paper where title = 'title1-1');

insert into paper (title, authors, abstr, content, session_id)
select 'title2-1', '"author1", "author2"', 'abstr2', 'content2', session_id
from session where topic='topic1-2' and not exists (select * from paper where title = 'title2-1');

insert into paper (title, authors, abstr, content, session_id)
select 'title3-1', '"author1", "author2"', 'abstr3', 'content3', session_id
from session where topic='topic2-1' and not exists (select * from paper where title = 'title3-1');

