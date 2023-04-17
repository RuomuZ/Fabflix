CREATE DATABASE moviedb;
use moviedb

create table movies(
id varchar(45) default '',
title varchar(100) default '',
year int not null,
director varchar (100) default '',
primary key (id)
);


create table stars(
id varchar(10) default '',
name varchar(100) default '',
birthYear int null,
primary key (id)
);

create table stars_in_movies(
starId varchar(10) default '',
movieId varchar(10) default '', 
foreign key (starId) references stars(id),
foreign key (movieId) references movies(id)
);


create table genres(
id int not null auto_increment,
name varchar(32) default '',
primary key(id)
);

create table genres_in_movies(
genreId int not null,
movieId varchar(10) default '',
foreign key (genreId) references genres(id),
foreign key (movieId) references movies(id)
);


create table customers(
id int not null auto_increment,
firstName varchar(50) default '',
lastName varchar(50) default '',
ccId varchar(20) default '',
address varchar(200) default '',
email varchar(50) default '',
password varchar(20) default '',
primary key (id),
foreign key (ccId) references creditcards(id)
);


create table sales(
id int not null auto_increment,
customerId int not null,
movieId varchar(10) default '',
saleDate date not null,
primary key (id),
foreign key (customerId) references customers(id),
foreign key (movieId) references movies(id)
);


create table ratings(
movieId varchar(10) default '',
rating float not null,
numVotes int not null,
foreign key (movieId) references movies(id)
);

