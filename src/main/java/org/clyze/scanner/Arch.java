package org.clyze.scanner;

// The supported architectures.
enum Arch {
    X86, X86_64, AARCH64, ARMEABI, MIPS;

    static final Arch DEFAULT_ARCH = AARCH64;
}
