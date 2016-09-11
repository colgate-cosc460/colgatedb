# Lab 3

This lab assignment is for lab on Tuesday, September 13th and is due **Sunday Sept. 18th by 11:59PM.** Submission instructions are at the end of the lab.

There are two parts to this lab: 

1. Continued development on ColgateDB
2. SQL exercises


## Obtaining the Code

Follow [the same instructions as lab 2](https://github.com/colgate-cosc460/colgatedb/blob/master/labs/lab2.md) except that I suggest you add the `--no-edit` flag on the `git pull` command, like so

	git pull --no-edit https://github.com/colgate-cosc460/colgatedb.git master


## ColgateDB Instructions

This lab is a direct continuation of last week's lab on `SlottedPage`.  This week, you finish off the implementation of a slotted page by implementing methods for reading a page from bytes and writing a page to bytes.  You will need to make a few modifications to `SlottedPage` but most of the coding will be in `SlottedPageFormatter`.

**Tip**: This lab requires manipulating invididual bits.  You are expected to know about binary representation and logic operators (and, or, not, etc.) from COSC 201.  But you may not know how to manipulate data stored in bytes to extract out individual bit values.  Here is the [official page from Oracle](https://docs.oracle.com/javase/tutorial/java/nutsandbolts/op3.html) describing the bitwise operators in Java.   There are also decent [tutorials online](http://javarevisited.blogspot.com/2013/03/bitwise-and-bitshift-operators-in-java-and-or-xor-left-right-shift-example-tutorial.html).  

**Note**: The book uses the term "slotted page organization" to refer specifically to the format used for *variable-length* records in Ch. 9.6.2, even though the page organization for *fixed-length* records also places tuples in slots.  To be clear, ColgateDB's `SlottedPage` is intended to hold records of *fixed-length*.

The ColgateDB slotted page format is most similar to the "Unpacked, bitmap" format in Figure 9.6 of the Cow book.  The main differences are that no space is used to store the number of slots and the header is stored in the first bytes of the page (in contrast the figure in the book makes it look as though the "header" comes at the page's end).  For more details on the format of a slotted page in ColgateDB, see the javadoc for `SlottedPageFormatter`.

**Task 1** Add this line to the end of the second `SlottedPage` constructor:
	
	setBeforeImage();  // used for logging, leave this line at end of constructor

Thus, the second constructor should look exactly like this:

    /**
     * Constructs SlottedPage with its data initialized according to last parameter
     * @param pid  page id to assign to this page
     * @param td   the schema for tuples held on this page
     * @param pageSize the size of this page
     * @param data data with which to initialize page content
     */
    public SlottedPage(PageId pid, TupleDesc td, int pageSize, byte[] data) {
        this(pid, td, pageSize);
        setPageData(data);
        setBeforeImage();  // used for logging, leave this line at end of constructor
    }


**Task 2**  Implement the `computePageCapacity` static method in `SlottedPageFormatter` and update the `getNumSlots` method in `SlottedPage`.  After you implement these methods, you should be able to pass the `computePageCapacity` and `slottedPageNumSlots` tests in `SlottedPageFormatterTest`.

**Task 3**  Implement the `getHeaderSize` static method in `SlottedPageFormatter` and pass the `testHeaderSize` test in `SlottedPageFormatterTest`.

**Task 4**  Now comes the challenging part, reading a page from bytes and writing a page as bytes.  First, read the javadoc at the top of `SlottedPageFormatter` so that you understand how a page is formatted.  Then implement the static methods `pageToBytes` and `bytesToPage`.  (You might find it helpful to implement the helper methods `isSlotUsed` and `markSlot` in `SlottedPageFormatter`.  These methods are optional; you are not required to implement them.)  You will need to call the `pageToBytes` and `bytesToPage` methods from the appropriate places in `SlottedPage`.  After you implement `pageToBytes` and call it appropriately in `SlottedPage`, you should be able to pass the `writeToBytes` series of unit tests in `SlottedPageFormatterTest`.  Similarly, `bytesToPage` is associated with the `readFromBytes` series of unit tests in `SlottedPageFormatterTest`.

Finally, once you have written both  `pageToBytes` and `bytesToPage`, you should be able to pass the remaining tests in `SlottedPageFormatterTest`.


## SQL Exercises

We will be using SQLite for this exercise.  Tutorials on sqlite were provided in [Lab 1](https://github.com/colgate-cosc460/colgatedb/blob/master/labs/lab1.md).

1. Open Terminal.  Change the current directory to be the `sql` subdirectory of `cosc460repo`.  If you do `ls lab3*` you should see this:
	
		$ cd <path to your cosc460repo>/sql
		$ ls lab3*
		lab3.sql lab3populate.sql lab3trace.txt

	Here's a description of each file:

	- `lab3populate.sql` - a file of sql commands that creates and populates the tables for the assignment (after dropping any old instances). You do not need to edit this or turn this in.
	- `lab3.sql` - a file containing the descriptions of the queries you need to implement.  You will add your SQL to this file and turn it in.
	- `lab3trace.txt` - a trace of the output that you should get from running your completed version of `lab3.sql`.  You do not need to edit this or turn this in.

2. To get started, do this:

		$ sqlite3 lab3.db
		SQLite version 3.8.5 2014-08-15 22:37:57
		Enter ".help" for usage hints.
		sqlite> 

    In the above, we called the database `lab3.db` but you can call it whatever you want. This command will cause a file named `lab3.db` to be created in the current directory.

3. At the prompt, populate the database by having sqlite read the `lab3populate.sql` file, like so:

		sqlite> .read lab3populate.sql 

4. The command `.schema` will display the database schema:

		sqlite> .schema
		CREATE TABLE Movie(mID int, title text, year int, director text);
		CREATE TABLE Reviewer(rID int, name text);
		CREATE TABLE Rating(rID int, mID int, stars int, ratingDate date);

5. After the data is loaded, you can write queries directly in the sqlite shell (always remember to terminate a SQL query with a semicolon). For example,

		sqlite> select * from Movie;
		101|Gone with the Wind|1939|Victor Fleming
		102|Star Wars|1977|George Lucas
		103|The Sound of Music|1965|Robert Wise
		104|E.T.|1982|Steven Spielberg
		105|Titanic|1997|James Cameron
		106|Snow White|1937|David Hand
		107|Avatar|2009|James Cameron
		108|Raiders of the Lost Ark|1981|Steven Spielberg


6. While you can write queries directly in the sqlite shell, you must ultimately save your work in the `lab3.sql` file.  You can open up `lab3.sql`in a text editor (Intellij also works) and copy and past queries from the shell.  You can also run the commands in `lab3.sql` by doing this:

		sqlite> .read lab3.sql 
		 
		Q1
		 
		Q2
		 
		Q3
		
		... and so on...

7. Before you turn in your work, execute `.read lab3.sql` and make sure your results match those shown in `lab3trace.txt`.



## Milestone

If the submitted code (see instructions above) passes the tests described above, then you have completed the milestone for this lab.  

## Submission instructions

To submit follow [the instructions from lab1](https://github.com/colgate-cosc460/colgatedb/blob/master/labs/lab1.md).  **Please signal to me that you have finished the lab** by writing a commit message that says `"completed lab 3; this lab was _____"` where you fill in the blank with a phrase that best describes your sentiments about this lab.

