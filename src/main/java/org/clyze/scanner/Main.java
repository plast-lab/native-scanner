package org.clyze.scanner;

import java.io.*;
import java.nio.file.Files;
import java.util.*;
import java.util.stream.Stream;

/**
 * The command-line entry point of the native scanner.
 */
public class Main {
    private static final String OPT_HELP = "--help";
    private static final String OPT_ONLY_PRECISE_STRINGS = "--only-precise-strings";
    private static final String OPT_BINUTILS = "--binutils";
    private static final String OPT_RADARE = "--radare";
    private static final String OPT_BUILTIN = "--built-in";
    private static final String OPT_METHOD_STRINGS = "--method-strings=";
    private static final String OPT_TRUNCATE_ADDRESSES = "--truncate-addresses";
    private static final String OPT_DEMANGLE_ENTRY_POINTS = "--demangle-entry-points";
    private static final String OPT_NO_XREF = "--no-xref";

    public static void main(String[] args) {
        if (args.length == 0) {
            printUsage();
            return;
        }
        
        boolean onlyPreciseStrings = false;
        BinaryAnalysis.AnalysisType analysisType = BinaryAnalysis.AnalysisType.RADARE;
        boolean truncateAddresses = false;
        boolean demangle = false;
        boolean computeXRefs = true;
        Set<String> methodStrings = null;
        List<File> inputs = new ArrayList<>();

        for (String arg : args) {
            if (OPT_HELP.equals(arg)) {
                printUsage();
                return;
            } else if (OPT_ONLY_PRECISE_STRINGS.equals(arg))
                onlyPreciseStrings = true;
            else if (OPT_RADARE.equals(arg))
                analysisType = BinaryAnalysis.AnalysisType.RADARE;
            else if (OPT_BINUTILS.equals(arg))
                analysisType = BinaryAnalysis.AnalysisType.BINUTILS;
            else if (OPT_BUILTIN.equals(arg))
                analysisType = BinaryAnalysis.AnalysisType.BUILTIN;
            else if (OPT_TRUNCATE_ADDRESSES.equals(arg))
                truncateAddresses = true;
            else if (OPT_DEMANGLE_ENTRY_POINTS.equals(arg))
                demangle = true;
            else if (OPT_NO_XREF.equals(arg))
                computeXRefs = false;
            else if (arg.startsWith(OPT_METHOD_STRINGS)) {
                File msFile = new File(arg.substring(OPT_METHOD_STRINGS.length()));
                if (!msFile.exists()) {
                    System.err.println("Method string file does not exist: " + arg);
                    return;
                }
                if (methodStrings == null)
                    methodStrings = new HashSet<>();
                try (Stream<String> stream = Files.lines(msFile.toPath())) {
                    stream.forEach(methodStrings::add);
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            } else {
                File f = new File(arg);
                if (f.exists())
                    inputs.add(f);
                else {
                    System.err.println("Input file does not exist: " + arg);
                    return;
                }
            }
        }

        // A simple consumer to show the results on screen.
        for (File f : inputs) {
            BasicDatabaseConsumer dbc = new BasicDatabaseConsumer();
            NativeScanner scanner = new NativeScanner(computeXRefs, methodStrings);
            String lib = f.getAbsolutePath();
            if (lib.endsWith(".apk") || lib.endsWith(".jar") || lib.endsWith(".aar")) {
                scanner.scanArchive(dbc, analysisType, onlyPreciseStrings, truncateAddresses, demangle, f);
            } else {
                BinaryAnalysis analysis = NativeScanner.create(dbc, analysisType, lib, onlyPreciseStrings, truncateAddresses, demangle);
                scanner.scanBinaryCode(analysis);
            }
            dbc.getProduct().forEach(System.out::println);
        }
    }
    
    private static void printUsage() {
        System.out.println("Usage: native-scanner [OPTION]... [FILE]...");
        System.out.println("Options:");
        System.out.println("  " + OPT_HELP + "                    Print this help message.");
        System.out.println("  " + OPT_ONLY_PRECISE_STRINGS + "    Filter out strings without position information.");
        System.out.println("  " + OPT_RADARE + "                  Use Radare2 mode (default).");
        System.out.println("  " + OPT_BINUTILS + "                Use binutils mode.");
        System.out.println("  " + OPT_BUILTIN + "                 Use built-in mode.");
        System.out.println("  " + OPT_TRUNCATE_ADDRESSES + "      Truncate addresses to fit in 32 bits.");
        System.out.println("  " + OPT_METHOD_STRINGS + "<FILE>   Use method strings from <FILE> for filtering." );
        System.out.println("  " + OPT_DEMANGLE_ENTRY_POINTS + "   Demangle names of C++ entry points.");
        System.out.println("  " + OPT_NO_XREF + "                 Skip string-xrefs analysis.");
    }
}
