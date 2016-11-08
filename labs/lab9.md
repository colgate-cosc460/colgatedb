# Lab 9

This lab assignment is for lab on Tuesday, November 8th (please vote!) and is due **Sunday November 13th by 11:55PM.**  Submission instructions are at the end of the lab.

## Obtaining the Code

Follow [the same instructions as lab 2](https://github.com/colgate-cosc460/colgatedb/blob/master/labs/lab2.md) except that I suggest you add the `--no-edit` flag on the `git pull` command, like so

	git pull --no-edit https://github.com/colgate-cosc460/colgatedb.git master


## ColgateDB Lock Manager

In this lab assignment, you will write a lock manager for ColgateDB that provides support for transactions to acquire locks on pages.

Your implementation should be written in `LockManagerImpl` and it should support the `LockManager` interface.

Your implementation should closely follow the description in the Cow book (17.2-17.4).  This includes support for shared/exclusive locks and (eventually) deadlock detection.  For deadlock detection, you may implement a timeout-based approach or the check for cycles in the waits-for graph (challenge problem!).  Note: however that *you are not required to implement deadlock detection in this lab*.  Nevertheless, you should keep it in mind during implementation as it could affect your design decisions (see below).

### Implementation details 

There are some subtleties that the book does not cover or are worth reiterating.  I encourage you to review these periodically when working your implementation:

- **Lock table entries**: A class is provided called `LockTableEntry` that you may wish to use this in your lock manager.  Feel free to modify this class or not use it at all.  It will never be accessed outside the lock manager implementation.  (It should probably be an inner class of the LockManagerImpl because it's really an internal data structure.  I moved it to a separate class mainly to make the code a little easier to read.)
- **Make lock acquisition atomic**: Since multiple transactions will be running concurrently, you need to be especially careful to avoid race conditions *during the process of acquiring locks*.  For example, two transactions may both try to acquire an exclusive lock on the same page. Acquiring a lock is fundamentally a multi-step process: at the very least, you have to first check that the lock is free, and then you acquire it. We want to avoid this bad scenario: Transaction t1 checks the lock, finds it free and then between this step and actually acquiring the lock, transaction t2 comes along and also finds the lock free and acquires it. Thus, both t1 and t2 will believe they have an exclusive lock! You **must** make lock acquisition an atomic operation.  This is where [Java synchronization](http://docs.oracle.com/javase/tutorial/essential/concurrency/sync.html) comes in handy.
- **Update the lock table before waiting**:  The method `acquireLock` should check whether a lock is available and if not, make the thread wait.  But it's very important that before waiting, the thread should update the lock manager state as appropriate.  For example, it should place a lock request in the appropriate queue.
- **Upon deadlock, remove queued requests and throw an exception**: If deadlock is detected and a transaction thread is going to abort itself, it must be sure to remove any outstanding lock requests (there should be only one).  Then, it should throw a `TransactionAbortedException`.  At this point, you do **not** need to worry about releasing held locks (this will be handled later by code that will catch the thrown exception).
- **Deadlock detection**: If you implement deadlock detection using a waits for graph, then transactions can use `wait()` to wait and `notifyAll()` to alert waiting transaction threads.  However, if you use a timeout approach, this probably won't work.  Why not?  Each transaction runs in a separate thread and when that thread calls `wait()` it will effectively sleep until some other thread calls `notifyAll()`.  But in the case of deadlock, the deadlocked threads will all be waiting and no one will be active to check whether a timeout has occurred.  Thus, if you use the timeout approach, then it is recommended that each thread "busy wait" (see previous lab). 



### Overview of unit tests

There are three unit tests with this lab.  Here is a brief description of each:

- `LockManagerTest`: there is no concurrency in this test class.  Instead it is testing that the LockManager state is appropriately updated when locks are acquired and released.  It also tests the other helper methods.  You can pass all of the tests in this class a fairly simple `acquireLock` that simple always grants the lock.
- `ConcurrencyControlTest`: this is very similar to the `LockManagerDemo` from last week.  A collection of transaction threads are started, each threads repeatedly does the following three steps: (a) acquire an exclusive lock, (b) increment a shared counter, and (c) release the lock.  If the `LockManagerImpl` is implemented correctly, there should be no thread interference.  Note since all threads are acquiring exclusive locks, this does not test any functionality around shared locks, upgrades, etc.  Further, only a single item is being locked so there is no deadlock.
- `LockScheduleTest`: this is a test harness designed to more thoroughly test the *logic* of lock requests.  For example, this can be used test that two shared locks can be acquired simultaneously.  Only a couple of 


### Tasks

**Task 1** Implement the lock manager!  Okay, this is a big task.  There are not crisply defined subtasks because it really depends on how you proceed.  You might try to work towards the unit tests above, starting with `LockManagerTest`, then `ConcurrencyControlTest`, and finally `LockScheduleTest`.  With this strategy, you could start as simple as possible and then revise/refactor your code as you go.

**Task 2** Add **at least three** unit tests to `LockScheduleTest`.  Include comments that clearly explain the rationale for the test.  See `upgradeRequestCutsInLine` as an example.


## Milestone

The milestone for this lab is to complete the above tasks and
pass the `LockManagerTest`, `ConcurrencyControlTest`, and `LockScheduleTest`.


## Submission instructions

To submit follow [the instructions from lab1](https://github.com/colgate-cosc460/colgatedb/blob/master/labs/lab1.md).  **Please signal to me that you have finished the lab** by writing a commit message that says `"completed lab 9; this lab was _____"` where you fill in the blank with a phrase that best describes your sentiments about this lab.

