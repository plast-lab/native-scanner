package org.clyze.scanner;

import org.clyze.scanner.parser.Elf;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.*;

public class BuiltinAnalysis extends BinaryAnalysis {
    final boolean verbose = true;

    BuiltinAnalysis(NativeDatabaseConsumer dbc, String lib) {
        super(dbc, lib, false, true);
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
        return Arch.autodetectFromPath(lib);
    }

    @Override
    public Map<String, String> getNativeCodeInfo() {
        return createNativeCodeInfo(getWordSize(), libArch.isLittleEndian());
    }

    @Override
    public int getWordSize() {
        return libArch.getWordSize();
    }

    @Override
    public Section getSection(String sectionName) throws IOException {
        byte[] bytes = Files.readAllBytes(new File(lib).toPath());
        if (bytes[0] == 0x7f && bytes[1] == 'E' && bytes[2] == 'L' && bytes[3] == 'F') {
            System.out.println("Using built-in ELF mode for library: " + lib);
            Elf data = Elf.fromFile(lib);
            SortedSet<Long> sectionAddresses = new TreeSet<>();
            Long sectionAddr = null;
            for (Elf.EndianElf.SectionHeader sh : data.header().sectionHeaders()) {
                long addr = sh.addr();
                sectionAddresses.add(addr);
                if (sh.name().equals(sectionName)) {
                    sectionAddr = addr;
                    System.out.println("Found section " + sectionName + " at address " + sectionAddr);
                }
            }
            if (sectionAddr != null) {
                // Use next section address to calculate section size (or end of file if last section).
                SortedSet<Long> nextAddrs = sectionAddresses.tailSet(sectionAddr + 1);
                long sectionBoundary = !nextAddrs.isEmpty() ? nextAddrs.first() : bytes.length;
                System.out.println("sectionBoundary=" + sectionBoundary);
                return new Section(sectionName, lib, (int) (sectionBoundary - sectionAddr), 0, sectionAddr);
            }
        }
        return null;
    }

    // Don't process .data sections.
    @Override
    protected Set<Long> getGlobalDataPointers() throws IOException {
        return null;
    }
}
