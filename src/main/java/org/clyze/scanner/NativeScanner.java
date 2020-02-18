package org.clyze.scanner;

import java.io.*;
import java.util.*;

/**
 * The basic object that controls scanning of libraries.
 */
public class NativeScanner {
    /** Enable debug messages. */
    private final static boolean debug = false;
    /** The method strings to use for improving precision. */
    private final Set<String> methodStrings;

    /**
     * Create a native scanner object, to be used for analyzing native
     * libraries.
     *
     * @param methodStrings         a list of method substrings (names and
     *                              type descriptors) to use for filtering -- set to
     *                              null to disable this filtering
     */
    public NativeScanner(Set<String> methodStrings) {
        this.methodStrings = methodStrings;

        if (methodStrings != null)
            System.err.println("Initializing native scanner with " + methodStrings.size() + " strings related to methods.");
        else
            System.err.println("No method (sub)strings provided, the output may be imprecise.");
    }

    /**
     * Scan a piece of native code.
     *
     * @param analysis   the binary analysis to use
     */
    public void scanBinaryCode(BinaryAnalysis analysis) {
        String lib = analysis.getLib();
        System.out.println("== Processing library: " + lib + " ==");

        analysis.initEntryPoints();

        // Find all strings in the binary.
        System.out.println("Gathering strings from " + lib + "...");
        SortedMap<Long, String> allStrings = analysis.findStrings();
        if (allStrings == null || allStrings.size() == 0) {
            System.err.println("Cannot find strings in " + lib + ", aborting.");
            return;
        }
        System.out.println("Total strings: " + allStrings.size());
        if (debug)
            allStrings.forEach ((k, v) -> System.out.println(k + " -> " + v));

        // Filter the strings to work with a more manageable set
        // of strings.
        Map<String, List<SymbolInfo>> nameSymbols = new HashMap<>();
        Map<String, List<SymbolInfo>> methodTypeSymbols = new HashMap<>();
        SortedMap<Long, String> strings = new TreeMap<>();
        for (Map.Entry<Long, String> foundString : allStrings.entrySet()) {
            String s = foundString.getValue();
            // Keep only those that were encountered in the input
            // program as method names or signatures.
            if (methodStrings != null && !methodStrings.contains(s)) {
                // System.err.println("Rejecting string: " + s);
                continue;
            }
            Long addr = foundString.getKey();
            if (isMethodType(s)) {
                addSymbol(methodTypeSymbols, s, new SymbolInfo(s, lib, BinaryAnalysis.UNKNOWN_FUNCTION, addr));
                strings.put(addr, s);
            } else if (isName(s)) {
                addSymbol(nameSymbols, s, new SymbolInfo(s, lib, BinaryAnalysis.UNKNOWN_FUNCTION, addr));
                strings.put(addr, s);
            } else if (debug)
                // If this code runs, then a method-related string is not a name/type.
                System.err.println("WARNING: rejecting native string '" + s + "'");
        }
        System.out.println("Filter: " + strings.size() + " out of " + allStrings.size() + " survived.");
        if (debug)
            strings.forEach((k, v) -> System.out.println(k + " -> " + v));

        // If name or method type sets are empty, do not continue,
        // as their product will eventually be empty as well.
        int namesCount = nameSymbols.keySet().size();
        int methodTypesCount = methodTypeSymbols.keySet().size();
        if (namesCount == 0 || methodTypesCount == 0) {
            System.out.println("Product [name x type] is empty, ignoring native library.");
            return;
        } else {
            System.out.println("Possible method/class names: " + namesCount);
            System.out.println("Possible method types found: " + methodTypesCount);
        }

        // Find in which function every string is used.
        long xrefsTime1 = System.currentTimeMillis();
        Map<String, Set<XRef>> xrefs = analysis.findXRefs(strings);
        long xrefsTime2 = System.currentTimeMillis();
        System.out.println("Computed " + xrefs.size() + " xrefs (time: " + ((xrefsTime2 - xrefsTime1) / 1000.0) + " sec)");
        if (debug)
            xrefs.forEach ((k, v) -> System.out.println("XREF: '" + k + "' -> " + v) );

        // Write out facts: first write names and method types that
        // belong to known functions, then write everything else (that
        // may be found via radare or parsing the .rodata section).
        // For values that we know their containing function, we set special offsets.
        for (String s : xrefs.keySet()) {
            if (s == null)
                continue;
            if (isMethodType(s)) {
                Set<XRef> strRefs = xrefs.get(s);
                if (strRefs != null)
                    for (XRef strRef : strRefs)
                        addSymbol(methodTypeSymbols, s, new SymbolInfo(s, lib, strRef.function, null));
            } else if (isName(s)) {
                Set<XRef> strRefs = xrefs.get(s);
                if (strRefs != null)
                    for (XRef strRef : strRefs)
                        addSymbol(nameSymbols, s, new SymbolInfo(s, lib, strRef.function, null));
            }
        }

        // Resolve "unknown function" info.
        updateLibSymbolTable(nameSymbols, lib, xrefs);
        updateLibSymbolTable(methodTypeSymbols, lib, xrefs);

        // Finally write facts.
        analysis.writeFacts(xrefs, nameSymbols, methodTypeSymbols);
    }

    private static boolean isName(String line) {
        char[] chars = line.toCharArray();
        for (int i = 0; i < line.length(); i++) {
            char c = chars[i];
            if ((c != '$') && (c != '/') && (c != '_') &&
                (c != '<') && (c != '>') &&
                !Character.isLetterOrDigit(c)) {
                if (debug)
                    System.err.println("isName(): Rejecting char '" + c + "' : " + line);
                return false;
            }
        }
        return true;
    }

    private static boolean isMethodType(String line) {
        char[] chars = line.toCharArray();
        if (chars.length == 0)
            return false;
        if ((chars[0] != '(') || (!line.contains(")")))
            return false;
        for (int i = 0; i < line.length(); i++) {
            char c = chars[i];
            if ((c != ',') && (c != '/') && (c != '$') && (c != '[') &&
                (c != '(') && (c != ')') && (c != ';') && (c != '_') &&
                (!Character.isLetterOrDigit(c))) {
                if (debug)
                    System.err.println("isMethodType(): Rejecting char '" + c + "' : " + line);
                return false;
            }
        }
        return true;
    }

    static List<String> runCommand(ProcessBuilder builder) {
        if (debug)
            System.err.println("Running external command: " + String.join(" ", builder.command()));
        builder.redirectErrorStream(true);
        try {
            Process process = builder.start();
            InputStream is = process.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(is));

            List<String> lines = new LinkedList<>();
            String line;
            while ((line = reader.readLine()) != null)
                lines.add(line);
            return lines;
        } catch (IOException ex) {
            ex.printStackTrace();
            throw new RuntimeException("Could not run command: " + String.join(" ", builder.command()));
        }
    }

    /**
     * Unpack .xzs libraries (found in some .apk inputs).
     *
     * @param xzsPath    the path to the .xzs file
     * @return the path to the decompressed library
     */
    public static String getXZSLib(String xzsPath) {
        System.out.println("Processing xzs-packed native code: " + xzsPath);
        String xzPath = xzsPath.substring(0, xzsPath.length() - 1);
        // Change .xzs extension to .xz.
        runCommand(new ProcessBuilder("mv", xzsPath, xzPath));
        runCommand(new ProcessBuilder("xz", "--decompress", xzPath));
        return xzPath.substring(0, xzPath.length() - 3);
    }

    /**
     * Handle .zstd libraries (found in some .apk inputs).
     *
     * @param zstdPath   the path to the .zstd file
     * @return the path to the decompressed library
     */
    public static String getZSTDLib(String zstdPath) {
        System.out.println("Processing zstd-packed native code: " + zstdPath);
        String zstdOutPath = zstdPath.substring(0, zstdPath.length() - 5);
        runCommand(new ProcessBuilder("zstd", "-d", "-o", zstdOutPath));
        return zstdOutPath;
    }

    /**
     * Register a symbol with its info in a table.
     *
     * @param symbols    the symbols table
     * @param symbol     key: the (string) symbol
     * @param si         value: the symbol information
     */
    private static void addSymbol(Map<String, List<SymbolInfo> > symbols,
                                  String symbol, SymbolInfo si) {
        // Ignore @-suffix (such as '@plt' or '@@Base'), since it's not part of the mangled name.
        int atSymbol = symbol.indexOf("@");
        if (atSymbol != -1)
            symbol = symbol.substring(0, atSymbol);
        List<SymbolInfo> infos = symbols.getOrDefault(symbol, new LinkedList<>());
        infos.add(si);
        symbols.put(symbol, infos);
    }

    private static void regUnknown(Collection<String> uSymbols,
                                   String s, SymbolInfo v) {
        if (v.function.equals(BinaryAnalysis.UNKNOWN_FUNCTION)) {
            if (debug)
                System.out.println("Marking unknown symbol to localize: " + s);
            uSymbols.add(s);
        }
    }

    private void updateLibSymbolTable(Map<String, List<SymbolInfo>> symbols,
                                      String lib,
                                      Map<String, Set<XRef>> xrefs) {
        Collection<String> unknown = new HashSet<>();
        symbols.forEach((s, v) ->
                        v.forEach(v0 -> regUnknown(unknown, s, v0)));

        final long j = -1;
        for (String uString : unknown) {
            // System.out.println("updateLibSymbolTable('" + uString + "')");
            Set<XRef> uXRefs = xrefs.get(uString);
            if (uXRefs == null)
                continue;
            if (debug)
                System.out.println("Found xref information for string: " + uString);
            List<SymbolInfo> l = symbols.get(uString);
            for (XRef xref : uXRefs) {
                l.add(new SymbolInfo(uString, lib, xref.function, j));
            }
        }
    }

    /**
     * Factory method that creates a binary analysis for a target native code library.
     *
     * @param dbc                  the database consumer to use
     * @param radareMode           if true, a Radare analysis will be returned, if false, a binutils analysis
     * @param lib                  the path to the native code library
     * @param onlyPreciseStrings   only record strings with known position in the code
     * @param truncateAddresses    truncate addresses to 32-bit
     * @param demangle             do name demanging (on binutils analysis)
     * @return a binary analysis configured for the target native code library
     */
    public static BinaryAnalysis create(NativeDatabaseConsumer dbc, boolean radareMode,
                                        String lib, boolean onlyPreciseStrings,
                                        boolean truncateAddresses, boolean demangle) {
        return radareMode ?
            new RadareAnalysis(dbc, lib, onlyPreciseStrings, truncateAddresses)  :
            new BinutilsAnalysis(dbc, lib, onlyPreciseStrings, truncateAddresses, demangle);
    }
}

class SymbolInfo {
    private final String sym;
    final String lib;
    final String function;
    final Long offset;
    SymbolInfo(String sym, String lib, String function, Long offset) {
        this.sym = sym;
        this.lib = lib;
        this.function = function;
        this.offset = offset;
    }
    @Override
    public String toString() {
        return sym + "@" + lib + "." + offset + "(" + function + ")";
    }
}
