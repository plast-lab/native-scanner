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
    private static final String OPT_METHOD_STRINGS = "--method-strings=";
    private static final String OPT_TRUNCATE_ADDRESSES = "--truncate-addresses";

    public static void main(String[] args) {
        if (args.length == 0) {
            printUsage();
            return;
        }
        
        boolean onlyPreciseStrings = false;
        boolean binutilsMode = false;
        boolean radareMode = false;
        boolean truncateAddresses = false;
        Set<String> methodStrings = null;
        List<File> inputs = new ArrayList<>();
        
        for (String arg : args) {
            if (OPT_HELP.equals(arg)) {
                printUsage();
                return;
            } else if (OPT_ONLY_PRECISE_STRINGS.equals(arg))
                onlyPreciseStrings = true;
            else if (OPT_RADARE.equals(arg))
                radareMode = true;
            else if (OPT_BINUTILS.equals(arg))
                binutilsMode = true;
            else if (OPT_TRUNCATE_ADDRESSES.equals(arg))
                truncateAddresses = true;
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

        if (binutilsMode && radareMode) {
            System.err.println("ERROR: only one of options " + OPT_BINUTILS + " and " + OPT_RADARE + " should be used.");
            return;
        }

        // A simple consumer to show the results on screen.
        NativeDatabaseConsumer dbc = new PrintDatabaseConsumer();
        NativeScanner scanner = new NativeScanner(dbc, radareMode, onlyPreciseStrings, truncateAddresses, methodStrings);
        for (File f : inputs)
            scanner.scanLib(f);
    }
    
    private static void printUsage() {
        System.out.println("Usage: native-scanner [OPTION]... [FILE]...");
        System.out.println("Options:");
        System.out.println("  " + OPT_HELP + "                    Print this help message.");
        System.out.println("  " + OPT_ONLY_PRECISE_STRINGS + "    Filter out strings without position information.");
        System.out.println("  " + OPT_BINUTILS + "                Use binutils mode.");
        System.out.println("  " + OPT_RADARE + "                  Use Radare2 mode.");
        System.out.println("  " + OPT_TRUNCATE_ADDRESSES + "      Truncate addresses to fit in 32 bits.");
        System.out.println("  " + OPT_METHOD_STRINGS + "<FILE>   Use method strings from <FILE> for filtering." );
    }
}
