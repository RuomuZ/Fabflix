Use moviedb;

create table helper_star(
id int,
head varchar(45) not null,
generator int,
primary key (id)
);

create table helper_movie(
id int,
head varchar(45) not null,
generator int,
primary key (id)
);

INSERT INTO helper_star VALUES(1,"abcd",1);
INSERT INTO helper_movie VALUES(1,"cdef",1);


DELIMITER $$
CREATE PROCEDURE insert_star (in star varchar(100),in dob int
,out new_star_id varchar(15))
BEGIN
DECLARE max_id varchar(10);
DECLARE new_id varchar(10);
DECLARE gene int;
START TRANSACTION;
select head
into max_id
from helper_star
where id = 1;
select generator
into gene
from helper_star
where id = 1;
Select concat(max_id,gene+1) into new_id;
update helper_star set generator = (gene + 1) where id = 1;
if (dob is not null) then
INSERT INTO stars (id, name, birthYear) VALUES(new_id,star,dob);
else
INSERT INTO stars (id, name) VALUES(new_id,star);
end if;
Select new_id into new_star_id;
commit;
rollback;
END
$$
DELIMITER ;



DELIMITER $$
CREATE PROCEDURE add_movie (in mTitle varchar(100),in mYear int,in mDirector varchar(100),
in star varchar(100),in genre varchar(32))
BEGIN
DECLARE movie_helper varchar(45);
DECLARE movie_gene varchar(45);
DECLARE movie_msg varchar(45);
DECLARE star_msg varchar(45);
DECLARE genre_msg varchar(45);
DECLARE new_movie_id varchar(45);
DECLARE new_star_id varchar(45);
DECLARE new_genre_id varchar(45);
start transaction;
if ((select count(*) from movies where title = mTitle) = 0)
then
select head into movie_helper from helper_movie;
select generator into movie_gene from helper_movie;
select concat(movie_helper,movie_gene + 1) into new_movie_id;
select concat("new movie id ", new_movie_id, "  ") into movie_msg;
INSERT INTO movies VALUES(new_movie_id,mTitle,mYear,mDirector);
update helper_movie set generator = (movie_gene + 1) where id = 1;
select id into new_star_id from stars where name = star;
if (new_star_id is null)
then
call insert_star(star,null,@new_star_id);
select @new_star_id into new_star_id;
select concat("new star id ", new_star_id) into star_msg;
else
select concat("existing star id ", new_star_id, "  ") into star_msg;
end if;
select id into new_genre_id from genres where name = genre;
if (new_genre_id is null)
then
select max(id) + 1 into new_genre_id from genres;
select concat("new genre id", new_genre_id) into genre_msg;
INSERT INTO genres VALUES(new_genre_id,genre);
else
select concat("existing genre id ", new_genre_id) into genre_msg;
end if;
INSERT INTO stars_in_movies VALUES(new_star_id,new_movie_id);
INSERT INTO genres_in_movies VALUES(new_genre_id,new_movie_id);
INSERT INTO ratings VALUES(new_movie_id, 0, 0);
select concat(movie_msg,star_msg, genre_msg) as message;
else
select "duplicate movie!" as message;
end if;
commit;
rollback;
END
$$
DELIMITER ;

