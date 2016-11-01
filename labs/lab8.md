# Lab 8

This lab assignment is for lab on Tuesday, November 1st and is due **Sunday November 6th by 11:55PM.**  Submission instructions are at the end of the lab.

## Obtaining the Code

Follow [the same instructions as lab 2](https://github.com/colgate-cosc460/colgatedb/blob/master/labs/lab2.md) except that I suggest you add the `--no-edit` flag on the `git pull` command, like so

	git pull --no-edit https://github.com/colgate-cosc460/colgatedb.git master


## Instructions: Concurrency Tutorial

This series of exercises is intended to serve as an introduction to writing concurrent programs in Java. In order to complete later labs, you must have a strong understanding of a few basic concepts.

For some of you, this may be your first experience with concurrency. Others of you may have had some exposure in previous courses (e.g., operating systems). Whether this is an introduction or a review, I hope you find this tutorial helpful.


### Java Tutorial

Oracle, the company that maintains the Java language, has a very well written [tutorial on concurrency](http://docs.oracle.com/javase/tutorial/essential/concurrency/index.html). Please read the Java Concurrency Tutorial. Please read up to and including the section titled "Guarded Blocks." Later parts of this tutorial will refer to specific sections of this tutorial.

### Preventing Interference Through Synchronization

The `SynchronizedThreads` class demonstrates a simple program in which multiple threads (`Incrementers`) concurrently access a single shared object (a `Counter`). Review this code and make sure you understand it.

Run the main program see if the threads interfere with each other. Play around with the parameters. For example, if numThreads is 1, there should be no interference. Similarly if numAdds is 1 or a small number, you might not experience any interference.

**Task 1**. Prevent interference (aka race conditions) by modifying the code. You may wish to review the section of the Java tutorial on synchronization, especially the part on [synchronization idioms](http://docs.oracle.com/javase/tutorial/essential/concurrency/syncmeth.html).
There are at least *two ways* to prevent inference: one way modifies the `Counter` class; the other way leaves `Counter` unchanged and modifies the `Incrementer` class. Implement one way and *describe how you would implement the other way*.

There is lab writeup found at `labs/lab8questions.txt`.  Describe your implementation and your alternative implementation there.

### Deadlock 

Check out the `Deadlock` example. This is copied directly from the corresponding section on [deadlock in the Java tutorial](http://docs.oracle.com/javase/tutorial/essential/concurrency/deadlock.html).

**Task 2**. Explain the deadlock that happens in `Deadlock` in terms of locks and threads waiting on locks. The two threads in this case are alphonse and gaston. 

- a) What is being locked? 
- b) Who has what locks? 
- c) How does deadlock occur?
- d) Return to Task 1. Can deadlock occur here? Explain why or why not.

Record your answers in the writeup.  Your explanations can be short (2-3 sentences) but must be precise!

### Lock Manager

The `LockManagerDemo` is very similar to `SynchronizedThreads` except that concurrency is handled by a lock manager. The lock manager is a separate class that manages shared resources. Threads (such as `Incrementer` objects) that want to access shared objects (such as `Counter`) must ask the lock manager for a lock and then release it when done. The advantage of this is the nitty gritty details of lock management are encapsulated into a single class; all other classes like `Incrementer` and `Counter` don't need to "worry" about these details.

Pay special attention to this example as you will implement something similar in an upcoming lab!

**Task 3** Explain why `acquireLock` uses a [synchronized statement](http://docs.oracle.com/javase/tutorial/essential/concurrency/locksync.html) inside the body of the method. In other words, why not just make the `acquireLock` method synchronized, just like `releaseLock`? Will this work? Why or why not?  Record your answers in the writeup.

This implementation uses a busy wait: the thread keeps looping until it gets the lock. This can be wasteful because the thread uses up valuable CPU cycles waiting for the lock. To reduce wasteful CPU cycles, the thread goes to sleep for 1 millisecond each time throught the loop. But an even better way is to have the thread pause completely until the lock becomes available.

**Task 4** Revise this code to use `wait` and `notifyAll` as [illustrated in the Java tutorial](http://docs.oracle.com/javase/tutorial/essential/concurrency/guardmeth.html).

While waiting and notifying is generally preferred, busy waiting has its advantages in some cases. In an upcoming, you may find busy waiting makes it easier to detect deadlock (because each thread can keep track of how long it has been waiting).

### Zig Zag Lock Manager

Check out the `ZigZagThreads` class. The main method of this program spawns a bunch of `Ziggers` and then a bunch of `Zaggers`. `Ziggers` print this pattern

	//////////

and `Zaggers` print this pattern

	\\\\\\\\\\

If you run the code, you will probably see a bunch of zigs followed by a bunch of zags. This is not what we want. Instead, we want this pattern:

	//////////
	\\\\\\\\\\
	//////////
	\\\\\\\\\\
	//////////
	\\\\\\\\\\
	...

Notice that before printing, both `Ziggers` and `Zaggers` must acquire a lock. Your task is to implement a lock manager that forces them to alternate, producing the desired pattern.

**Task 5**. Implement the lock manager in `ZigZagThreads` to achieve the desired pattern.  Hint: the lock manager needs to keep track of two things.  The first thing is whether someone has the lock (i.e., someone is busy printing).  The second thing is whose turn it is (a Zigger or Zagger).

You might think this zig zag thing is a weird problem -- and it kind of is -- but in an upcoming lab, you will implement a lock manager for page objects that will actually bear some similarity to this problem.  In particular, you will not only have to manage locks but you also have to make sure that the lock is granted to the appropriate thread.  Here, Ziggers and Zaggers must "take turns"; in an upcoming lab, some threads will have higher priority than others and will be able to "cut in line" and acquire the lock first.

<!-- Your implementation should employ the busy waiting approach rather than the wait and notify approach. -->


## Milestone

The milestone for this lab is to complete the above tasks and submit the writeup.  (There are no unit tests.)


## Submission instructions

To submit follow [the instructions from lab1](https://github.com/colgate-cosc460/colgatedb/blob/master/labs/lab1.md).  **Please signal to me that you have finished the lab** by writing a commit message that says `"completed lab 8; this lab was _____"` where you fill in the blank with a phrase that best describes your sentiments about this lab.

