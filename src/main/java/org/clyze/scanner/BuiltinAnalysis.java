package org.clyze.scanner;

import org.clyze.scanner.parser.Elf;
import org.clyze.scanner.parser.MicrosoftPe;

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

    /**
     * Parse the native code binary to read its basic metadata.
     * @return the metadata
     */
    @Override
    protected CodeInfo getNativeCodeInfo() {
        System.out.println("-- getNativeCodeInfo()");
        Map<String, String> metadata = new HashMap<>();
        Arch libArch = null;
        int libSize = 0;
        SortedMap<Long, String> sectionAddresses = null;
        try {
            byte[] bytes = Files.readAllBytes(new File(lib).toPath());
            libSize = bytes.length;
            if (bytes[0] == 0x7f && bytes[1] == 'E' && bytes[2] == 'L' && bytes[3] == 'F') {
                System.out.println("Using built-in ELF mode for library: " + lib);
                Elf data = Elf.fromFile(lib);
                if (data.bits() == Elf.Bits.B32)
                    metadata.put("bits", "32");
                else if (data.bits() == Elf.Bits.B64)
                    metadata.put("bits", "64");
                Elf.Machine m = data.header().machine();
                switch (m) {
                    case AARCH64: libArch = Arch.AARCH64; break;
                    case ARM    : libArch = Arch.ARMEABI; break;
                    case X86    : libArch = Arch.X86    ; break;
                    case X86_64 : libArch = Arch.X86_64 ; break;
                    default     : System.err.println("WARNING: Unknown ELF architecture " + m.name());
                }
                sectionAddresses = new TreeMap<>();
                for (Elf.EndianElf.SectionHeader sh : data.header().sectionHeaders())
                    sectionAddresses.put(sh.addr(), sh.name());
            } else if (bytes[0] == 'M' && bytes[1] == 'Z') {
                System.out.println("Using built-in PE mode for library: " + lib);
                MicrosoftPe data = MicrosoftPe.fromFile(lib);
                MicrosoftPe.CoffHeader.MachineType mt = data.pe().coffHdr().machine();
                switch (mt) {
                    case I386 : libArch = Arch.X86    ; break;
                    case ARM  : libArch = Arch.ARMEABI; break;
                    case AMD64: libArch = Arch.X86_64 ; break;
                    case ARM64: libArch = Arch.AARCH64; break;
                    default     : System.err.println("WARNING: Unknown PE architecture " + mt.name());
                }
                if (libArch != null)
                    metadata.put("bits", "" + libArch.getBits());
                sectionAddresses = new TreeMap<>();
                for (MicrosoftPe.Section section : data.pe().sections())
                    sectionAddresses.put(section.pointerToRawData(), section.name());
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }

        if (metadata.isEmpty()) {
            System.err.println("Could not parse " + lib + ", using built-in heuristic.");
            metadata = createNativeCodeInfo(getWordSize(), libArch.isLittleEndian());
        }
        if (libArch == null)
            libArch = Arch.autodetectFromPathOrDefault(lib);

        return new CodeInfo(metadata, libArch, libSize, sectionAddresses);
    }

    @Override
    public int getWordSize() {
        return codeInfo.arch.getWordSize();
    }

    @Override
    public Section getSection(String sectionName) throws IOException {
        Long sectionAddr = null;
        System.out.println("sectionAddresses: " + codeInfo.sectionAddresses);
        for (Map.Entry<Long, String> entry : codeInfo.sectionAddresses.entrySet())
            if (entry.getValue().equals(sectionName)) {
                sectionAddr = entry.getKey();
                System.out.println("Found section " + sectionName + " at address " + sectionAddr);
            }

        if (sectionAddr != null) {
            // Use next section address to calculate section size (or end of file if last section).
            SortedMap<Long, String> nextAddrs = codeInfo.sectionAddresses.tailMap(sectionAddr + 1);
            long sectionBoundary = !nextAddrs.isEmpty() ? nextAddrs.firstKey() : codeInfo.libSize;
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
