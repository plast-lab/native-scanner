package org.clyze.scanner;

import java.io.*;
import java.util.*;

/**
 * A binary analysis that is available to the native scanner.
 */
public abstract class BinaryAnalysis {

    public enum AnalysisType {
        BINUTILS, RADARE, BUILTIN
    }

    /** The name of the relation containing method name strings. */
    public static final String NATIVE_NAME_CANDIDATE = "NATIVE_NAME_CANDIDATE";
    /** The name of the relation containing method types (JVM-style method descriptors). */
    public static final String NATIVE_METHODTYPE_CANDIDATE = "NATIVE_METHODTYPE_CANDIDATE";

    /** Truncate long addresses to fit 32 bits. Used for clients that
     *  do not support 64-bit integers (such as Doop using some builds
     *  of Souffle). */
    private final boolean truncateTo32Bits;

    /** Dummy value for "offset" column in facts. */
    private static final String UNKNOWN_OFFSET = "-1";
    /** Dummy value for "function" column in facts. */
    static final String UNKNOWN_FUNCTION = "-";
    /** Dummy address. */
    static final long UNKNOWN_ADDRESS = -1;

    /** The database connector to use for writing facts. */
    private final NativeDatabaseConsumer dbc;
    /** The native code library. */
    protected final String lib;
    /** The entry points table. */
    final SortedMap<Long, String> entryPoints = new TreeMap<>();
    /** Only output localized strings (i.e. found inside function
     *  boundaries). When function boundaries can be determined, this
     *  improves precision. */
    private final boolean onlyPreciseNativeStrings;

    /** A dictionary of library properties. */
    protected Map<String, String> info = null;

    /** The native code architecture. */
    protected Arch libArch;

    BinaryAnalysis(NativeDatabaseConsumer dbc, String lib,
                   boolean onlyPreciseNativeStrings, boolean truncateTo32Bits) {
        this.dbc = dbc;
        this.lib = lib;
        this.onlyPreciseNativeStrings = onlyPreciseNativeStrings;
        this.truncateTo32Bits = truncateTo32Bits;

        // Auto-detect architecture.
        this.libArch = autodetectArch();
    }

    /**
     * Return a set of the strings found in a binary.
     *
     * @return a map of address-to-string entries
     */
    public SortedMap<Long, String> findStrings() {
        try {
            Section rodata = getSection(".rodata");
            if (rodata == null)
                rodata = getSection(".rdata");
            if (rodata != null)
                return rodata.strings();
        } catch (IOException ex) {
            ex.printStackTrace();
            throw new RuntimeException("Could not read strings.");
        }
        return new TreeMap<>();
    }

    /**
     * Returns the path of the analyzed binary.
     *
     * @return the native library path
     */
    public String getLib() {
        return this.lib;
    }

    /**
     * Find string cross-references.
     *
     * @param binStrings   the string table (offset-string entries)
     * @return             a mapping from strings to references in code
     */
    abstract public Map<String, Set<XRef>> findXRefs(Map<Long, String> binStrings);

    /**
     * Initialize the entry points table of the library.
     */
    abstract public void initEntryPoints();

    /**
     * Autodetect the target hardware architecture.
     *
     * @return the architecture
     */
    abstract public Arch autodetectArch();

    /**
     * A getter of the "info" field.
     *
     * @return the "info" mapping
     */
    abstract public Map<String, String> getNativeCodeInfo();

    /**
     * Autodetect the endianness of the target architecture.
     *
     * @return if true, architecture is little-endian, big-endian otherwise
     */
    public boolean isLittleEndian() {
        return getNativeCodeInfo().get("endian").equals("little");
    }

    /**
     * Determine the word size of the target architecture.
     *
     * @return the word size (in bytes)
     */
    abstract public int getWordSize();

    /**
     * Returns a list of pointer values that may point to global data.
     */
    protected Set<Long> getGlobalDataPointers() throws IOException {
        Section data = getSection(".data");
        return data == null ? null : data.analyzeWords(getWordSize(), isLittleEndian());
    }

    /**
     * Reads a section by name.
     *
     * @param sectionName   the name of the section in the binary
     * @return a representation of interesting data in the section
     * @throws IOException  if the section could not be read
     */
    abstract public Section getSection(String sectionName) throws IOException;

    /**
     * Write the facts computed by the analysis.
     */
    void writeFacts(Map<String, Set<XRef>> xrefs,
                    Map<String, List<SymbolInfo>> nameSymbols,
                    Map<String, List<SymbolInfo>> methodTypeSymbols) {

        // Write out symbol tables.
        Set<Long> dataPointers;
        try {
            dataPointers = getGlobalDataPointers();
        } catch (IOException ex) {
            System.err.println("Could not find global data pointers: " + ex.getMessage());
            dataPointers = new HashSet<>();
        }
        writeSymbolTable(NATIVE_NAME_CANDIDATE, nameSymbols, xrefs, dataPointers);
        writeSymbolTable(NATIVE_METHODTYPE_CANDIDATE, methodTypeSymbols, xrefs, dataPointers);

        // Write xrefs.
        xrefs.forEach((s, refs) -> refs.forEach(xr -> writeXRef(s, xr)));

        // Write entry points.
        entryPoints.forEach((addr, name) ->
                            dbc.add("NATIVE_LIB_ENTRY_POINT", lib, name, factAddr(addr)));
    }

    /**
     * Write the full symbol table. This method can also be extended
     * to merge information per symbol (for example, if different
     * entries contain complementary information).
     *
     * @param factsFile  the facts file to use for writing
     * @param symbols    the symbols table
     * @param xrefs      the string xrefs table
     * @param words      a set of machine words that might contain string pointers
     */
    private void writeSymbolTable(String factsFile,
                                  Map<String, List<SymbolInfo> > symbols,
                                  Map<String, Set<XRef>> xrefs,
                                  Collection<Long> words) {
        for (Map.Entry<String, List<SymbolInfo>> entry : symbols.entrySet()) {
            String symbol = entry.getKey();
            for (SymbolInfo si : entry.getValue()) {
                String offset = si.offset == null ? UNKNOWN_OFFSET : factAddr(si.offset);
                // If used in global data, set dummy function name for string.
                String func = si.function;
                if (func.equals(UNKNOWN_FUNCTION) && words != null && words.contains(si.offset))
                    func = "<<GLOBAL_DATA_SECTION>>";
                // Skip strings used in unknown locations if appropriate option is set.
                boolean skipString = onlyPreciseNativeStrings && func.equals(UNKNOWN_FUNCTION) && (xrefs.get(symbol) == null);
                if (!skipString)
                    dbc.add(factsFile, si.lib, func, symbol, offset);
            }
        }
    }

    private void writeXRef(String s, XRef xr) {
        dbc.add("NATIVE_XREF", s, lib, xr.function, factAddr(xr.codeAddr));
    }

    private String factAddr(long addr) {
        return String.valueOf(truncateTo32Bits ? ((addr << 32) >> 32) : addr);
    }

    /**
     * Auxiliary hex converter.
     */
    static long hexToLong(String s) {
        if (s.startsWith("0x"))
            s = s.substring(2);
        return Long.parseLong(s.trim(), 16);
    }

    /**
     * Auxiliary hex converter.
     */
    static int hexToInt(String s) {
        if (s.startsWith("0x"))
            s = s.substring(2);
        return Integer.parseInt(s.trim(), 16);
    }

    /**
     * Create an information table about this native code.
     * @param wordSize      the word size of the architecture
     * @param littleEndian  if the architecture is little-endian
     * @return              a dictionary containing the information
     */
    static protected Map<String, String> createNativeCodeInfo(Integer wordSize, Boolean littleEndian) {
        if (littleEndian == null) {
            final boolean DEFAULT_ENDIANNESS = true;
            System.err.println("WARNING: could not determine endianness, assuming littleEndian=" + DEFAULT_ENDIANNESS);
            littleEndian = DEFAULT_ENDIANNESS;
        }
        if (wordSize == null) {
            final int DEFAULT_WORDSIZE = 4;
            System.err.println("WARNING: could not determine word size, assuming wordSize=" + DEFAULT_WORDSIZE);
            wordSize = DEFAULT_WORDSIZE;
        }

        Map<String, String> info = new HashMap<>();
        info.put("endian", littleEndian ? "little" : "big");
        info.put("wordSize", wordSize + "");
        return info;
    }
}
