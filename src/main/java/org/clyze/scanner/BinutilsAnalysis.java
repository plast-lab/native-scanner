package org.clyze.scanner;

import java.util.*;
import java.util.regex.*;

/**
 * This class implements the analysis of the native scanner that uses
 * command-line tools (such as 'file', 'gdb', 'objdump' and 'nm').
 */
public class BinutilsAnalysis extends BinaryAnalysis {

    // Enable debug messages.
    private final static boolean debug = false;
    // Check for the presence of some special symbols (statistic).
    private final static boolean check = false;
    // Demangle C++ entry points.
    private final boolean demangle;
    // Environment variables needed to find external tools.
    private static final String envVarARMEABI = "ARMEABI_TOOLCHAIN";
    private static final String toolchainARMEABI = System.getenv(envVarARMEABI);
    private static final String envVarAARCH64 = "AARCH64_TOOLCHAIN";
    private static final String toolchainAARCH64 = System.getenv(envVarAARCH64);

    // The path to tool 'nm'.
    private String nmCmd;
    // The path to tool 'objdump'.
    private String objdumpCmd;

    BinutilsAnalysis(NativeDatabaseConsumer dbc, String lib,
                     boolean onlyPreciseNativeStrings, boolean truncateTo32Bits,
                     boolean demangle) {
        super(dbc, lib, onlyPreciseNativeStrings, truncateTo32Bits);

        this.demangle = demangle;

        this.nmCmd = "nm";
        this.objdumpCmd = "objdump";
        if (codeInfo.arch == Arch.ARMEABI) {
            if (toolchainARMEABI != null) {
                this.nmCmd = toolchainARMEABI + "/bin/nm";
                this.objdumpCmd = toolchainARMEABI + "/bin/objdump";
            } else
                System.err.println("No ARMEABI toolchain found, set " + envVarARMEABI + ". Using system nm/objdump.");
        } else if (codeInfo.arch == Arch.AARCH64) {
            if (toolchainAARCH64 != null) {
                this.nmCmd = toolchainAARCH64 + "/bin/nm";
                this.objdumpCmd = toolchainAARCH64 + "/bin/objdump";
            } else
                System.err.println("No AARCH64 toolchain found, set " + envVarAARCH64 + ". Using system nm/objdump.");
        }

        if (debug) {
            System.out.println("== Native scanner paths ==");
            System.out.println("arch = " + codeInfo.arch.name());
            System.out.println("nmCmd = " + nmCmd);
            System.out.println("objdumpCmd = " + objdumpCmd);
        }
    }

    @Override
    protected synchronized CodeInfo getNativeCodeInfo() {
        Boolean littleEndian = null;
        Integer wordSize = null;

        ProcessBuilder gdbBuilder = new ProcessBuilder("objdump", "-d", lib);
        final String FILE_FORMAT = "file format";

        for (String line : NativeScanner.runCommand(gdbBuilder)) {
            int idx = line.indexOf(FILE_FORMAT);
            if (idx != -1)
                for (String s : line.substring(idx + FILE_FORMAT.length()).split("\\s+"))
                    if (s.endsWith("pei-i386") || s.startsWith("elf32-")) {
                        littleEndian = true;
                        wordSize = 4;
                    } else if (s.endsWith("pei-x86-64") || s.endsWith("elf64-x86-64") || s.contains("Mach-O 64-bit x86-64")) {
                        littleEndian = true;
                        wordSize = 8;
                    } else if (s.endsWith("-little") || s.endsWith("-i386") || s.endsWith("-x86-64"))
                        littleEndian = true;
                    else if (s.endsWith("-big"))
                        littleEndian = false;
        }
        Map<String, String> metadata = createNativeCodeInfo(wordSize, littleEndian);
        return new CodeInfo(metadata, detectArch(), null, null);
    }

    @Override
    public int getWordSize() {
        return Integer.parseInt(codeInfo.metadata.get("wordSize"));
    }

    private Arch detectArch() {
        Arch libArch = null;
        try {
            String util = "file";
            ProcessBuilder pb = new ProcessBuilder(util, lib);
            for (String line : NativeScanner.runCommand(pb)) {
                if (line.contains("80386")) {
                    libArch = Arch.X86;
                    break;
                } else if (line.contains("x86-64")) {
                    libArch = Arch.X86_64;
                    break;
                } else if (line.contains("aarch64")) {
                    libArch = Arch.AARCH64;
                    break;
                } else if (line.contains("ARM") || line.contains("EABI")) {
                    libArch = Arch.ARMEABI;
                    break;
                } else if (line.contains("MIPS")) {
                    libArch = Arch.MIPS;
                    break;
                }
            }
            if (libArch != null)
                System.out.println("Detected architecture of " + lib + " is " + libArch);
            else {
                throw new RuntimeException("Could not determine architecture of " + lib + " using '" + util +"'.");
            }
        } catch (Exception ex) {
            System.err.println("Error: " + ex.getMessage());
            // For systems where 'file' is not available, use a heuristic.
            libArch = Arch.autodetectFromPathOrDefault(lib);
        }
        return libArch;
    }

    @Override
    public Map<String, Set<XRef>> findXRefs(Map<Long, String> binStrings) {
        System.out.println("Using binutils-based scanner to find strings in functions...");
        Arch libArch = codeInfo.arch;
        switch (libArch) {
            case X86:
                return findXRefsInX86(binStrings, lib);
            case X86_64:
                return findXRefsInX86_64(binStrings, lib);
            case AARCH64:
                return findXRefsInAARCH64(binStrings, lib);
            case ARMEABI:
                // Fuse results for both armeabi/armeabi-v7a.
                Map<String, Set<XRef>> eabi = findXRefsInARMEABI(binStrings, lib);
                Map<String, Set<XRef>> eabi7 = findXRefsInARMEABIv7a(binStrings, lib);
                return mergeMaps(eabi, eabi7);
        }
        System.err.println("Architecture not supported: " + libArch);
        return new HashMap<>();
    }

    /**
     * Merge two maps from keys to collections of values. Parameters may be mutated.
     */
    private static <T> Map<String, Set<T>> mergeMaps(Map<String, Set<T>> map1,
                                                     Map<String, Set<T>> map2) {
        for (Map.Entry<String, Set<T>> entry : map2.entrySet()) {
            String key = entry.getKey();
            Set<T> existing = map1.get(key);
            if (existing == null)
                map1.put(key, entry.getValue());
            else {
                Set<T> newValue = map1.get(key);
                newValue.addAll(entry.getValue());
                map1.put(key, newValue);
            }
        }
        return map1;
    }

    private void registerXRef(Map<String, Set<XRef>> xrefs, String str,
                              String function) {
        xrefs.computeIfAbsent(str, k -> new HashSet<>()).add(new XRef(lib, function, UNKNOWN_ADDRESS));
    }

    private Map<String, Set<XRef>> findXRefsInX86(Map<Long, String> foundStrings, String lib) {
        Long address;
        String function = null;
        Map<String, Set<XRef>> xrefs = new HashMap<>();
        Map<String, Long> registers = null;
        Pattern funPattern = Pattern.compile("^.*[<](.*)[>][:]$");
        Pattern addPattern = Pattern.compile("^\\s+([a-f0-9]+).*add\\s+[$][0][x]([a-f0-9]+)[,][%](.*)$");
        Pattern leaPattern = Pattern.compile("^.*lea\\s+(.)[0][x]([a-f0-9]+)[(][%](.*)[)].*$");
        Matcher m;

        ProcessBuilder gdbBuilder = new ProcessBuilder("objdump", "-j", ".text", "-d", lib);
        for (String line : NativeScanner.runCommand(gdbBuilder)) {
            m = funPattern.matcher(line);
            if (m.find()) {
                function = m.group(1);
                registers = new HashMap<>();
                continue;
            }

            m = addPattern.matcher(line);
            if (m.find()) {
                Long value = hexToLong(m.group(1)) + hexToLong(m.group(2));
                if (registers == null)
                    System.err.println("WARNING: no registers map initialized for 'add' pattern");
                else
                    registers.put(m.group(3), value);
                continue;
            }

            m = leaPattern.matcher(line);
            if (m.find()) {
                if (registers == null)
                    System.err.println("WARNING: no registers map initialized for 'lea' pattern");
                else if (registers.get(m.group(3)) != null) {
                    address = registers.get(m.group(3));
                    if (m.group(1).equals(" "))
                        address += hexToLong(m.group(2));
                    else if (m.group(1).equals("-"))
                        address -= hexToLong(m.group(2));

                    String str = foundStrings.get(address);
                    if (str != null) {
                        if (debug)
                            System.out.println("objdump disassemble string: '" + str + "' -> " + address);
                        registerXRef(xrefs, str, function);
                    }
                }
            }
        }

        return xrefs;
    }

    private Map<String, Set<XRef>> findXRefsInX86_64(Map<Long,String> foundStrings, String lib) {
        Map<String, Set<XRef>> xrefs = new HashMap<>();
        Pattern leaPattern = Pattern.compile("^.*lea.*[#]\\s[0][x]([a-f0-9]+)$");
        for (Map.Entry<Long, String> entry : entryPoints.entrySet()) {
            try {
                String function = entry.getValue();
                ProcessBuilder gdbBuilder = new ProcessBuilder("gdb", "-batch", "-ex", "disassemble " + function, lib);
                for (String line : NativeScanner.runCommand(gdbBuilder)) {
                    Matcher m = leaPattern.matcher(line);
                    if (m.find()) {
                        long address = hexToLong(m.group(1));
                        String str = foundStrings.get(address);
                        if (str != null) {
                            if (debug)
                                System.out.println("gdb disassemble string: '" + str + "' -> " + address);
                            registerXRef(xrefs, str, function);
                        }
                    }
                }
            } catch (RuntimeException ex) {
                System.err.println("Could not run gdb: " + ex.getMessage());
            }
        }
        return xrefs;
    }

    private Map<String, Set<XRef>> findXRefsInAARCH64(Map<Long,String> foundStrings, String lib) {
        Map<String, Set<XRef>> xrefs = new HashMap<>();
        Pattern adrpPattern = Pattern.compile("^.*adrp\\s+([a-z0-9]+)[,]\\s[0][x]([a-f0-9]+)$");
        Pattern addPattern = Pattern.compile("^.*add\\s+([a-z0-9]+)[,]\\s([a-z0-9]+)[,]\\s[#][0][x]([a-f0-9]+)$");
        Pattern movPattern = Pattern.compile("^.*mov\\s+([a-z0-9]+)[,]\\s([a-z0-9]+)$");
        Matcher m;
        Map<String,String> registers = new HashMap<>();
        for (Map.Entry<Long, String> entry : entryPoints.entrySet()) {
            try {
                String function = entry.getValue();
                ProcessBuilder gdbBuilder = new ProcessBuilder("gdb", "-batch", "-ex", "disassemble " + function, lib);
                for (String line : NativeScanner.runCommand(gdbBuilder)) {
                    m = adrpPattern.matcher(line);
                    if (m.find())
                        registers.put(m.group(1),m.group(2));
                    m = addPattern.matcher(line);
                    if (m.find() && registers.containsKey(m.group(2))) {
                        Long address = hexToLong(registers.get(m.group(2))) + hexToLong(m.group(3));
                        String str = foundStrings.get(address);
                        if (str != null) {
                            if (debug)
                                System.out.println("gdb disassemble string: '" + str + "' -> " + registers.get(m.group(1)));
                            registerXRef(xrefs, str, function);
                        }
                    }
                    m = movPattern.matcher(line);
                    if (m.find() && registers.containsKey(m.group(2)))
                        registers.put(m.group(1),registers.get(m.group(2)));
                }
            } catch (RuntimeException ex) {
                System.err.println("Could not run gdb: " + ex.getMessage());
            }
        }
        return xrefs;
    }

    private Map<String, Set<XRef>> findXRefsInARMEABIv7a(Map<Long, String> foundStrings, String lib) {
        String function = null;
        Pattern addrCodePattern = Pattern.compile("^\\s+([a-f0-9]+)[:]\\s+([a-f0-9]+)\\s?([a-f0-9]*)\\s+.*$");
        Pattern funPattern = Pattern.compile("^.*[<](.*)[>][:]$");
        Pattern insPattern = Pattern.compile("^\\s+([a-f0-9]+)[:]\\s+([a-f0-9]+)\\s?([a-f0-9]*)\\s+(\\w+[.]?\\w+)(.*)$");
        Pattern ldrPattern = Pattern.compile("^\\s+(\\w+)[,]\\s.*\\bpc.*[;]\\s[(]([a-f0-9]+).*$");
        Pattern ldrwPattern = Pattern.compile("^\\s+(\\w+)[,]\\s.*\\bpc.*[;]\\s([a-f0-9]+).*$");
        Pattern addPattern = Pattern.compile("^\\s(\\w+)[,]\\s(\\w+)[,]?\\s?(\\w*)(.*)$");
        Pattern movPattern = Pattern.compile("^\\s(\\w+)[,]\\s(\\w+)$");
        Matcher m;
        Map<String, String> registers = null, addressCode = new HashMap<>();
        Map<String, Set<XRef>> xrefs = new HashMap<>();

        ProcessBuilder objdumpBuilder = new ProcessBuilder(objdumpCmd, "-j", ".text", "-d", lib);
        for (String line : NativeScanner.runCommand(objdumpBuilder)) {
            m = addrCodePattern.matcher(line);
            if (m.find()) {
                if (!m.group(3).equals("")) {
                    String nextAddr = Integer.toHexString(Integer.parseInt(m.group(1),16)+Integer.parseInt("2",16));
                    addressCode.put(m.group(1),m.group(2));
                    addressCode.put(nextAddr,m.group(3));
                } else {
                    if (m.group(2).length()==4)
                        addressCode.put(m.group(1),m.group(2));
                    else {
                        addressCode.put(m.group(1),m.group(2).substring(0,4));
                        String nextAddr = Integer.toHexString(Integer.parseInt(m.group(1),16)+Integer.parseInt("2",16));
                        addressCode.put(nextAddr, m.group(2).substring(4,8));
                    }
                }
            }
        }
        for (String line : NativeScanner.runCommand(objdumpBuilder)) {
            m = funPattern.matcher(line);
            if (m.find()) {
                function = m.group(1);
                if (function.contains("@"))
                    function = function.substring(0, function.indexOf('@'));
                registers = new HashMap<>();
                if (debug)
                    System.out.println("new function " + function);
                continue;
            }
            try {
                m = insPattern.matcher(line);
                if (m.find()) {
                    if (registers == null) {
                        System.err.println("WARNING: no registers map initialized for 'ins' pattern");
                        continue;
                    }
                    registers.put("pc",m.group(1));
                    String instruction = m.group(5);
                    if (m.group(4).equals("ldr")) {
                        m = ldrPattern.matcher(instruction);
                        if (m.find()) {
                            String addr = m.group(2);
                            String nextAddr = Integer.toHexString(Integer.parseInt(addr,16)+Integer.parseInt("2",16));
                            String value;
                            if (addressCode.containsKey(nextAddr))
                                value = addressCode.get(nextAddr)+addressCode.get(addr);
                            else
                                value = addressCode.get(addr);
                            registers.put(m.group(1), value);
                        }
                    } else if (m.group(4).equals("ldr.w")) {
                        m = ldrwPattern.matcher(instruction);
                        if (m.find()) {
                            String addr = m.group(2);
                            String nextAddr = Integer.toHexString(Integer.parseInt(addr,16)+Integer.parseInt("2",16));
                            String value;
                            if (addressCode.containsKey(nextAddr))
                                value = addressCode.get(nextAddr)+addressCode.get(addr);
                            else
                                value = addressCode.get(addr);
                            registers.put(m.group(1), value);
                        }
                    } else if (m.group(4).contains("add") || m.group(4).equals("adr")) {
                        m = addPattern.matcher(instruction);
                        if (m.find() && registers.containsKey(m.group(1)) && registers.containsKey(m.group(2))) {
                            long address = hexToLong(registers.get(m.group(2)));
                            if (!m.group(3).equals("")) {
                                if (!registers.containsKey(m.group(3)))
                                    if (m.group(4).contains("#"))
                                        address += hexToLong(m.group(4).substring(m.group(4).lastIndexOf('#')));
                                    else
                                        continue;
                                address += hexToLong(registers.get(m.group(3)));
                            } else
                                address += hexToLong(registers.get(m.group(1)));
                            int len = Long.toHexString(address).length();
                            if (len>registers.get(m.group(1)).length() && len>registers.get(m.group(2)).length())
                                address = hexToLong(Long.toHexString(address).substring(1));
                            registers.put(m.group(1),Long.toHexString(address));
                            address += 4;
                            String str = foundStrings.get(address);
                            if (str != null) {
                                if (debug)
                                    System.out.println("objdump disassemble string: '" + str + "' -> " + registers.get(m.group(1)));
                                registerXRef(xrefs, str, function);
                            }
                        }
                    } else if (m.group(4).equals("mov")) {
                        m = movPattern.matcher(instruction);
                        if (m.find() && registers.containsKey(m.group(2)))
                            registers.put(m.group(1),registers.get(m.group(2)));
                    }
                }
            } catch (NumberFormatException ex) {
                System.err.println("Number format error '" + ex.getMessage() + "' in line: " + line);
            }
        }
        return xrefs;
    }

    private Map<String, Set<XRef>> findXRefsInARMEABI(Map<Long,String> foundStrings, String lib) {
        String function = null;
        Pattern funPattern = Pattern.compile(".*[<](.*)[>][:]$");
        Pattern insPattern = Pattern.compile("^\\s([a-f0-9]+)[:]\\s+([a-f0-9]+)\\s+[.]?(\\w+)(.*)$");
        Pattern ldrPattern = Pattern.compile("^\\s+(\\w+).*\\bpc.*[;]\\s([a-f0-9]+).*$");
        Pattern addPattern = Pattern.compile("^\\s+(\\w+)[,]\\s(\\w+)[,]\\s(\\w+)$");
        Pattern movPattern = Pattern.compile("^\\s+(\\w+)[,]\\s(\\w+)$");
        Matcher m;
        Map<String, String> registers = null, words = new HashMap<>();
        Map<String, Set<XRef>> xrefs = new HashMap<>();

        ProcessBuilder objdumpBuilder = new ProcessBuilder(objdumpCmd, "-j", ".text", "-d", lib);
        for (String line : NativeScanner.runCommand(objdumpBuilder)) {
            m = insPattern.matcher(line);
            if (m.find() && m.group(3).equals("word"))
                words.put(m.group(1),m.group(2));
        }
        for (String line : NativeScanner.runCommand(objdumpBuilder)) {
            m = funPattern.matcher(line);
            if (m.find()) {
                function = m.group(1);
                registers = new HashMap<>();
                //System.out.println("new function " + function);
                continue;
            }
            m = insPattern.matcher(line);
            if (m.find()) {
                if (registers == null) {
                    System.err.println("WARNING: no registers map initialized for 'ins' pattern");
                    continue;
                }
                registers.put("pc",m.group(1));
                String instruction = m.group(4);
                switch (m.group(3)) {
                    case "ldr":
                        m = ldrPattern.matcher(instruction);
                        if (m.find())
                            registers.put(m.group(1), words.get(m.group(2)));
                        break;
                    case "add":
                        m = addPattern.matcher(instruction);
                        if (m.find() && registers.containsKey(m.group(2)) && registers.containsKey(m.group(3))) {
                            try {
                                long address = hexToLong(registers.get(m.group(2))) + 8;
                                address += hexToLong(registers.get(m.group(3)));
                                String str = foundStrings.get(address);
                                if (str != null) {
                                    if (debug)
                                        System.out.println("objdump disassemble string: '" + str + "' -> " + registers.get(m.group(1)));
                                    registerXRef(xrefs, str, function);
                                }
                            } catch (NumberFormatException ex) {
                                System.err.println("Number format error '" + ex.getMessage() + "' in line: " + line);
                            }
                        }
                        break;
                    case "mov":
                        m = movPattern.matcher(instruction);
                        if (m.find() && registers.containsKey(m.group(2)))
                            registers.put(m.group(1), registers.get(m.group(2)));
                        break;
                }
            }
        }
        return xrefs;
    }

    @Override
    public Section getSection(String sectionName) {
        ProcessBuilder builder = new ProcessBuilder(objdumpCmd, "--headers", lib);
        List<String> lines = NativeScanner.runCommand(builder);
        for (String line : lines) {
            if (!line.contains(sectionName + " "))
                continue;
            String[] parts = line.trim().split("\\s+");
            if (parts.length < 7)
                continue;
            try {
                String secName = parts[1];
                System.out.println(secName + ": " + secName.trim().equals(sectionName));
                int size = BinaryAnalysis.hexToInt(parts[2]);
                long vma = BinaryAnalysis.hexToLong(parts[3]);
                long offset = BinaryAnalysis.hexToLong(parts[5]);
                return new Section(secName, lib, size, vma, offset);
            } catch (NumberFormatException ex) {
                if (debug)
                    System.err.println("NumberFormatException: " + ex.getMessage());
            }
        }
        System.err.println("WARNING: cannot find section " + sectionName + " from output:");
        for (String l : lines)
            System.out.println(l);
        return null;
    }

    // @Override
    // public SortedMap<Long, String> findStrings() {
    //     // // Legacy 'strings'-based scanner.
    //     // ProcessBuilder builderStrings = new ProcessBuilder("strings", lib);
    //     // Collection<String> methodTypes = new LinkedList<>();
    //     // Collection<String> names = new LinkedList<>();
    //     // for (String line : runCommand(builderStrings)) {
    //     //     if (debug)
    //     //         System.out.println("Checking string: '" + line + "'");
    //     //     if (isMethodType(line)) {
    //     //         if (debug)
    //     //             System.out.println("isMethodType('" + line + "') = true");
    //     //         methodTypes.add(line);
    //     //     } else if (isName(line))
    //     //         names.add(line);
    //     // }
    // }

    /**
     * Reads dynamic symbols from a library. Matching results may be
     * passed through c++filt.
     *
     * @param lib       the path to the dynamic library
     * @param demangle  if true, nm does the demangling, otherwise
     *                  we use c++filt
     * @return          a list of lines containing entry points
     */
    @SuppressWarnings("UseBulkOperation")
    private List<String> libSymbols(String lib, boolean demangle) {
        List<String> ids = new LinkedList<>();

        List<String> nmInvocation = new ArrayList<>();
        nmInvocation.add(nmCmd);
        nmInvocation.add("--defined-only");
        nmInvocation.add("--dynamic");
        if (demangle)
            nmInvocation.add("--demangle");
        nmInvocation.add(lib);

        ProcessBuilder nmBuilder = new ProcessBuilder(nmInvocation);
        for (String nmLine : NativeScanner.runCommand(nmBuilder)) {
            // if (!nmLine.contains("JNI"))
            //     continue;
            ids.add(nmLine);
            // // Call separate tool to do name demangling.
            // final String CPPFILT = "c++filt";
            // ProcessBuilder cppfilt = new ProcessBuilder(CPPFILT, "'" + nmLine + "'");
            // List<String> lines = NativeScanner.runCommand(cppfilt);
            // if (lines.size() == 1)
            //     ids.add(lines.get(0));
            // else {
            //     String out = lines.stream().map(Object::toString).collect(Collectors.joining(", "));
            //     System.err.println("Error: cannot process " + CPPFILT + " output: " + out);
            //     // Add original line.
            //     ids.add(nmLine);
            // }
        }
        return ids;
    }

    /**
     * Diagnostic: check for the presence of some special symbols in
     * the output of 'nm'.
     *
     * @param symbols  the text output of 'nm' that contains symbols
     * @param lib      the name of the library
     */
    private static void checkSymbols(Iterable<String> symbols, String lib) {
        boolean referencesGetMethodID = false;
        boolean referencesGetFieldID = false;
        for (String symbol : symbols) {
            if (debug)
                System.out.println("SYMBOL: " + symbol);
            if (symbol.contains("W _JNIEnv::GetMethodID("))
                referencesGetMethodID = true;
            else if (symbol.contains("W _JNIEnv::GetFieldID("))
                referencesGetFieldID = true;
        }

        if (referencesGetMethodID)
            System.out.println("Library references GetMethodID(): " + lib);
        if (referencesGetFieldID)
            System.out.println("Library references GetFieldID(): " + lib);
        if (!referencesGetMethodID && !referencesGetFieldID)
            System.out.println("Library seems to not contain interesting JNIEnv calls: " + lib);
    }

    @Override
    public void initEntryPoints() {
        try {
            // Demangling interacts poorly with libraries lacking
            // symbol tables and is thus turned off.
            List<String> symbols = libSymbols(lib, demangle);
            if (check)
                checkSymbols(symbols, lib);
            for (String symbol : symbols) {
                EntryPoint ep = EntryPoint.fromNmOutput(symbol);
                if (ep != null)
                    entryPoints.put(ep.addr, ep.name);
            }
        } catch (Exception ex) {
            System.err.println("ERROR: initEntryPoints(): " + ex.getMessage());
        }
    }
}

class EntryPoint {
    final String name;
    final Long addr;
    private EntryPoint(String name, Long addr) {
        this.name = name;
        this.addr = addr;
    }

    private static String trimAfter(String str, String delim) {
        int delimIdx = str.indexOf(delim);
        int endIdx = delimIdx < 0 ? str.length() : delimIdx;
        return str.substring(0, endIdx);
    }

    static EntryPoint fromNmOutput(String line) {
        String prefix = line;
        // Cut part after left parenthesis.
        prefix = trimAfter(prefix, "(");
        // Cut part after left bracket.
        prefix = trimAfter(prefix, "<");

        int lastSpaceIndex = prefix.lastIndexOf(" ");
        if (lastSpaceIndex == -1) {
            System.err.println("Error: cannot determine format of symbols output.");
            return null;
        } else if (prefix.charAt(lastSpaceIndex - 1) == 'U') {
            System.out.println("Ignoring line containing: " + prefix);
            return null;
        }

        int firstSpaceIndex = prefix.indexOf(" ");
        long addr = -1;
        if (firstSpaceIndex != -1) {
            String field = prefix.substring(0, firstSpaceIndex);
            if (field.charAt(0) == '\'')
                field = field.substring(1);
            try {
                addr = BinaryAnalysis.hexToLong(field);
            } catch (NumberFormatException ex) {
                System.err.println("Cannot compute address[0.." + firstSpaceIndex + "] for field: " + field);
            }
        }

        String method = prefix.substring(lastSpaceIndex + 1);
        if (method.startsWith("_JNIEnv::"))
            return null;
        else if (method.equals(""))
            throw new RuntimeException("Empty method! line = " + line + ", prefix = " + prefix);
        else
            return new EntryPoint(method, addr);
    }
}
