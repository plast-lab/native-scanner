package org.clyze.scanner;

/**
 * This interface should be implemented by all clients of the native
 * scanner library.
 * @see BasicDatabaseConsumer
 */
public interface NativeDatabaseConsumer {
    /**
     * Record a tuple in a table.
     *
     * @param table   the name of the table
     * @param arg     the value of the first column
     * @param args    the values of the rest of the columns
     */
    void add(String table, String arg, String... args);
}
