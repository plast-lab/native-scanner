package org.clyze.scanner;

/**
 * A simple implementation of a database consumer.
 */
public class PrintDatabaseConsumer implements NativeDatabaseConsumer {
    @Override
    public void add(String predicateFile, String arg, String... args) {
        StringBuilder sb = new StringBuilder(predicateFile);
        sb.append(": ").append(arg);
        for (String s : args)
            sb.append("  ").append(s);
        System.out.println(sb.toString());
    }
}
