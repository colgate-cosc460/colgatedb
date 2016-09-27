# Lab 5

This lab assignment is for lab on Tuesday, September 27th and is due **Sunday Oct. 2nd by 11:59PM.** Submission instructions are at the end of the lab.

## Obtaining the Code

Follow [the same instructions as lab 2](https://github.com/colgate-cosc460/colgatedb/blob/master/labs/lab2.md) except that I suggest you add the `--no-edit` flag on the `git pull` command, like so

	git pull --no-edit https://github.com/colgate-cosc460/colgatedb.git master


## ColgateDB Instructions

In this lab, you are asked to implement a Heap File.  You are strongly encouraged to review your notes from class and re-read the textbook (Cow book, especially Ch. 9.5).

One of the things that makes this lab interesting is that we are now starting to bring the pieces together into a functioning database.  For example, your `HeapFile` implementation will sit on top of your `BufferManager` and it will read and write data onto `SlottedPage` objects.  Those pages are transferred to/from disk using a `DiskManager` implementation that I provide.  In addition, this lab introduces some new components, such as the `Catalog`, which maintains information about the tables stored in the database.  By bringing different units of code together, one of the *learning goals* for this lab is that you further develop your *conceptual understanding* of database architecture.

This lab is challenging in ways that are different from earlier labs.  First, you may need to spend more time exploring the code before you dive in and implement.  Second, often what happens when you bring code together is that bugs surface in existing code, despite the fact that this code may have passed some earlier tests.  Thus, if your `HeapFile` crashes on some test, do not assume the error is in `HeapFile`.  The source of the bug may in fact be in your `SlottedPage` implementation, for instance.

**Note**: In the javadocs and various places, you may notice comments about acquiring locks.  You will also see references to `TransactionId` objects.  For now, you can simply ignore these comments and references.  Later in the semester you will *revise* your `HeapFile` implementation so that it supports multiple users making concurrent database modifications.  

### High Level Overview

To implement `HeapFile` you have to support three basic operations:

- **Adding tuples**: The `insertTuple` method in `HeapFile` is responsible for adding a tuple to a heap file. To add a new tuple to a `HeapFile`, you will have to find a page with an empty slot. If no such pages exist in the `HeapFile`, you need to allocate a new page.
- **Removing tuples**: To remove a tuple, you will need to implement `deleteTuple` in `HeapFile`. This is as simple as locating the page on which the tuple resides and calling the delete method you implemented on `SlottedPage`. Remember that a tuple contains a `RecordID` which allows you to find the page on which it resides. (Make sure your `SlottedPage` implementation sets the `RecordId` to null upon deletion.) 
- **Iteration over tuples**: You will also need to implement the `iterator()` method, which should iterate through through the tuples of each page in the `HeapFile`. Your iterator *must* be (space and time) efficient!  In particular, your implementation should consume a *minimal amount of memory* beyond what the Buffer Manager has allocated.  A rough guideline: your `HeapFile` may have roughly 1 page worth of data but not much more than that.  An implementation that loads the entire table's worth of data into memory will not receive any credit.

**VERY IMPORTANT**: Your `HeapFile` implementation must obtain data by calling the `BufferManager`.  It should **never** interact directly with the `DiskManager`.  Also, since some `BufferManager` methods return generic `Page` objects, you will need to cast them to `SlottedPage` objects in `HeapFile`.


### Suggested Tasks

**Task 0** Answer the "Code Exploration Questions" listed below.  You should be able to complete this during the 2 hour lab.  Please **email me your answers** by **Thursday night at 11:55pm**.

**Task 1** Implement the various `get` methods in `HeapFile`.  You should be able to pass the associated tests.

**Task 2** Implement `insertTuple`.  You may want to write a "helper" method that finds (or creates!) a page into which you can insert a record.

**Task 3** Implement `deleteTuple`.  This method should be much simpler than `insertTuple`.  

**Task 4** Implement the heap file iterator.


### Code Exploration Questions

1. If you need to obtain a reference to the `BufferManager`, what method do you call?  Hint: look at the javadocs of `Database`.
1. How does the disk manager store pages on disk?  (Hint: find an implementation of the `DiskManager` interface.)
1. Given the `DiskManager` interface, explain why the database can only *increase* in size. (Note: a real database should not have this property.  ColgateDB takes some shortcuts here and there to reduce overall complexity.)
1. The book describes two ways of organizing pages of a heap file (directory and linked list).  In ColgateDB, the organization can be much simpler because of the way the Disk Manager stores pages.  Explain how.
1. The `HeapFile` should *never* directly call methods on the `DiskManager`.  Instead it should interact with what object?
1. Recall that a heap file stores an *unordered* collection of tuples. Explain at a high level what `HeapFile.insertTuple` must do.  For example, what if the heap file has just been created and it does not have any pages? What if the heap file has 10 pages but they are all full?  What if the heap file has one empty slot and it is on page 3?  
1. What methods on the `BufferManager` should be called during the invocation of `HeapFile.insertTuple`?
1. When `deleteTuple` is called, it should (obviously) only be called on tuples that actually exist in the heap file.  What should happen if it is called with a
tuple that does not exist (hint: read the javadoc for `deleteTuple`).
1. In implementing the `HeapFileIterator` you need to implement `hasNext` which should return true if there is another tuple in the heap file.  Suppose your heap file currently has N pages.  Given that many tuples may have been *inserted* and then *deleted* over time, how many pages (in the worst case) must `hasNext` check before returning?  Your answer should be a number between 0 and N.


## Milestone

The milestone for this lab is to pass tests in `HeapFileTest`.  I will likely provide additional tests later but the only tests needed for the milestone are those contained in `HeapFileTest`.

## Submission instructions

To submit follow [the instructions from lab1](https://github.com/colgate-cosc460/colgatedb/blob/master/labs/lab1.md).  **Please signal to me that you have finished the lab** by writing a commit message that says `"completed lab 5; this lab was _____"` where you fill in the blank with a phrase that best describes your sentiments about this lab.

