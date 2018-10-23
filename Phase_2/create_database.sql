CREATE DATABASE shopx CHARACTER SET utf8 COLLATE utf8_bin;
CREATE USER 'admin'@'localhost' IDENTIFIED BY 'admin' PASSWORD EXPIRE NEVER;
GRANT ALL PRIVILEGES ON shopx.* TO 'admin'@'localhost';
flush privileges;