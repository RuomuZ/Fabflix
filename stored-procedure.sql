Use moviedb;

DELIMITER $$
CREATE PROCEDURE insert_star (in star varchar(100),in dob int)
BEGIN
DECLARE max_id varchar(10);
DECLARE new_id varchar(10);
select max(id) 
into max_id
from stars;
Select concat(left(max_id,length(max_id)-1),char(ASCII(right(max_id,1))+1)) into new_id;
if (dob is not null) then
INSERT INTO stars (id, name, birthYear) VALUES(new_id,star,dob);
else
INSERT INTO stars (id, name) VALUES(new_id,star);
end if;
Select new_id;
END
$$
