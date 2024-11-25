select * from users;

SELECT * FROM users AS u WHERE u.country = 'Finland' ORDER BY u.name


CREATE TABLE users(
  id serial ,
  gender VARCHAR(255),
  name VARCHAR(255),
  surname VARCHAR(255),
  street VARCHAR(255),
  city VARCHAR(255),
  state VARCHAR(255),
  country VARCHAR(255),
  username VARCHAR(255),
  password VARCHAR(255),
  phone VARCHAR(255),
  age Integer,
  primary key(id)

);
