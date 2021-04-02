package org.clyze.scanner;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.*;
import java.util.function.Consumer;

/**
 * This class implements the analysis of the native scanner that uses Radare2.
 */
public class RadareAnalysis extends BinaryAnalysis {

    private static final boolean debug = false;
    private static final String RADARE_PY_RESOURCE = "/radare.py";
    private static Path scriptPath = null;

    // Radare interface prefixes, see script for details.
    private static final String LOC_MARKER = "STRING_LOC:";
    private static final String STR_MARKER = "STRING:";
    private static final String SEC_MARKER = "SECTION:";
    private static final String EP_MARKER = "ENTRY_POINT:";
    private static final String INFO_MARKER = "INFO:";

    RadareAnalysis(NativeDatabaseConsumer dbc, String lib,
                   boolean onlyPreciseNativeStrings, boolean truncateTo32Bits) {
        super(dbc, lib, onlyPreciseNativeStrings, truncateTo32Bits);
    }

    /**
     * Extracts the bundled Radare2 Pytrhon script and returns its path.
     *
     * @return the path of the extracted Python script
     */
    private static synchronized Path getScript() {
        if (scriptPath != null)
            return scriptPath;
        try {
            Path tmpPath = Files.createTempFile("radare", ".py");
            tmpPath.toFile().deleteOnExit();
            InputStream resourceAsStream = BinaryAnalysis.class.getResourceAsStream(RADARE_PY_RESOURCE);
            Files.copy(resourceAsStream, tmpPath, StandardCopyOption.REPLACE_EXISTING);
            scriptPath = tmpPath;
            return scriptPath;
        } catch (IOException ex) {
            ex.printStackTrace();
            throw new RuntimeException("Error: could not extract " + RADARE_PY_RESOURCE);
        }

    }
    private static void runRadare(String... args) {
        checkRadareSupport();

        String script = getScript().toString();

        List<String> args0 = new LinkedList<>();
        args0.add("python3");
        args0.add(script);
        args0.addAll(Arrays.asList(args));

        ProcessBuilder radareBuilder = new ProcessBuilder(args0.toArray(new String[0]));
        System.out.println("Radare command line: " + radareBuilder.command());

        List<String> output = NativeScanner.runCommand(radareBuilder);
        for (String s : output) {
            if (s.contains("ImportError: No module named r2pipe"))
                throw new RuntimeException("Error: r2pipe is not installed");
            if (debug)
                System.out.println(s);
        }
    }

    /**
     * Use Radare (via external tool) to find strings. This method
     * also calls the supertype method to get a base set, which it
     * will then extend.
     *
     * @return a map of address-to-string entries
     */
    @Override
    public SortedMap<Long, String> findStrings() {
        System.out.println("Finding strings with Radare2...");
        SortedMap<Long, String> strings = super.findStrings();
        try {
            File outFile = File.createTempFile("strings-out", ".txt");
            outFile.deleteOnExit();
            runRadare("strings", lib, outFile.getCanonicalPath());
            Consumer<ArrayList<String>> proc = (l -> {
                    String vAddrStr = l.get(0);
                    String s = l.get(1);
                    long vAddr = UNKNOWN_ADDRESS;
                    try {
                        vAddr = hexToLong(vAddrStr);
                    } catch (NumberFormatException ex) {
                        System.err.println("WARNING: error parsing string address: " + vAddrStr);
                    }
                    strings.put(vAddr, s);
                });
            processMultiColumnFile(outFile, STR_MARKER, 2, proc);
        } catch (IOException ex) {
            ex.printStackTrace();
            throw new RuntimeException("Could not read strings.");
        }
        return strings;
    }

    /**
     * Given a table of strings, returns a map that connects each
     * string with the functions that reference it.
     *
     * @param binStrings  a table of strings
     * @return            a map from strings to (sets of) functions
     */
    @Override
    public Map<String, Set<XRef>> findXRefs(Map<Long, String> binStrings) {
        System.out.println("Finding string xrefs with Radare2 in: " + lib);

        Map<String, Set<XRef>> xrefs = new HashMap<>();
        try {
            File stringsFile = File.createTempFile("strings", ".txt");
            stringsFile.deleteOnExit();
            try (FileWriter writer = new FileWriter(stringsFile)) {
                binStrings.forEach((addr, s) -> writeString(addr, s, writer));
            }
            File outFile = File.createTempFile("string-xrefs-out", ".txt");
            outFile.deleteOnExit();
            runRadare("xrefs", lib, stringsFile.getCanonicalPath(), outFile.getCanonicalPath());
            processMultiColumnFile(outFile, LOC_MARKER, 3, l -> regXRef(l, xrefs));
        } catch (IOException ex) {
            ex.printStackTrace();
            throw new RuntimeException("Could not find string xrefs: " + ex.getMessage());
        }
        return xrefs;
    }

    private void writeString(Long addr, String s, FileWriter writer) {
        // Omit huge strings.
        if (s.length() > 300)
            return;
        // Omit non-Latin strings, since the Python interface
        // cannot support some UTF-8 codes.
        boolean nonLatin = false;
        for (char c : s.toCharArray())
            if (Character.UnicodeBlock.of(c) != Character.UnicodeBlock.BASIC_LATIN) {
                nonLatin = true;
                break;
            }
        if (!nonLatin)
            try {
                writer.write(addr + " " + s + "\n");
            } catch (IOException ex) {
                ex.printStackTrace();
            }
    }

    private void regXRef(ArrayList<String> l, Map<String, Set<XRef>> xrefs) {
        String func = l.get(0);
        String codeAddrStr = l.get(1);
        String s = l.get(2);
        if (func.equals("(nofunc)"))
            func = UNKNOWN_FUNCTION;
        long codeAddr = UNKNOWN_ADDRESS;
        try {
            codeAddr = hexToLong(codeAddrStr);
        } catch (NumberFormatException ex) {
            System.err.println("WARNING: error parsing xref address: " + codeAddrStr);
        }
        xrefs.computeIfAbsent(s, k -> new HashSet<>()).add(new XRef(lib, func, codeAddr));
    }

    @Override
    public Section getSection(String sectionName) throws IOException {
        File outFile = File.createTempFile("sections-out", ".txt");
        outFile.deleteOnExit();

        runRadare("sections", lib, outFile.getCanonicalPath());

        // Box to use for returning value from section processor.
        Section[] sec = new Section[1];

        Consumer<ArrayList<String>> proc = (l -> {
                String secName = l.get(0);
                if (!secName.equals(sectionName))
                    return;
                String vAddrStr = l.get(1);
                String sizeStr = l.get(2);
                String offsetStr = l.get(3);
                long vAddr;
                int size;
                long offset;
                try {
                    vAddr = hexToLong(vAddrStr);
                    size = Integer.parseInt(sizeStr.trim());
                    offset = hexToLong(offsetStr);
                    sec[0] = new Section(secName, lib, size, vAddr, offset);
                } catch (NumberFormatException ex) {
                    System.err.println("WARNING: error parsing section: " + secName + " " + vAddrStr + " " + sizeStr);
                }
            });
        processMultiColumnFile(outFile, SEC_MARKER, 4, proc);
        return sec[0];
    }

    @Override
    public void initEntryPoints() {
        try {
            File outFile = File.createTempFile("sections-out", ".txt");
            outFile.deleteOnExit();
            runRadare("epoints", lib, outFile.getCanonicalPath());
            Consumer<ArrayList<String>> proc = (l -> {
                String vAddrStr = l.get(0);
                String name = l.get(1);
                long vAddr;
                try {
                    vAddr = hexToLong(vAddrStr);
                    entryPoints.put(vAddr, name);
                } catch (NumberFormatException ex) {
                    System.err.println("WARNING: error parsing section: " + vAddrStr + " " + name);
                }
            });
            processMultiColumnFile(outFile, EP_MARKER, 2, proc);
        } catch (IOException ex) {
            ex.printStackTrace();
            throw new RuntimeException("Could not read entry points.");
        }
    }

    @Override
    protected synchronized CodeInfo getNativeCodeInfo() {
        Map<String, String> metadata = new HashMap<>();
        try {
            File outFile = File.createTempFile("info-out", ".txt");
            outFile.deleteOnExit();
            runRadare("info", lib, outFile.getCanonicalPath());
            Consumer<ArrayList<String>> proc = (l -> {
                    String key = l.get(0);
                    String value = l.get(1);
                    metadata.put(key.trim(), value.trim());
                });
            processMultiColumnFile(outFile, INFO_MARKER, 2, proc);
        } catch (IOException ex) {
            ex.printStackTrace();
            throw new RuntimeException("Could not read native code properties.");
        }
        return new CodeInfo(metadata, detectArch(metadata), null, null);
    }

    @Override
    public boolean isLittleEndian() {
        return codeInfo.metadata.get("endian").equals("little");
    }

    @Override
    public int getWordSize() {
        Map<String, String> info = codeInfo.metadata;
        String c = info.get("class");
        String bits = info.get("bits");
        if (c.equals("ELF32"))
            return 4;
        else if (c.equals("PE32") && bits.equals("32"))
            return 4;
        else if (c.equals("PE32+") && bits.equals("64"))
            return 8;
        else {
            int ws = Integer.parseInt(bits) / 8;
            System.out.println("Unknown class " + c + ", assuming word size = " + ws);
            return ws;
        }
    }

    private Arch detectArch(Map<String, String> info) {
        String arch = info.get("arch");
        String bits = info.get("bits");

        Arch libArch = null;
        if (arch != null && bits != null) {
            if (arch.equals("x86") && bits.equals("32"))
                libArch = Arch.X86;
            else if (arch.equals("x86") && bits.equals("64"))
                libArch = Arch.X86_64;
            else if (arch.equals("arm") && bits.equals("64"))
                libArch = Arch.AARCH64;
            else if (arch.equals("arm") && (bits.equals("16") || bits.equals("32")))
                libArch = Arch.ARMEABI;
            else if (arch.equals("mips"))
                libArch = Arch.MIPS;
        }

        if (libArch == null) {
            System.out.println("Could not determine architecture of " + lib + ", using default: " + Arch.DEFAULT_ARCH.name());
            return Arch.DEFAULT_ARCH;
        }

        System.out.println("Detected architecture of " + lib + " is " + libArch.name());
        return libArch;
    }

    private void processMultiColumnFile(File f, String prefix, int numColumns,
                                        Consumer<ArrayList<String>> proc) throws IOException {
        for (String line : Files.readAllLines(f.toPath())) {
            if (debug)
                System.out.println(line);
            boolean badLine = false;
            if (line.startsWith(prefix)) {
                line = line.substring(prefix.length());
                ArrayList<String> values = new ArrayList<>(numColumns);
                // Split first (n-1) values, consider the rest the last value.
                for (int i = 0; i < numColumns - 1; i++) {
                    int tabIdx = line.indexOf("\t");
                    if (tabIdx > 0) {
                        values.add(line.substring(0, tabIdx));
                        line = line.substring(tabIdx+1);
                    } else {
                        System.err.println("WARNING: malformed line: " + line);
                        badLine = true;
                        break;
                    }
                }
                if (!badLine) {
                    // Add last value.
                    values.add(line);
                    proc.accept(values);
                }
            }
        }
    }

    private static void checkRadareSupport() {
        ProcessBuilder radareBuilder = new ProcessBuilder(new String[] { "r2", "-v" });
        System.out.println("Radare command line: " + radareBuilder.command());

        List<String> output = NativeScanner.runCommand(radareBuilder);
        if (output.size() > 0) {
            String first = output.get(0);
            String PREFIX = "radare2 ";
            int PREFIX_LEN = PREFIX.length();
            if (first.startsWith(PREFIX)) {
                String ver = first.substring(PREFIX_LEN, first.indexOf(" ", PREFIX_LEN));
                if (!ver.startsWith("3."))
                    System.out.println("WARNING: Radare2 version != 3 may not be supported.");
            }
        }
    }
}
