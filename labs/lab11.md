# Lab 11

This lab assignment is for lab on Tuesday, November 29th and is due **Friday December 9th by 11:55PM.**  Submission instructions are at the end of the lab.

## Obtaining the Code

Follow [the same instructions as lab 2](https://github.com/colgate-cosc460/colgatedb/blob/master/labs/lab2.md) except that I suggest you add the `--no-edit` flag on the `git pull` command, like so

    git pull --no-edit https://github.com/colgate-cosc460/colgatedb.git master


## ColgateDB Recovery

In this lab you will implement log-based rollback for aborts and log-based crash recovery. I supply code that defines the log format and appends records to a log file at appropriate times during transactions. You will implement rollback and recovery using the contents of the log file.

The logging code I provide generates records that support *physical*  undo and redo (as opposed to *logical* undo and redo) and the logging is at the granularity of entire *pages*. When a page is first read in, the code remembers the original content of the page as a before-image. When a page is modified, the corresponding log record contains a before-image along with an after-image (which is simply the current version of the page). You'll use the before-image to rollback during aborts and to undo loser transactions during recovery, and the after-image to redo winners during recovery.

Logging at the page granularity has some obvious advantages.  Because we are also locking at the page level, the recovery protocol is greatly simplified.  (If a transaction modified a page, it must have had an exclusive lock on it, which means no other transaction was concurrently modifying it, so we can UNDO changes to it by just overwriting the whole page.)  On other hand, it has some disadvantages in that it is not especially efficient (our log records are large) and it is not a viable approach for supporting more complex file structures like indexes (see book for discussion).

Your `AccessManagerImpl` currently requires that changes are flushed upon commit (FORCE) and *only* flushed upon commit (NO STEAL).  This makes handling aborts pretty easy: just discard dirty pages.  Logging allows more flexible buffer management (STEAL and NO-FORCE), and you are expected to revise your implementation to support this more flexible policy.


### Implementation details 

Your implementation consists of implementing some methods in `LogFileRecovery` and slightly revising your existing `AccessManagerImpl` code and `BufferManagerImpl` code.  I provide `LogFile` which provides methods for writing log records to the log file as well as `LogType` which defines the different kinds of log records. 

**Task 0** Add a new constructor to `SlottedPage`.  Recovery expects to be able to read page data given only a page id and the bytes.  (The `TupleDesc` can be obtained from the Catalog.)  Add the following constructor to your `SlottedPage`.

    public SlottedPage(PageId pid, byte[] bytes) {
        this(pid, Database.getCatalog().getTupleDesc(pid.getTableId()), bytes.length, bytes);
    }


**Task 1** *Writing database updates to the log*.  The first task is to make sure that ColgateDB obeys the logging protocol, writing out the appropriate log records at the appropriate time.  Inside `AccessManagerImpl.unpinPage`, if the page being unpinned is dirty, then an appropriate record should be written to the log.  You should add this line
    
    Database.getLogFile().logWrite(dirtier, p.getBeforeImage(), p);
where `p` is the page that is being unpinned and `dirtier` is the transaction who dirtied the page.  

**Task 2** *Write ahead logging (WAL)*.  It is critical that changes are written and flushed to the log before the corresponding changes are flushed to disk.  To flush the contents of the log, you can simply execute the following command:

    Database.getLogFile().force();
It is up to you to figure out where this line of code should be added.  If you use Intellij's "find usages" command, you can see that the `LogFile.force` method is already called in several places.  You must add (at least) one more call to this method to ensure WAL.

**Task 3** *Updating the before image.*  As you can see above, to write an update log record, we need information about what the page looked like *before* this transaction modified it. This information is captured in the before image.

The before image is first set when a page is read from disk.  (You should double check that `setBeforeImage` is called inside the `SlottedPage` constructor.) Then, when a transaction commits, the before image must be updated to reflect the changes made by this committed transaction. This is necessary so that later transactions that abort will rollback to this committed version of the page.

Inside `AccessManagerImpl.transactionComplete`, you should make the following modification: if the transaction is committing, for any page `p` that was dirtied by this transaction and still residing the buffer pool, the before image of `p` must be set as follows:

    p.setBeforeImage();

Hint: in order for the transaction to dirty a page it must have an exclusive lock on it, so it suffices to check all pages locked by the commiting transaction and set the before image for only those pages that are dirty (the `BufferManager.isDirty` and `BufferManager.getPage`methods come in handy here).

**Task 4** *Implement rollback*.  Read the comments in `LogFileImpl` for a description of the log file format. You should see in `LogFileImpl` a set of functions, such as `logCommit()`, that generate appropriate log records and append them to the log.

Your first job is to implement the `rollback()` function in `LogFileRecovery` This function is called from `LogFile.logAbort` which is called whenever a transaction aborts, before the transaction releases its locks. Its job is to un-do any changes the transaction may have made to the database.Your implementation should read the log file, find all update records associated with the aborting transaction, extract the before-image from each, and write the before-image to the database. 

To *read* the log file, use the private field `readOnlyLog` of `LogFileRecovery`, which is a read only version of the log file.  Use `readOnlyLog.seek()` to move around, and `readOnlyLog.readInt()`, `readOnlyLog.readLong()` etc. to examine the contents of a particular log record. Use `LogFile.readPageData()` to read each of the before- and after-images. In addition, use constants such as `LogFile.LONG_SIZE` and those defined in `LogType`.

To *write* new records to the log file, use commands such as the following:

    Database.getLogFile().logAbort(tidToRollback.getId());

Note: You will also want to make sure that you discard any page from the buffer pool any page that is being rolled back.

As you develop your code, you may find the `Logfile.print()` method useful for displaying the current contents of the log.

**Task 5** *Implement recovery*.  If the database crashes and then reboots, `LogFileRecovery.recover()` will be called before any new transactions start. Your implementation should perform the following tasks:

1. Read the last checkpoint, if any.  (See the documentation in `LogFileImpl` for information on how to find the checkpoint and what format the checkpoint message takes.)
2. Scan forward from the checkpoint (or start of log file, if no checkpoint) to build the set of loser transactions. Re-do updates during this pass. You can safely start re-do at the checkpoint because `LogFile.logCheckpoint()` flushes all dirty buffers to disk.
3. Un-do the updates of loser transactions.

**Task 6** *Review and revise your implementation to support STEAL and NO-FORCE*.  Your implementation should now be capable of supporting a different policies including the preferred combination of STEAL (dirty pages can be flushed before commits) and NO-FORCE (dirty pages should not be flushed when a transaction commits).


### Overview of unit tests

Unit tests will be provided a little later this week.  (This page will be updated when they are.)


## Milestone

The milestone for this lab is to complete the above tasks and
pass the unit tests listed above.


## Submission instructions

To submit follow [the instructions from lab1](https://github.com/colgate-cosc460/colgatedb/blob/master/labs/lab1.md).  **Please signal to me that you have finished the lab** by writing a commit message that says `"completed lab 11; this lab was _____"` where you fill in the blank with a phrase that best describes your sentiments about this lab.

