package org.clyze.scanner;

import java.io.*;
import java.nio.file.CopyOption;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;

/**
 * The basic object that controls scanning of libraries.
 */
public class NativeScanner {
    /** Enable debug messages. */
    private final static boolean debug = false;
    /** The method strings to use for improving precision. */
    private final Set<String> methodStrings;
    /** If false, no string-xrefs will be computed. */
    private final boolean computeXRefs;

    /**
     * Create a native scanner object, to be used for analyzing native
     * libraries.
     *
     * @param computeXRefs          if false, don't do string-xrefs analysis
     * @param methodStrings         a list of method substrings (names and
     *                              type descriptors) to use for filtering -- set to
     *                              null to disable this filtering
     */
    public NativeScanner(boolean computeXRefs, Set<String> methodStrings) {
        this.computeXRefs = computeXRefs;
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
        Map<String, Set<XRef>> xrefs;
        if (computeXRefs) {
            long xrefsTime1 = System.currentTimeMillis();
            xrefs = analysis.findXRefs(strings);
            long xrefsTime2 = System.currentTimeMillis();
            System.out.println("Computed " + xrefs.size() + " xrefs (time: " + ((xrefsTime2 - xrefsTime1) / 1000.0) + " sec)");
            if (debug)
                xrefs.forEach ((k, v) -> System.out.println("XREF: '" + k + "' -> " + v) );
        } else {
            xrefs = new HashMap<>();
            strings.forEach((k, s) -> xrefs.put(s, new HashSet<>()));
        }

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
        // We assume that "<clinit>()" are not called by JNI code, only constructors.
        if (line.equals("<init>"))
            return true;
        char[] chars = line.toCharArray();
        if (line.length() == 0 || !Character.isJavaIdentifierStart(chars[0]))
            return false;
        for (int i = 1; i < line.length(); i++) {
            char c = chars[i];
            if (!Character.isJavaIdentifierPart(c)) {
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
        if ((chars[0] != '(') || (!line.contains(")") || (line.charAt(line.length()-1) == ')')))
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
     * @param analysisType         the type of the engine to use (binutils/Radare2/built-in)
     * @param lib                  the path to the native code library
     * @param onlyPreciseStrings   only record strings with known position in the code
     * @param truncateAddresses    truncate addresses to 32-bit
     * @param demangle             do name demanging (on binutils analysis)
     * @return a binary analysis configured for the target native code library
     */
    public static BinaryAnalysis create(NativeDatabaseConsumer dbc, BinaryAnalysis.AnalysisType analysisType,
                                        String lib, boolean onlyPreciseStrings,
                                        boolean truncateAddresses, boolean demangle) {
        if (analysisType == BinaryAnalysis.AnalysisType.RADARE)
            return new RadareAnalysis(dbc, lib, onlyPreciseStrings, truncateAddresses);
        else if (analysisType == BinaryAnalysis.AnalysisType.BINUTILS)
            return new BinutilsAnalysis(dbc, lib, onlyPreciseStrings, truncateAddresses, demangle);
        else {
            if (debug) {
                System.out.println("Ignoring parameter: onlyPreciseStrings = " + onlyPreciseStrings);
                System.out.println("Ignoring parameter: truncateAddresses = " + truncateAddresses);
                System.out.println("Ignoring parameter: demangle = " + demangle);
            }
            return new BuiltinAnalysis(dbc, lib);
        }
    }

    /**
     * Scan the native code in a compressed archive (ZIP-based formats: JAR/AAR/APK).
     * @param dbc                  the database consumer to use
     * @param analysisType         the type of the engine to use (binutils/Radare2/built-in)
     * @param onlyPreciseStrings   only record strings with known position in the code
     * @param truncateAddresses    truncate addresses to 32-bit
     * @param demangle             do name demanging (on binutils analysis)
     * @param f                    the ZIP file
     */
    public void scanArchive(NativeDatabaseConsumer dbc, BinaryAnalysis.AnalysisType analysisType,
                            boolean onlyPreciseStrings, boolean truncateAddresses,
                            boolean demangle, File f) {
        try (ZipInputStream zin = new ZipInputStream(new FileInputStream(f));
             ZipFile zipFile = new ZipFile(f)) {
            ZipEntry entry;
            while ((entry = zin.getNextEntry()) != null) {
                /* Skip directories */
                if (!entry.isDirectory())
                    scanArchiveEntry(dbc, analysisType, onlyPreciseStrings,
                            demangle, truncateAddresses, zipFile, entry,
                            entry.getName().toLowerCase());
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }

    }
    /**
     * Process an entry inside a compressed archive (ZIP-based formats: JAR/AAR/APK).
     *
     * @param dbc                  the database consumer to use
     * @param analysisType         the type of the engine to use (binutils/Radare2/built-in)
     * @param onlyPreciseStrings   only record strings with known position in the code
     * @param truncateAddresses    truncate addresses to 32-bit
     * @param demangle             do name demanging (on binutils analysis)
     * @param file                 the ZIP file
     * @param entry                the ZIP entry
     * @param entryName            the name of the ZIP entry
     * @throws IOException on archive scan I/O error
     */
    public void scanArchiveEntry(NativeDatabaseConsumer dbc, BinaryAnalysis.AnalysisType analysisType,
                                 boolean onlyPreciseStrings, boolean truncateAddresses,
                                 boolean demangle, ZipFile file, ZipEntry entry,
                                 String entryName) throws IOException {
        boolean isSO = entryName.endsWith(".so");
        boolean isDylib = entryName.endsWith(".dylib");
        boolean isDLL = entryName.toLowerCase().endsWith(".dll");
        boolean isLibsXZS = entryName.endsWith("libs.xzs");
        boolean isLibsZSTD = entryName.endsWith("libs.zstd");

        if (isSO || isDylib || isDLL || isLibsXZS || isLibsZSTD) {
            // Replace ".." to guard against bad archives accessing the filesystem.
            String entryNameSanitized = entryName.replaceAll("..", "_");
            File libTmpFile = extractZipEntryAsFile("native-lib", file, entry, entryNameSanitized);
            String libPath = libTmpFile.getCanonicalPath();
            // Handle some special formats.
            if (isLibsXZS)
                libPath = NativeScanner.getXZSLib(libPath);
            else if (isLibsZSTD)
                libPath = NativeScanner.getZSTDLib(libPath);
            BinaryAnalysis analysis = NativeScanner.create(dbc, analysisType, libPath, onlyPreciseStrings, truncateAddresses, demangle);
            // Check that the current mode supports .dll inputs.
            if (isDLL && analysisType != BinaryAnalysis.AnalysisType.RADARE)
                System.err.println("WARNING: Radare mode should be activated to scan library " + entryName);

            scanBinaryCode(analysis);
        }
    }

    /**
     * Helper method to extract an entry inside a ZIP archive and save
     * it as a file.
     *
     * @param tmpDirName   a name for the intermediate temporary directory
     * @param zipFile      the ZIP archive
     * @param entry        the archive entry
     * @param entryName    the name of the entry
     * @return             the output file
     */
    @SuppressWarnings("SameParameterValue")
    private static File extractZipEntryAsFile(String tmpDirName, ZipFile zipFile, ZipEntry entry, String entryName) throws IOException {
        File tmpDir = Files.createTempDirectory(tmpDirName).toFile();
        tmpDir.deleteOnExit();
        // Replace all directory separator characters with an underscore.
        // Do not use String.replaceAll(), since it chokes on Windows separators.
        StringBuilder sb = new StringBuilder();
        String sep = File.separator;
        boolean singleCharSep = sep.length() == 1;
        for (char c : entryName.toCharArray())
            if (singleCharSep && sep.charAt(0) == c)
                sb.append("_");
            else
                sb.append(c);
        String tmpName = sb.toString();
        File libTmpFile = new File(tmpDir, tmpName);
        libTmpFile.deleteOnExit();
        long bytesCopied = copy(zipFile.getInputStream(entry), libTmpFile.toPath());
        if (debug)
            System.out.println("Number of bytes copied: " + bytesCopied);
        return libTmpFile;
    }

    /**
     * Like Files.copy(), but creates the parent directory of the destination.
     *
     * @param in           the input stream to read
     * @param target       the target path to write
     * @param options      possible options
     * @return             the number of processed bytes
     * @throws IOException on error
     */
    public static long copy(InputStream in, Path target, CopyOption... options) throws IOException {
        Path parent = target.getParent();
        if (parent != null) {
            File fParent = parent.toFile();
            if (!fParent.exists())
                fParent.mkdirs();
        }
        return Files.copy(in, target, options);
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
