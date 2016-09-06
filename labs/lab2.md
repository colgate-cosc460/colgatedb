# Lab 2

This lab assignment is for Tuesday, September 6th and is due **Sunday Sept. 11th by 11:59PM.** Submission instructions are at the end of the lab.

## Obtaining the Code

Each lab will follow a basic work flow: I will update the [repository for ColgateDB](https://github.com/colgate-cosc460/colgatedb) with new Java classes.  You will need to obtain these new files before you start working on the lab. To do this follow these instructions, which are based on [this article](https://help.github.com/articles/merging-an-upstream-repository-into-your-fork/).

1. Open Terminal.

2. Change the current working directory to your local project.

3. Check out the branch you wish to merge to. (Unless you went and created your own branches, you will simply merge into master.)

		$ git checkout master

4. Pull the desired branch from the upstream repository. This method will retain the commit history without modification.

		$ git pull https://github.com/colgate-cosc460/colgatedb.git master

    *You should not have conflicts*.  If there are conflicts, please let me know.  If a conflict does occur it may be easily resolved.  For more information, see "[Resolving a merge conflict from the command line](https://help.github.com/articles/resolving-a-merge-conflict-from-the-command-line/)".

5. Commit the merge.

6. Review the changes and ensure they are satisfactory.

7. Push the merge to your GitHub repository.

		$ git push origin master


## ColgateDB Instructions

In the previous lab, you created the Java representation of a `Tuple`.  Now we move up a level of granularity and implement the Java representation of a page.  Recall that a page is an important concept in the database architecture: it represents the minimal unit of data that is read from disk into memory and written back out to disk.  A significant fraction of the ColgateDB architecture centers around manipulating pages.  Thus, it is essential that you implement this well!  You'll notice many of the methods include all sorts of error checking and exception throwing.  This is to help the "future you" when you are coding more complex components of ColgateDB that rely on your page implementation.

The main task is to implement the methods of the `SlottedPage` class.  This page stores a collection of *fixed-length* tuples.  Tuples are stored in slots.  The basic architecture is similar to the organization described in the Ch. 9.6.1 of the Cow book.  *An important detail*: this lab is focused exclusively with the Java class representation of the data.  The following lab will address the problem of representing a page as a sequence of bytes and translating between the byte representation and the Java class representation.  Therefore, during this lab you will not see much discussion of byte-level format details (such as what is described in Figure 9.6 of the book.)  We'll get to that next week.

Before implementing `SlottedPage` itself, you are asked to implement two other classes `RecordId` and `SimplePageId`.  Recall that all tuples are assigned record ids and all pages must have page ids.  These identifiers allow other parts of the DBMS to unambiguously locate particular pages and records.

**Task 1**  Implement the methods in `SimplePageId`.  After you implement these methods, you should be able to pass the unit tests in `SimplePageIdTest`.

**Task 2**  Implement the methods in `RecordId`.  After you implement these methods, you should be able to pass the unit tests in `RecordIdTest`.

**Task 3**  Implement the methods in `SlottedPage`.  After you implement these methods, you should be able to pass the unit tests in `SlottedPageTest`.


## Milestone

If the submitted code (see instructions below) passes the tests described above, then you have completed the milestone for this lab.  

## Submission instructions

To submit follow [the instructions from lab1](https://github.com/colgate-cosc460/colgatedb/blob/master/labs/lab1.md).  **Please signal to me that you have finished the lab** by writing a commit message that says `"completed lab 2; this lab was _____"` where you fill in the blank with a phrase that best describes your sentiments about this lab.  I appreciate your feedback!



