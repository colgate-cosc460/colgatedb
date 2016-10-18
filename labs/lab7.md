# Lab 7

This lab assignment is for lab on Tuesday, October 18th and is due **Sunday October 30th by 11:55PM.**  (*Your are strongly encouraged to work on this lab leading up to the exam next week.*)  Submission instructions are at the end of the lab.

## Obtaining the Code

Follow [the same instructions as lab 2](https://github.com/colgate-cosc460/colgatedb/blob/master/labs/lab2.md) except that I suggest you add the `--no-edit` flag on the `git pull` command, like so

	git pull --no-edit https://github.com/colgate-cosc460/colgatedb.git master


## ColgateDB Instructions

In this lab assignment, you will write a set of operators for ColgateDB that provide support for querying and modifying the data. The operators provide support for selections, joins, insertions, and deletions. These will build on top of the foundation that you wrote in previous labs to provide you with a database system that can perform simple queries over multiple tables. You do not need to implement transactions or locking in this lab though you may continue to see mentions of it in the code.

A few details:

-  Your implementation must be consistent with the pipeline approach described in the book (and in lecture). It is *not* acceptable to materialize the result -- i.e., build a list of tuples and then simply return an iterator over that set of tuples. (There are exceptions to this rule, for instance if you do the optional challenge problem of implementing aggregation operators.) Your implementation should use a constant amount of memory regardless of the size of the input database.

- Unlike most of your previous labs, in this lab I am asking you to implement a fairly large number of classes.  Many of them require a fairly small amount of coding.  The most algorithmically challenging part is probably implementing `Join`.

### Some helper files

Included with this lab is a small database. The files are `college.schema`, `students.dat`, `takes.dat`, `courses.dat`, and `profs.dat`. Here are the contents of the database:

	students(sid(INT_TYPE), name(STRING_TYPE))
	------------------------------------------
	1 alice
	2 bob
	3 alice
	4 charles
	5 alice

	takes(sid(INT_TYPE), cid(INT_TYPE))
	-----------------------------------
	1 301
	2 301
	2 460
	4 460
	1 302

	courses(cid(INT_TYPE), title(STRING_TYPE))
	------------------------------------------
	301 os
	302 algo
	460 db

	profs(pid(INT_TYPE), name(STRING_TYPE), favoriteCourse(INT_TYPE))
	-----------------------------------------------------------------
	10  sommers 301
	20  hay 460
	20  ramachandran  302


### Suggested Tasks

**Task 1** Review the implementation of `SeqScan`.  This class sequentially scans all of the tuples from the pages of the table specified by the tableid in the constructor. If this sounds familiar, that's because `SeqScan` will (indirectly) call the iterator you wrote for `HeapFile`.  `SeqScan` is essentially a wrapper that talks to the Catalog, retrieves the appropriate `DbFile` object and obtain that object's `DbFileIterator`.  The only method that is non-trivial is the constructor which updates the `TupleDesc` renaming the attributes as necessary (see the javadocs).  

**Task 2** Implement `Predicate` and then `Filter`.  The implementation should pass `PredicateTest` and `FilterTest`.  Before working on the `Filter` operator, you may find it helpful to look at the implementation of the `Project` operator.  (Like `Filter`, `Project` is a subclass of `Operator`.)

**Task 3** At this point, you should be able to use your operators to execute basic queries.  Run the main method of `Lab7Main` which effectively computes the following query:

	SELECT * 
	FROM Students
	WHERE name="alice"

When run, you should see something like this:

	Loading schema from file: college.schema
	Added table : Students with schema sid(INT_TYPE), name(STRING_TYPE) Table has 1 pages.
	Added table : Takes with schema sid(INT_TYPE), cid(INT_TYPE) Table has 0 pages.
	Added table : Courses with schema cid(INT_TYPE), title(STRING_TYPE) Table has 1 pages.
	Added table : Profs with schema pid(INT_TYPE), name(STRING_TYPE), favoriteCourse(INT_TYPE) Table has 1 pages.
	Query results:
		1	alice
		3	alice
		5	alice


**Task 4** Implement `JoinPredicate` and then `Join`.  The implementation should pass `JoinPredicateTest` and `JoinTest`.

**Task 5** Write three unit tests in JoinTest.  Follow the template of `eqJoin` except that you might use different input "table" scans and/or other predicates besides equals.  (Small detail: if a variable is only used in one test (such as a new table scan), please make it a local variable and not a field.) Each test **must include a javadoc explanation** of what behavior is being tested (with an emphasis on distinctions from any other tests written).

**Task 6** Modify `Lab7Main` to execute this query

	SELECT S.name
	FROM Students S, Takes T, Profs P
	WHERE S.sid = T.sid AND
	      T.cid = P.favoriteCourse AND
	      P.name = "hay"

It is up to you to decide how to translate this SQL query into a relational algebra expression. Note that you may need to use the Project operator, which is included in the repo. Also, do not assume that Hay's favorite course is 460 -- your query must work correctly even if the database instance changes. The correct answer to the query on the provided data files is:

	Query results:
	  bob
	  charles


**Task 7** Implement `Insert` and `Delete` operators. For plans that implement insert and delete queries, the top-most operator is a special `Insert` or `Delete` operator that modifies the pages on disk.  It does so by looking up the appropriate table in the Catalog and calling the insertTuple and deleteTuple methods on the resulting `DbFile`.  (Recall that your `HeapFile` class is an implementation of the `DbFile` interface.)  These operators return the number of affected tuples. This is implemented by returning a single tuple with one integer field, containing the count.  Note that even when no tuples are inserted or deleted, this operator should still return a tuple (with a count of zero).

- Insert: This operator adds the tuples it reads from its child operator to the tableid specified in its constructor. It should use the `DbFile.insertTuple()` method to do this.  When completed, you should be able to pass `InsertTest`.
- Delete: This operator deletes the tuples it reads from its child operator from the tableid specified in its constructor. It should use the `DbFile.deleteTuple()` method to do this.  Use the RecordId of each tuple to find the DbFile from which it should be deleted.

## Milestone

The milestone for this lab is to pass the unit tests mentioned above, write three tests of your own in `JoinTest`, and edit `Lab7Main`.  As with previous labs, I may supply additional tests later.

### Optional challenge problem

*Optional*: This exercise is an optional challenge problem that earns you an unspecified number of extra points. Only attempt these after completing the rest of the assignment.

An additional operator implements basic SQL aggregates with a GROUP BY clause. You should implement the five SQL aggregates (COUNT, SUM, AVG, MIN, MAX) and support grouping. You only need to support aggregates over a single field, and grouping by a single field.

In order to calculate aggregates, we use an `Aggregator` interface which merges a new tuple into the existing calculation of an aggregate. The `Aggregator` is told during construction what operation it should use for aggregation. Subsequently, the client code should call `Aggregator.mergeTupleIntoGroup()` for every tuple in the child iterator. After all tuples have been merged, the client can retrieve a `DbIterator` of aggregation results. Each tuple in the result is a pair of the form `(groupValue, aggregateValue)`, unless the value of the group by field was `Aggregator.NO_GROUPING`, in which case the result is a single tuple of the form `(aggregateValue)`.

Note that this implementation requires space linear in the number of distinct groups. For the purposes of this lab, you do not need to worry about the situation where the number of groups exceeds available memory.

Implement `IntegerAggregator`, `StringAggregator`, and `Aggregate`.  In coding your implementation, you might find it handy to use the `TupleIterator`. At this point, your code should pass the unit tests `IntegerAggregatorTest`, `StringAggregatorTest`, and `AggregateTest`. 


## Submission instructions

To submit follow [the instructions from lab1](https://github.com/colgate-cosc460/colgatedb/blob/master/labs/lab1.md).  **Please signal to me that you have finished the lab** by writing a commit message that says `"completed lab 7; this lab was _____"` where you fill in the blank with a phrase that best describes your sentiments about this lab.

