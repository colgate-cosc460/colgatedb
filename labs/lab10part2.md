# Lab 10, Part 2

This lab assignment is for lab on Tuesday, November 15th and is due **Sunday November 20th by 11:55PM.**  Submission instructions are at the end of the lab.

## Obtaining the Code

Follow [the same instructions as lab 2](https://github.com/colgate-cosc460/colgatedb/blob/master/labs/lab2.md) except that I suggest you add the `--no-edit` flag on the `git pull` command, like so

	git pull --no-edit https://github.com/colgate-cosc460/colgatedb.git master


## ColgateDB Lock Manager Continued...

This is *part two* of lab 10 and is a continuation of the previous lab.  In this part, you will write an implementation of the `AccessManager` in the `AccessManagerImpl` class.  In addition, you will revise your `HeapFile` code to call `AccessManager` instead of `BufferManager`.


### Tasks

- **Task 1** Revise all existing code that pins pages.  Rather than calling the `BufferManager` directly, it should instead use the `AccessManager` via the `Database.getAccessManager()` method.  

- **Task 2** Keep track of pins and unpins inside `AccessManagerImpl`.  This will be important later when it comes to handling aborted transactions.  For now, ensure that your `AccessManagerImpl` maintains a list of transactions that have pinned each page.  Keep in mind (1) a transaction could pin a page multiple times (thus generating multiple entries in a list), and (2) multiple transactions will be executing this code concurrently so updates must be done atomically (use `synchronized` blocks).

- **Task 3** Ensure that your code implements strict 2PL.  At a high level, this involves two things.  First, before pinning a page, appropriate locks should be acquired.  There are some subtle detail inside `HeapFile` (see below).  Second, all locks should be held until the transaction aborts or commits.  Look at `Transaction.commit()` and `Transaction.abort()` and follow what the code does.  Then it should be clear where you need to release the locks.

- **Task 4** When a transaction ends (either in an abort or commit), certain actions must be taken in addition to releasing locks.  
	+ The precise actions depends on the force policy which, by default, is set to `true`; something we will change in a later lab. 
	+ If a transaction is committing and a force policy is in place, then pages dirtied by this transaction should be flushed to disk.  Be sure that the page was dirtied by *this* transaction and not some other transaction!
	+ If a transaction is aborting, then any pages that are still pinned should be unpinned and any dirtied pages should be discarded from the buffer pool.

- **Task 5** Enforce a "no steal" policy.  At this point ColgateDB does not have a logging system nor the capability to undo changes made by a transaction that aborts.  Thus, we should ensure a "no steal" policy: pages that have been dirtied by a transaction should not be allowed to be flushed to disk until the transaction has committed.  Make sure that `AccessManagerImpl` enforces a no steal policy. (Hint: this can be done with one line in the constructor of `AccessManagerImpl`.)


### Implementation details

- **Acquiring locks in HeapFile** There are some subtleties around acquiring and releasing locks in the following situations:
	+ Looking for an empty slot into which you can insert tuples. Most implementations scan pages looking for an empty slot, and will need a `READ_ONLY` lock to do this. If a transaction t finds no free slot on a page p, t may immediately release the lock on p. Although this technically contradicts the rules of two-phase locking, this deviation is permitted because t did not use any data from the page, such that a concurrent transaction t' which updated p cannot possibly effect the answer or outcome of t.  *Important*: the lock should only be released if it was just acquired; if the transaction already had the lock (e.g., from a previous operation), then the lock should *not* be released.
	+ Adding a new page to a `HeapFile`.  When a transaction needs to allocate a *new* page to the HeapFile, care must be taken that the entire page allocation process is done atomically.  Note: this is not as simple as making `allocatePage` atomic (why not?). 
	<!-- We want to avoid a situation where two transactions simultaneously allocate a page and yet, due to race conditions, it may appear as though only one page has been allocated. -->




### Overview of unit tests

Here is a brief description of each unit test:

- `TransactionTest`: checks that locks are appropriately released when a transaction commits/aborts.



## Milestone

The milestone for (part 2 of) this lab is to complete the above tasks and
pass the `TransactionTest`.


## Submission instructions

To submit follow [the instructions from lab1](https://github.com/colgate-cosc460/colgatedb/blob/master/labs/lab1.md).  **Please signal to me that you have finished the lab** by writing a commit message that says `"completed lab 10 part 2; this lab was _____"` where you fill in the blank with a phrase that best describes your sentiments about this lab.

