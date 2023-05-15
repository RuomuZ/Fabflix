create table employees (
email varchar(50) not null,
password varchar(20) not null,
fullname varchar(100) default '',
primary key (email)
);

INSERT INTO employees VALUES('classta@email.edu','classta','TA CS122B');
