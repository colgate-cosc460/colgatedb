# Lab 10, Part 1

This lab assignment is for lab on Tuesday, November 15th and is due **Sunday November 20th by 11:55PM.**  Submission instructions are at the end of the lab.

## Obtaining the Code

Follow [the same instructions as lab 2](https://github.com/colgate-cosc460/colgatedb/blob/master/labs/lab2.md) except that I suggest you add the `--no-edit` flag on the `git pull` command, like so

	git pull --no-edit https://github.com/colgate-cosc460/colgatedb.git master


## ColgateDB Lock Manager Continued...

This is *part one* of lab 10 and is a continuation of the previous lab.

### Implementation details

- **Details from previous lab** Review the [details from the previous lab](https://github.com/colgate-cosc460/colgatedb/blob/master/labs/lab9.md).
- **Deadlock detection** There are many possible ways to detect deadlock. For example, you may implement a simple timeout policy that aborts a transaction if it has not completed after a given period of time.  Alternately, as an additional challenge problem, you may implement cycle-detection in a dependency graph data structure. In this scheme, you would check for cycles in a dependency graph whenever you attempt to grant a new lock, and abort something if a cycle exists.
- **Timeout duration** If you go with the timeout approach, how long should you wait?  There's no simple answer.  Suggested implementation: wait a *random* period of time between 100 milliseconds and 200 milliseconds.  The randomness helps avoid the situation where multiple transactions start waiting at exactly the same time and thus timeout at the same time, preventing any one of them from making progress.
- **Choosing a victim** After you have detected that a deadlock exists, you must decide how to improve the situation. Assume you have detected a deadlock while transaction t is waiting for a lock. The simplest strategy is to simply abort t.  This is acceptable but you should be aware of the potential downsides of this simple strategy.
- **Cleaning up** Once deadlock is detected and you have chosen a victim, you must do a little clean up before aborting the victim.  You should **not** release the victim's locks.  This will be handled by other mechanisms outside of the `LockManager`.  However, you *should* remove any *outstanding requests* (there should be only one).  In addition, if you implement the waits for graph approach, be sure to *clean up the graph*.  There are two cases:
	+ Lock acquired on page p: this transaction is no longer waiting.  At a minimum, any edges added while waiting for p can be removed.  But, depending on your implementation, it may be acceptable to simply remove the transaction from the graph (provided that other waiting transactions can add it back as necessary).
	+ Transaction aborted: it's sufficient to remove all outgoing edges from the victim transaction since the victim is about to abort and therefore no longer waiting on any other transaction.


### Overview of unit tests

Here is a brief description of each unit test:

- `DeadlockTest`: runs a series of tests that you should be able to pass regardless of which implementation you choose for deadlock.


### Tasks

**Task 1** Implement a mechanism for deadlock detection.


## Milestone

The milestone for (part 1 of) this lab is to complete the above tasks and
pass the `DeadlockTest`.


## Submission instructions

To submit follow [the instructions from lab1](https://github.com/colgate-cosc460/colgatedb/blob/master/labs/lab1.md).  **Please signal to me that you have finished the lab** by writing a commit message that says `"completed lab 10; this lab was _____"` where you fill in the blank with a phrase that best describes your sentiments about this lab.

