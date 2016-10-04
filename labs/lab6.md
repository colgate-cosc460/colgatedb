# Lab 6

This lab assignment is for lab on Tuesday, October 4th and is due **Friday, October 14th at 5:00pm**. Submission instructions are at the end of the lab.

## Obtaining the Code

Follow [the same instructions as lab 2](https://github.com/colgate-cosc460/colgatedb/blob/master/labs/lab2.md) except that I suggest you add the `--no-edit` flag on the `git pull` command, like so

	git pull --no-edit https://github.com/colgate-cosc460/colgatedb.git master


## Instructions

In this lab, you are asked to implement a version of the B+Tree data structure.  Recall that in a real B+Tree, the data structure is *persistent*: each node is stored on a separate page and the entire data structure is written out to stable storage.  Your B+tree implementation is not required to be persistent: the entire thing should live in main memory and disappear when the program finishes executing.

**Note**: You are encouraged to discuss ideas and approaches with other students.  However, looking at an existing implementation of a B+Tree -- whether written by another student, or found online, etc. -- is considered *a violation of the academic honor code*.  In any case, you will be asked questions on future exams about B+Trees and implementing it yourself is a good way to ensure you understand it well!

Requirements:

- You must implement `insert`, `getRecord` and `toString`.  You will undoubtedly want to write other methods.
- You must implement a main method in `BTreeMain` that performs operations on a tree sufficient to demonstrate that your implementation works correctly.

A few implementation details:

- Your tree should *not* permit duplicate keys.  If `insert` is called with a key that is already stored in the B+Tree, then a `BTreeException` should be raised.
- You are *not* required to link leaf nodes together.
- You may assume that all keys are integers and records are java Objects.  (The starter code that I provide makes this assumption.)
- The Cow book is somewhat imprecise in its description of inserts (Fig. 10.10, p. 349).  If a node does not have space for a new entry, the book talks about splitting the node but it never actually says where you should insert the new entry!  Here's my suggestion: "flip" the order of operations.  First, insert the entry.  Then, if this causes the node to *over capacity*, then split the node.  (Trying to split before inserting the entry is harder, in my opinion.)

## Optional challenge problems

For an additional challenge, you may implement any of the following enhancements:

- Make your B+Tree implementation generic (using Java generics) so it can store arbitrary key value pairs.  (The key should implement `Comparable` or the B+Tree constructor should take a `Comparator` object.)  If you do this, you will necessarily have to modify the interfaces of the provided code.
- Add support for deleting entries from the tree.
- Add support for *range queries*.

## Milestone

Since this is a stand-alone lab, it does not have a milestone per se.  You are expected to complete the entire lab by the deadline.  In keeping with the grading policy outlined in the syllabus (and subsequent piazza post), I will provide feedback and you can revise your implementation and turn in an improved version along with your final ColgateDB submission.

## Submission instructions

To submit follow [the instructions from lab1](https://github.com/colgate-cosc460/colgatedb/blob/master/labs/lab1.md).  **Please signal to me that you have finished the lab** by writing a commit message that says `"completed lab 6; this lab was _____"` where you fill in the blank with a phrase that best describes your sentiments about this lab.

