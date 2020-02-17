package org.clyze.scanner;

import java.util.*;

/**
 * A simple implementation of a database consumer.
 */
public class BasicDatabaseConsumer implements NativeDatabaseConsumer {
    private Map<String, Set<String>> product = new HashMap<>();
    private Set<String> signatures = new HashSet<>();

    @Override
    public void add(String predicateFile, String arg, String... args) {
        StringBuilder sb = new StringBuilder(predicateFile);
        sb.append(": ").append(arg);
        for (String s : args)
            sb.append("  ").append(s);
        System.out.println(sb.toString());

        if (predicateFile.equals(BinaryAnalysis.NATIVE_NAME_CANDIDATE)) {
            String name = args[1];
            if (product.get(name) == null)
                product.put(name, signatures);
        } else if (predicateFile.equals(BinaryAnalysis.NATIVE_METHODTYPE_CANDIDATE))
            signatures.add(args[1]);
    }

    /**
     * Calculates the name/method-type string product.
     * @return a set of method candidates
     * @see Candidate
     */
    public Set<Candidate> getProduct() {
        Set<Candidate> ret = new HashSet<>();
        product.forEach((name, v) -> v.forEach(mt -> ret.add(new Candidate(name, mt))));
        return ret;
    }

    /**
     * A pair of a method name and a method type.
     */
    public static class Candidate {
        /** A method name. */
        public final String name;
        /** A method type (JVM descriptor syntax). */
        public final String methodType;

        public Candidate(String n, String mt) {
            this.name = n;
            this.methodType = mt;
        }

        @Override
        public int hashCode() {
            return Objects.hash(name, methodType);
        }

        @Override
        public String toString() {
            return name + ": " + methodType;
        }
    }
}
