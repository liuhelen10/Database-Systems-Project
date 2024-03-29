CREATE DATABASE db1;
USE DATABASE db1;

CREATE TABLE Student (sid INTEGER, sname STRING, gpa DOUBLE);
CREATE TABLE Professor (pid INTEGER, pname STRING);
CREATE TABLE Takes (tasid INTEGER, tacid INTEGER);

INSERT INTO Student VALUES(0,"Jeff", 3.82);
INSERT INTO Student VALUES(3,"Phillip", 2.11);
INSERT INTO Student VALUES(22,"Aneurin", 3.53);
INSERT INTO Student VALUES(23,"Nellie", 3.7);
INSERT INTO Student VALUES(24,"Mischa", 3.49);
INSERT INTO Student VALUES(25,"Chase", 3.67);
INSERT INTO Student VALUES(26,"Igor", 4.16);
INSERT INTO Student VALUES(27,"Malin", 1.27);
INSERT INTO Student VALUES(28,"Frayne", 4.18);
INSERT INTO Student VALUES(29,"Joe", 2.42);

INSERT INTO Takes VALUES(3,184);
INSERT INTO Takes VALUES(0, 13);
INSERT INTO Takes VALUES(0,21);
INSERT INTO Takes VALUES(171,40);
INSERT INTO Takes VALUES(3, 12);
INSERT INTO Takes VALUES(208,63);
INSERT INTO Takes VALUES(623,87);
INSERT INTO Takes VALUES(681,66);
INSERT INTO Takes VALUES(284,14);
INSERT INTO Takes VALUES(809,138);
INSERT INTO Takes VALUES(519,109);
INSERT INTO Takes VALUES(409,13);
INSERT INTO Takes VALUES(26,179);
INSERT INTO Takes VALUES(29,104);
INSERT INTO Takes VALUES(833,26);
INSERT INTO Takes VALUES(60,9);
INSERT INTO Takes VALUES(328,189);
INSERT INTO Takes VALUES(776,185);
INSERT INTO Takes VALUES(29,69);
INSERT INTO Takes VALUES(29,87);
INSERT INTO Takes VALUES(425,94);
INSERT INTO Takes VALUES(614,168);
INSERT INTO Takes VALUES(686,158);

INSERT INTO Professor VALUES(19,"Dimitri");
INSERT INTO Professor VALUES(20,"Jeff");
INSERT INTO Professor VALUES(21,"Jeff");
INSERT INTO Professor VALUES(22,"Lamech");
INSERT INTO Professor VALUES(23,"Jai");
INSERT INTO Professor VALUES(24,"Igor");
INSERT INTO Professor VALUES(25,"Delmar");
INSERT INTO Professor VALUES(26,"Mischa");

SELECT * FROM Student, Takes WHERE sid > tasid;
SELECT * FROM Student, Professor WHERE sname = pname;

exit;