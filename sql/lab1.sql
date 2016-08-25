
/* Turn foreign key constraints on */
PRAGMA foreign_keys = true;

/* Delete the tables if they already exist */
drop table if exists Frequents;
drop table if exists Person;
drop view if exists HutFans;


/* Create the schema for tables */

/* Create two tables
   (1) a Person table with name (string), age (integer), 
       and gender (string).  Add an integrity constraint
       that name is the primary key for the table.
   (2) a Frequents table with name (string) and pizzeria 
      (string).  Attribute name should be a foreign key to
      the Person table.

      You might find this page useful: https://www.sqlite.org/foreignkeys.html
*/  

/* YOUR CODE HERE */

/* Create a view called HutFans that contains the names of
   people who frequent Pizza Hut.
*/

/* YOUR CODE HERE */

/* Populate the tables with data */
insert into Person values('Amy', 16, 'female');
insert into Person values('Ben', 21, 'male');
insert into Person values('Cal', 33, 'male');
insert into Person values('Dan', 13, 'male');
insert into Person values('Amy', 45, 'male');
insert into Person values('Fay', 21, 'female');
insert into Person values('Gus', 24, 'male');
insert into Person values('Hil', 30, 'female');
insert into Person values('Ian', 18, 'male');

insert into Frequents values('Amy', 'Pizza Hut');
insert into Frequents values('Ben', 'Pizza Hut');
insert into Frequents values('Ben', 'Chicago Pizza');
insert into Frequents values('Cal', 'Straw Hat');
insert into Frequents values('Cal', 'New York Pizza');
insert into Frequents values('Dan', 'Straw Hat');
insert into Frequents values('Dan', 'New York Pizza');
insert into Frequents values('Eli', 'Straw Hat');
insert into Frequents values('Eli', 'Chicago Pizza');
insert into Frequents values('Fay', 'Dominos');
insert into Frequents values('Fay', 'Little Caesars');
insert into Frequents values('Gus', 'Chicago Pizza');
insert into Frequents values('Gus', 'Pizza Hut');
insert into Frequents values('Hil', 'Dominos');
insert into Frequents values('Hil', 'Straw Hat');
insert into Frequents values('Hil', 'Pizza Hut');
insert into Frequents values('Ian', 'New York Pizza');
insert into Frequents values('Ian', 'Straw Hat');
insert into Frequents values('Ian', 'Dominos');