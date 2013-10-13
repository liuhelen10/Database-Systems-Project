CREATE DATABASE db1;
CREATE DATABASE db2;
SHOW DATABASES;
USE DATABASE db1;

CREATE TABLE Number (number INTEGER);
CREATE TABLE Student (id INTEGER, major STRING, gpa DOUBLE);
CREATE TABLE Course (id INTEGER, title STRING);

INSERT INTO Student VALUES( 1, EE, 3.4 );
INSERT INTO Student VALUES( 2, EE, 3.5 );
INSERT INTO Student VALUES( 3, CS, 3.9 );
INSERT INTO Student VALUES( 4, CS, 2.4 );
INSERT INTO Student VALUES( 5, EE, 3.4 );

INSERT Into Number Values(234);
INSERT Into Number Values(224);
INSERT Into Number Values(244);

INSERT Into Course Values(15, Chemistry);
INSERT Into Course Values(224, Art);
INSERT Into Course Values(244, Philosophy);
INSERT Into Course Values(133, DBSystems);

SELECT * FROM Student;
SELECT * FROM Student WHERE major = CS;
SELECT major FROM Student WHERE id = 2341;
SELECT id, title FROM Course;
SELECT * FROM Course WHERE title = CS133;
SELECT id, title FROM Course WHERE id = 15;

UPDATE Student SET gpa = 4.3 WHERE id = 1;
UPDATE Student SET major = CS WHERE id = 2;
SELECT * FROM Student;

EXIT;