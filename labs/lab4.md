# Lab 4

This lab assignment is for lab on Tuesday, September 20th and is due **Sunday Sept. 25th by 11:59PM.** Submission instructions are at the end of the lab.

There are two parts to this lab: 

1. Continued development on ColgateDB
2. SQL exercises: aggregations and subqueries


## Obtaining the Code

Follow [the same instructions as lab 2](https://github.com/colgate-cosc460/colgatedb/blob/master/labs/lab2.md) except that I suggest you add the `--no-edit` flag on the `git pull` command, like so

	git pull --no-edit https://github.com/colgate-cosc460/colgatedb.git master


## ColgateDB Instructions

In this lab, you are asked to implement a Buffer Manager.  You are strongly encouraged to review your notes from class and re-read the textbook (Cow book, especially Ch. 9.4).

In this lab you are asked to complete `BufferManagerImpl`, an implementation of the `BufferManager` interface.  Your implementation must respect *the requirement that the size of the buffer pool never exceeds a specified size*.  Therefore, it must implement a *replacement policy* that determines which page to evict when the buffer pool is full.

**Note**: the unit test for this lab uses the idea of "[mock objects](https://en.wikipedia.org/wiki/Mock_object)."  In particular, `BufferManagerTest` has a `MockDiskManager` that mimics the behavior of a real disk manager for the purpose of testing your buffer manager (which must make calls to a disk manager to read/write pages).  In fact, the mock disk manager even writes mock pages!  In a future lab, I will provide a real disk manager and your buffer manager will eventually read/write your slotted pages to/from disk.

Here is a suggested sequence of tasks.  You are free to choose a different order provided that you complete all of them.

**Task 0** Read the javadoc comments in `BufferManager`.

**Tasks 1-10** In `BufferManagerImpl`, implement the 10 methods in the order listed.  The unit tests in `BufferManagerTest` roughly follow this order, so each time you implement a method you should be able to pass additional tests.  In your *initial* implementation, pretend that the buffer pool has unlimited capacity (i.e., you never need to evict a page).  With this approach, you should be able to pass every test except those with the word "evict" in them.  

**Task 11** Now it's time to face reality: your Buffer Pool has finite capacity.  Its capacity is measured in terms of the number of pages it can hold in its buffer pool and this number is specified by the `numPages` parameter that is passed into the constructor.  Therefore, once the pool is full, the next request to pin a page will force an eviction according to your replacement policy.  I suggest you start by implementing a dead simple eviction strategy: find the first page that is a *candidate* for replacement and evict it.  With this strategy you should be able to pass the remaining tests in `BufferManagerTest`.

**Task 12** Implement a more intelligent and efficient eviction strategy.  You have some flexibility here and I encourage you to have some fun with this.  There are some interesting data structure challenges here...  you know, the kind of stuff that engineers like to ask about in interviews for tech jobs :-).

Your grade for this lab will be a function of the level of difficulty of what you attempt and the quality of your resulting solution.  Very roughly, here are my guidelines:

- C level of difficulty: implement the dead simple eviction strategy.
- B level of difficulty: implement a correct but inefficient LRU.  For example, inefficient might mean that either `pinPage` or page eviction will require time roughly linear in the size of the buffer pool.
- A level of difficulty: implement a more efficient LRU or the clock replacement scheme and evaluate the computational efficiency of your implementation.  

Of course merely *attempting* an A level of difficulty does not guarantee an A.  

Regardless of which option you choose, **you must include a comment in your code that explains your implementation.**  My suggestion is that you write a method called `evictPage` and put the documentation in the javadoc for that method.

## SQL Exercises

This lab includes additional SQL exercises.  Follow the instructions from [Lab 3](https://github.com/colgate-cosc460/colgatedb/blob/master/labs/lab3.md), but replace `lab3` with `lab4` everywhere you see it.


## Milestone

The milesone for this lab is:

- complete the SQL exercises 
- pass the provided tests
- include a javadoc description of your eviction strategy.  
- submit your work

The eagle eyed observer will notice that you can pass the tests without implementing a smart/efficient replacement policy.  Here's the deal: your final grade for ColgateDB  will be a function of the quality of your code, which includes the quality of your buffer manager replacement policy implementation.  While you can continue to revise your code as the semester goes on, I will permit *a maximum increase of one grade level*.  So, for example, if you turn in a C implementation now and then revise it later, the maximum grade (for the lab4 component of your grade) would be a B.  The rationale for this limit is to discourage procrastination.  Of course, lab 4 will be just a component of your overall ColgateDB grade so even if you get a C on this component, you could still do well overall.


## Submission instructions

To submit follow [the instructions from lab1](https://github.com/colgate-cosc460/colgatedb/blob/master/labs/lab1.md).  **Please signal to me that you have finished the lab** by writing a commit message that says `"completed lab 4; this lab was _____"` where you fill in the blank with a phrase that best describes your sentiments about this lab.

