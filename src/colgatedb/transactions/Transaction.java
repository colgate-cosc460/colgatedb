package colgatedb.transactions;

import colgatedb.Database;

import java.io.IOException;

/**
 * Transaction encapsulates information about the state of
 * a transaction and manages transaction commit / abort.
 */

public class Transaction {
    private final TransactionId tid;
    volatile boolean started = false;

    public Transaction() {
        tid = new TransactionId();
    }

    /** Start the transaction running */
    public void start() {
        started = true;
        try {
            Database.getLogFile().logXactionBegin(tid);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public TransactionId getId() {
        return tid;
    }

    /** Finish the transaction */
    public void commit() throws IOException {
        transactionComplete(true);
    }

    /** Finish the transaction */
    public void abort() throws IOException {
        transactionComplete(false);
    }

    /** Handle the details of transaction commit / abort */
    private void transactionComplete(boolean commit) throws IOException {

        if (started) {
            if (commit) {
                Database.getLogFile().logCommit(tid);
            } else {
                Database.getLogFile().logAbort(tid); //does rollback too
            }
            Database.getAccessManager().transactionComplete(tid, commit);
        } else {
            throw new RuntimeException("Txn was never started!");
        }
    }
}
