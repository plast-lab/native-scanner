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
class RadareAnalysis extends BinaryAnalysis {

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
            InputStream resourceAsStream = BinaryAnalysis.class.getResourceAsStream(RADARE_PY_RESOURCE);
            Files.copy(resourceAsStream, tmpPath, StandardCopyOption.REPLACE_EXISTING);
            scriptPath = tmpPath;
            return scriptPath;
        } catch (IOException ex) {
            ex.printStackTrace();
            throw new RuntimeException("Error: could not extract " + RADARE_PY_RESOURCE);
        }

    }
    private static void runRadare(String... args) throws IOException {
        String script = getScript().toString();

        List<String> args0 = new LinkedList<>();
        args0.add("python");
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
    public SortedMap<Long, String> findStrings() throws IOException {
        System.out.println("Finding strings with Radare2...");
        SortedMap<Long, String> strings = super.findStrings();

        File outFile = File.createTempFile("strings-out", ".txt");

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
    Map<String, Set<XRef>> findXRefs(Map<Long, String> binStrings) throws IOException {
        System.out.println("Finding string xrefs with Radare2 in: " + lib);

        Map<String, Set<XRef>> xrefs = new HashMap<>();

        File stringsFile = File.createTempFile("strings", ".txt");
        try (FileWriter writer = new FileWriter(stringsFile)) {
            binStrings.forEach((addr, s) -> {
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
                });
        }

        File outFile = File.createTempFile("string-xrefs-out", ".txt");

        runRadare("xrefs", lib, stringsFile.getCanonicalPath(), outFile.getCanonicalPath());

        Consumer<ArrayList<String>> proc = (l -> {
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
            });
        processMultiColumnFile(outFile, LOC_MARKER, 3, proc);
        return xrefs;
    }

    @Override
    public Section getSection(String sectionName) throws IOException {
        File outFile = File.createTempFile("sections-out", ".txt");

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
                    sec[0] = new Section(secName, arch, lib, size, vAddr, offset);
                } catch (NumberFormatException ex) {
                    System.err.println("WARNING: error parsing section: " + secName + " " + vAddrStr + " " + sizeStr);
                }
            });
        processMultiColumnFile(outFile, SEC_MARKER, 4, proc);
        return sec[0];
    }

    @Override
    public void initEntryPoints() throws IOException {
        File outFile = File.createTempFile("sections-out", ".txt");

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
    }

    @Override
    protected Arch autodetectArch() throws IOException {
        File outFile = File.createTempFile("info-out", ".txt");

        runRadare("info", lib, outFile.getCanonicalPath());

        Map<String, String> info = new HashMap<>();
        Consumer<ArrayList<String>> proc = (l -> {
                String key = l.get(0);
                String value = l.get(1);
                info.put(key.trim(), value.trim());
            });
        processMultiColumnFile(outFile, INFO_MARKER, 2, proc);

        String arch = info.get("arch");
        String bits = info.get("bits");

        Arch ret = null;
        if (arch.equals("x86") && bits.equals("32"))
            ret = Arch.X86;
        else if (arch.equals("x86") && bits.equals("64"))
            ret = Arch.X86_64;
        else if (arch.equals("arm") && bits.equals("64"))
            ret = Arch.AARCH64;
        else if (arch.equals("arm") && (bits.equals("16") || bits.equals("32")))
            ret = Arch.ARMEABI;
        else if (arch.equals("mips"))
            ret = Arch.MIPS;

        if (bits == null || ret == null) {
            ret = Arch.DEFAULT_ARCH;
            System.out.println("Could not determine architecture of " + lib + ", using default: " + ret);
        } else
            System.out.println("Detected architecture of " + lib + " is " + arch);

        return ret;
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
}
