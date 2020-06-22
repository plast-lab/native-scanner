package org.clyze.scanner;

import org.clyze.scanner.parser.Elf;
import org.clyze.scanner.parser.MicrosoftPe;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.*;

public class BuiltinAnalysis extends BinaryAnalysis {
    final boolean verbose = true;
    final SortedMap<Long, String> sectionAddresses = new TreeMap<>();
    final Map<String, String> nativeCodeInfo;
    final int libSize;

    BuiltinAnalysis(NativeDatabaseConsumer dbc, String lib) throws IOException {
        super(dbc, lib, false, true);

        byte[] bytes = Files.readAllBytes(new File(lib).toPath());
        this.nativeCodeInfo = parseNativeCode(bytes);
        this.libSize = bytes.length;
    }

    @Override
    public Map<String, Set<XRef>> findXRefs(Map<Long, String> binStrings) {
        if (verbose)
            System.err.println("WARNING: xrefs computation is not supported by the built-in analysis.");
        return new HashMap<>();
    }

    @Override
    public void initEntryPoints() {
        if (verbose)
            System.err.println("WARNING: the built-in analysis does not compute entry points.");
    }

    @Override
    public Arch autodetectArch() {
        if (libArch != null)
            return libArch;
        else
            return Arch.autodetectFromPath(lib);
    }

    /**
     * Parse the native code binary to read its basic metadata.
     * @param bytes   the binary file data
     * @return        the metadata
     * @see getNativeCodeInfo()
     */
    private Map<String, String> parseNativeCode(byte[] bytes) {
        Map<String, String> nativeCodeInfo = new HashMap<>();
        try {
            if (bytes[0] == 0x7f && bytes[1] == 'E' && bytes[2] == 'L' && bytes[3] == 'F') {
                System.out.println("Using built-in ELF mode for library: " + lib);
                Elf data = Elf.fromFile(lib);
                if (data.bits() == Elf.Bits.B32)
                    nativeCodeInfo.put("bits", "32");
                else if (data.bits() == Elf.Bits.B64)
                    nativeCodeInfo.put("bits", "64");
                switch (data.header().machine()) {
                    case AARCH64: libArch = Arch.AARCH64; break;
                    case ARM    : libArch = Arch.ARMEABI; break;
                    case X86    : libArch = Arch.X86    ; break;
                    case X86_64 : libArch = Arch.X86_64 ; break;
                }
                for (Elf.EndianElf.SectionHeader sh : data.header().sectionHeaders())
                    sectionAddresses.put(sh.addr(), sh.name());
            } else if (bytes[0] == 'M' && bytes[1] == 'Z') {
                System.out.println("Using built-in PE mode for library: " + lib);
                MicrosoftPe data = MicrosoftPe.fromFile(lib);
                switch (data.pe().coffHdr().machine()) {
                    case I386 : libArch = Arch.X86    ; break;
                    case ARM  : libArch = Arch.ARMEABI; break;
                    case AMD64: libArch = Arch.X86_64 ; break;
                    case ARM64: libArch = Arch.AARCH64; break;
                }
                if (libArch != null)
                    nativeCodeInfo.put("bits", "" + libArch.getBits());
                for (MicrosoftPe.Section section : data.pe().sections())
                    sectionAddresses.put(section.pointerToRawData(), section.name());
            }
        } catch (IOException ex) {
            System.err.println();
        }

        if (nativeCodeInfo.isEmpty()) {
            System.err.println("Could not parse " + lib + ", using built-in heuristic.");
            return createNativeCodeInfo(getWordSize(), libArch.isLittleEndian());
        } else
            return nativeCodeInfo;
    }

    @Override
    public Map<String, String> getNativeCodeInfo() {
        return nativeCodeInfo;
    }

    @Override
    public int getWordSize() {
        return libArch.getWordSize();
    }

    @Override
    public Section getSection(String sectionName) throws IOException {
        Long sectionAddr = null;
        for (Map.Entry<Long, String> entry : sectionAddresses.entrySet())
            if (entry.getValue().equals(sectionName)) {
                sectionAddr = entry.getKey();
                System.out.println("Found section " + sectionName + " at address " + sectionAddr);
            }

        if (sectionAddr != null) {
            // Use next section address to calculate section size (or end of file if last section).
            SortedMap<Long, String> nextAddrs = sectionAddresses.tailMap(sectionAddr + 1);
            long sectionBoundary = !nextAddrs.isEmpty() ? nextAddrs.firstKey() : this.libSize;
            System.out.println("sectionBoundary=" + sectionBoundary);
            return new Section(sectionName, lib, (int) (sectionBoundary - sectionAddr), 0, sectionAddr);
        } else
            return null;
    }

    // Don't process .data sections.
    @Override
    protected Set<Long> getGlobalDataPointers() throws IOException {
        return null;
    }
}
