This repository hosts the native scanner, a library that searches for
strings in binary libraries, to inform static analyses. Read more about
this library in paper ["Identifying Java Calls in Native Code via Binary Scanning"](https://gfour.github.io/files/native-scanner-issta2020.pdf).

This library has the following modes:

* Radare2-based analysis: this mode uses the
  [Radare2](https://rada.re/) framework.

* [Binutils-based](https://www.gnu.org/software/binutils/) analysis:
  this mode assumes the existence of utilities such as `nm`,
  `objdump`, and `gdb`.

* Built-in mode: this mode uses no external tools but only supports a
  subset of the functionality for ELF and DLL files (using the [JElf
  parser](https://github.com/fornwall/jelf) and the [Kaitai Struct PE
  format](https://formats.kaitai.io/microsoft_pe/index.html)
  respectively). This mode should be used in applications where simple
  string detection is enough or in platforms where binutils/Radare2
  may not be available.

## Setup ##

### Standalone application ###

Install the application locally:

```
./gradlew installDist
```

The resulting binary is in `build/install/native-scanner/bin/native-scanner`.

### Library ###

Add the Bintray repository in your application build.gradle
and add the library as a dependency:

```
repositories {
  maven { url 'https://dl.bintray.com/gfour/plast-lab' }
  ...
}

dependencies {
  implementation 'org.clyze:native-scanner:0.5.7'
}
```

Note: this project also supports publishing to the local Maven
repository via `./gradlew publishToMavenLocal`.

### Binutils mode ###

This mode uses command-line programs available in your POSIX system,
such as `nm`, `objdump`, and `gdb`. This works for analyzing binaries
on a system with the same ABI (for example, x86 binaries on a x86
system).

To analyze ARM binaries on a x86 system, appropriate toolchains should
be used. Depending on the actual ARM target (32-bit or 64-bit), set
environment variables `ARMEABI_TOOLCHAIN` and `AARCH64_TOOLCHAIN` to
point to the correct toolchains (to generate such toolchains, consult
the [Android NDK documentation](https://developer.android.com/ndk/guides/standalone_toolchain)
or equivalent binary SDK distribution).

### Radare2 mode ###

This mode uses the [Radare2](https://rada.re/) reverse engineering framework for portability.

Setup:

1. Install Radare2 so that it is available in your PATH.

2. Install Python and [r2pipe](https://github.com/radareorg/radare2-r2pipe):

```
pip install r2pipe --user
```

## Use ##

For the standalone application, pass `--help` to see the available
options.

For the library, instantiate a NativeScanner object and a BinaryAnalysis
object, and use method `NativeScanner.scanBinaryCode()` to scan a native
library. To consume the results, implement interface `NativeDatabaseConsumer`.
See the standalone entry point `org.clyze.scanner.Main.main()` for an actual
piece of code using this library.
