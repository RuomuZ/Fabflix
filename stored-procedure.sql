Use moviedb;

create table helper(
id int,
max_movie_id varchar(45) not null,
max_star_id varchar(45) not null,
primary key (id)
);

INSERT INTO helper VALUES(1,(select max(id) 
from movies),(select max(id) 
from stars));


DELIMITER $$
CREATE PROCEDURE insert_star (in star varchar(100),in dob int)
BEGIN
DECLARE max_id varchar(10);
DECLARE new_id varchar(10);
select max_star_id
into max_id
from helper
where id = 1;
Select concat(left(max_id,length(max_id)-1),char(ASCII(right(max_id,1))+1)) into new_id;
update helper set max_star_id = new_id where id = 1;
if (dob is not null) then
INSERT INTO stars (id, name, birthYear) VALUES(new_id,star,dob);
else
INSERT INTO stars (id, name) VALUES(new_id,star);
end if;
Select new_id;
END
$$
DELIMITER ;

DELIMITER $$
CREATE PROCEDURE add_movie (in mTitle varchar(100),in mYear int,in mDirector varchar(100),
in star varchar(100),in dob int, in genre varchar(32))
BEGIN
DECLARE max_id varchar(45);
DECLARE new_id varchar(45);
DECLARE star_ID varchar(10);
DECLARE genre_ID int;
DECLARE movie_msg varchar(50);
DECLARE star_msg varchar(50);
DECLARE genre_msg varchar(50);
if ((select count(*)
from movies
where mTitle = title and mYear = year and mDirector = director
) = 0) then
select max_movie_id
into max_id
from helper
where id = 1;
Select concat(left(max_id,length(max_id)-1),char(ASCII(right(max_id,1))+1)) into new_id;
INSERT INTO movies VALUES(new_id,mTitle,mYear,mDirector);
INSERT INTO ratings VALUES(new_id,0,0);
update helper set max_movie_id = new_id where id = 1;
select concat("newly generated movie id: ", new_id, ";") into movie_msg;
select id
into star_ID
from stars
where star = name
limit 1;
if (star_ID is null)
then
call insert_star(star,dob);
select max_star_id into star_ID from helper where id = 1;
select "new" into star_msg;
select concat("new star id:", star_ID, ";") into star_msg;
else
select concat("existing star id:", star_ID, ";") into star_msg;
end if;
select id
into genre_ID
from genres
where genre = name
limit 1;
if (genre_ID is null) then
INSERT INTO genres(name) VALUES(genre);
select id into genre_ID from genres where name = genre;
select concat("new genre id:", genre_ID, ";") into genre_msg;
else
select concat("existing genre id:", genre_ID, ";") into genre_msg;
end if;
INSERT INTO stars_in_movies VALUES(star_ID,new_id);
INSERT INTO genres_in_movies VALUES(genre_ID,new_id);
select concat(movie_msg, star_msg, genre_msg) as message;
else
select "duplicate movie!" as message;
end if;
END
$$

DELIMITER ;
