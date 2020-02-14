This repository hosts the native scanner, a library that searches for
strings in binary libraries, to inform static analyses.

This library has the following modes:

* [binutils-based](https://www.gnu.org/software/binutils/) analysis: this assumes the existence of utilities
  such as `nm`, `objdump`, and `gdb`.

* Radare2-based analysis: this assumes the existence of a
  [Radare2](https://rada.re/) installation with
  [r2pipe](https://github.com/radareorg/radare2-r2pipe).

## Setup ##

### Standalone application ###

Install the application locally:

```
./gradlew installDist
```

The resulting binary is in `build/install/native-scanner/bin/native-scanner`.

### Library ###

Run the following command to publish the native scanner to the local
Maven repository:

```
./gradlew publishToMavenLocal
```

Add `mavenLocal()` as a repository in your application build.gradle
and add dependency:

```
repositories {
  mavenLocal()
  ...
}

dependencies {
  implementation 'org.clyze:native-scanner:1.0.0'
}
```

### Binutils mode ###

This mode uses command-line programs available in your POSIX system,
such as `nm`, `objdump`, and `gdb`. This works for analyzing binaries
on a system with the same ABI (for example, x86 binaries on a x86
system).

To analyze ARM binaries on a x86 system, appropriate toolchains should
be used. Environment variables `ARMEABI_TOOLCHAIN` and
`AARCH64_TOOLCHAIN` should point to appropriate toolchains (to
generate such toolchains, consult the [Android NDK
documentation](https://developer.android.com/ndk/guides/standalone_toolchain)
or equivalent binary SDK distribution).

### Radare2 mode ###

For Radare2 mode, set environment variable RADARE_PY_SCRIPT to
point to the directory containing radare.py.

## Use ##

For the standalone application, pass `--help` to see the available
options. The results will be printed on screen.

For the library, instantiate a NativeScanner object and use its
`scanLib()` method to scan a native library. To consume the results,
implement interface `DatabaseConsumer`. See the standalone entry point
org.clyze.scanner.Main.main() for an actual piece of code using this
library.
