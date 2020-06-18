package org.clyze.scanner;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

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
    public Arch autodetectArch() { return Arch.autodetectFromPath(lib); }

    @Override
    public Map<String, String> getNativeCodeInfo() { return createNativeCodeInfo(getWordSize(), libArch.isLittleEndian()); }

    @Override
    public int getWordSize() {
        return libArch.getWordSize();
    }

    @Override
    public Section getSection(String sectionName) throws IOException {
        return null;
    }
}
