CREATE DATABASE db1;
CREATE DATABASE db2;
SHOW DATABASES;
USE DATABASE db1;

CREATE TABLE Student (sid INTEGER, major STRING, gpa DOUBLE);
CREATE TABLE Course (cid INTEGER, title STRING);

INSERT INTO Student VALUES( 1, EE, 3.4 );
INSERT INTO Student VALUES( 2, EE, 3.5 );
INSERT INTO Student VALUES( 3, CS, 3.9 );
INSERT INTO Student VALUES( 4, CS, 2.4 );
INSERT INTO Student VALUES( 5, EE, 3.4 );

INSERT Into Course Values(15, Chemistry);
INSERT Into Course Values(224, Art);
INSERT Into Course Values(244, Philosophy);
INSERT Into Course Values(133, DBSystems);
INSERT Into Course Values(51, CS);

SELECT * FROM Student, Course;
SELECT * FROM Student, Course WHERE major = "CS";
SELECT * FROM Student WHERE gpa < 3.5;
SELECT * FROM Student, Course WHERE gpa > 3.3 AND title = "CS";
SELECT sid, gpa, title FROM Student, Course WHERE gpa > 3.3 AND title = "CS";

EXIT;