package org.clyze.scanner;

import java.io.File;

/** The supported architectures. */
public enum Arch {
    X86(32, 4, true),
    X86_64(64, 8, true),
    AARCH64(64, 8, true),
    ARMEABI(32, 4, true),
    MIPS(32, 4, true);

    static final Arch DEFAULT_ARCH = AARCH64;
    final int wordSize;
    final boolean littleEndian;
    final int bits;

    Arch(int bits, int wordSize, boolean littleEndian) {
        this.bits = bits;
        this.wordSize = wordSize;
        this.littleEndian = littleEndian;
    }

    public int getWordSize() {
        return wordSize;
    }

    public boolean isLittleEndian() { return littleEndian; }

    public int getBits() { return bits; }

    /**
     * A simple heuristic to autodetect architectures from path names.
     * @param lib     the name of the library path
     * @return        the hardware architecture object
     */
    public static Arch autodetectFromPath(String lib) {
        if (lib.contains(File.separator + "armeabi-v7a" + File.separator))
            return Arch.ARMEABI;
        else if (lib.contains(File.separator + "arm64-v8a" + File.separator))
            return Arch.AARCH64;
        else if (lib.contains(File.separator + "x86" + File.separator))
            return Arch.X86;
        else if (lib.contains(File.separator + "x86_64" + File.separator))
            return Arch.X86_64;
        else if (lib.endsWith(".dll"))
            return Arch.X86_64;
        else {
            Arch ret = Arch.DEFAULT_ARCH;
            System.out.println("Could not determine architecture of " + lib + ", using default: " + ret);
            return ret;
        }
    }

}
