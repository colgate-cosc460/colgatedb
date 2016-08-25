# Lab 1

This lab assignment is for Tuesday, August 30th.  The SQL Exercises are due by the end of lab.  The rest is due **Sunday Sept. 4th by 11:59PM.** Submission instructions are at the end of the lab.


## Short SQL Exercise

**Please complete this during the 2 hour lab.  When you finish, show me your work so I can give you credit for completing it.**

We will be using SQLite for this exercise.  SQLite is lightweight database management system that is extremely widely used because of its simplicity.  There is plenty of [documentation on it here](https://www.sqlite.org/index.html) and [other](http://www.sqlitetutorial.net/) [tutorials](http://www.tutorialspoint.com/sqlite/index.htm) that are decent.

1. Open Terminal.  Change the current directory to be the `sql` subdirectory of `cosc460repo`.  If you do `ls` you should see a file called `lab1.sql`.  
	
		$ cd <path to your cosc460repo>/sql
		$ ls
		lab1.sql

2. This file contains a series of SQL statements.  We will create and populate a new database by executing this file with SQLite.  To create a database, you simply specify an OS file where you want the data stored.  We'll call it `pizza.db`.

		$ sqlite3 pizza.db 
		SQLite version 3.8.5 2014-08-15 22:37:57
		Enter ".help" for usage hints.
		sqlite>

3. Execute the commands written in `lab1.sql`.  You should see many errors.

		sqlite> .read lab1.sql
		Error: near line 27: no such table: Person
		...

3. Now open `lab1.sql` in a text editor (or you can probably Eclipse or Intellij too).  You will see some documentation indicating a series of sql statements you are supposed to write.  Start by defining the two tables.  If you add definitions and re-run, you should see only 3 errors as shown below.  (If you don't see errors, you didn't define the tables according to the specification.)

		sqlite> .read lab1.sql
		Error: near line 31: UNIQUE constraint failed: Person.name
		Error: near line 44: FOREIGN KEY constraint failed
		Error: near line 45: FOREIGN KEY constraint failed

4. To make these errors go away, modify the value of the data being inserted.  (Hint: You only need to make a single modification to one data value and all three errors will disappear.)

5. Create a view called `HutFans` that has the names of people who frequent "Pizza Hut."  Then query that view and you should see something like this:

		sqlite> .read lab1.sql

		sqlite> select * from HutFans;
		Amy
		Ben
		Gus
		Hil

6. Your book talks about modifying views and the restrictions that database systems place on it.  Think about what it would mean to delete a tuple from this view.   Is SQLite justified in disallowing it?


## ColgateDB Lab 1 Instructions

Like most relational databases, ColgateDB stores data on disk.  To manipulate the data, we must bring it into memory, translating bytes into Java objects.  Those Java objects in turn must be capable of being translated back into bytes.  We will get into this translation in great detail in a coming lab.

For this lab, we focus on the Java representation of data.  Database concepts such as schema, tuples, and fields are represented as Java classes.

A `Tuple` in ColgateDB is quite basic.  It consists of a collection of
`Field` objects, one per field in the `Tuple`.  `Field` is an interface that different data types (e.g.,integer, string) implement.  Tuples also have a type (or schema), called a *tuple descriptor*, represented by a `TupleDesc` object.  This object consists of a collection of `TDItem` objects, one per field
in the tuple.  Each `TDItem` specifies the field `Type` and the name of the `Field`.

It is possible to create a `Tuple` object by simply calling the constructor and setting each field using the `setField` method.  In a later lab, you will parse data on disk to create tuples, using methods like the `parse` method on `Type` objects.  You will also write out data using the `serialize` method on `Field` objects.

**Task 1**  Implement the methods in `TupleDesc`.  You'll notice that each method currently throws an `UnSupportedOperationException`.  You can replace this line with your code.  After you implement the methods in `TupleDesc` you should be able to pass the unit tests in `TupleDescTest`.

**Task 2**  Implement the methods in `Tuple`.  After you implement the methods in `Tuple` you should be able to pass the unit tests in `TupleTest`.

**Task 3**  The code that you will write in this course will make heavy use of the object-oriented language features of Java.  While I hope that many of the concepts it uses were covered in 102, I realize that (a) 102 was a "long time ago" for many of you and (b) perhaps some of the concepts were not covered in detail.  Therefore, in these early labs I will ask you questions to refresh or add to your Java knowledge.  Task 3 is to answer the questions in the file titled `lab1questions.txt`.


## Milestone

If the submitted code (see instructions above) passes the `TupleDescTest` and `TupleTest` then you have completed the milestone for this lab.  

## Submission instructions

To submit your work, you must *commit* your changes and then *push* those changes to GitHub. 

1. Open Terminal and change to the `cosc460repo` directory where you code lives.

2. Check the status to see what changes have been made.

		$ git status
		On branch master
		Your branch is up-to-date with 'origin/master'.
		Changes not staged for commit:
		  (use "git add <file>..." to update what will be committed)
		  (use "git checkout -- <file>..." to discard changes in working directory)

			modified:   src/colgatedb/tuple/Tuple.java
			modified:   src/colgatedb/tuple/TupleDesc.java

		no changes added to commit (use "git add" and/or "git commit -a") 

3. Use `git add` to move these changes to the "staging area."  By using the `-u` flag you will only add files that already exist in the repository.  This is probably what you want to do in general.  Please **never** do `git add -A` because it will add all sorts of junk files (such as hidden files left by applications like Eclipse and IntelliJ IDEA).

		$ git add -u
		add 'src/colgatedb/tuple/Tuple.java'
		add 'src/colgatedb/tuple/TupleDesc.java'	

4. *Commit* the changes to your local repository.  Please write a commit message having the following form (where you fill in the blank).

		$ git commit -m 'this lab was _____'
		[master cf5963d] this lab was _____
		 2 files changed, 2 insertions(+)

5. *Push* these changes to GitHub.

		$ git push origin master
		Counting objects: 7, done.
		Delta compression using up to 4 threads.
		Compressing objects: 100% (6/6), done.
		Writing objects: 100% (7/7), 555 bytes | 0 bytes/s, done.
		Total 7 (delta 4), reused 0 (delta 0)
		remote: Resolving deltas: 100% (4/4), completed with 4 local objects.
		To https://github.com/colgate-cosc460/colgatedb-cosc460student.git
		   6f6ef87..cf5963d  master -> master

	(A note for power users: if you tire of typing your password every time you push to github, there are ways of [avoiding that](https://help.github.com/articles/caching-your-github-password-in-git).)

6. **Check have successfully submitted your work**  Use a web browser and check the repo online at GitHub.  If your latest commit is there, then your work is considered submitted.

