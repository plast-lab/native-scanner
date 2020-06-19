// This is a generated file! Please edit source .ksy file and use kaitai-struct-compiler to rebuild

package org.clyze.scanner.parser;

import io.kaitai.struct.ByteBufferKaitaiStream;
import io.kaitai.struct.KaitaiStruct;
import io.kaitai.struct.KaitaiStream;
import java.io.IOException;
import java.util.Map;
import java.util.HashMap;
import java.nio.charset.Charset;
import java.util.ArrayList;

/**
 * @see <a href="https://sourceware.org/git/?p=glibc.git;a=blob;f=elf/elf.h;hb=HEAD">Source</a>
 */
public class Elf extends KaitaiStruct {
    public static Elf fromFile(String fileName) throws IOException {
        return new Elf(new ByteBufferKaitaiStream(fileName));
    }

    public enum Endian {
        LE(1),
        BE(2);

        private final long id;
        Endian(long id) { this.id = id; }
        public long id() { return id; }
        private static final Map<Long, Endian> byId = new HashMap<Long, Endian>(2);
        static {
            for (Endian e : Endian.values())
                byId.put(e.id(), e);
        }
        public static Endian byId(long id) { return byId.get(id); }
    }

    public enum ShType {
        NULL_TYPE(0),
        PROGBITS(1),
        SYMTAB(2),
        STRTAB(3),
        RELA(4),
        HASH(5),
        DYNAMIC(6),
        NOTE(7),
        NOBITS(8),
        REL(9),
        SHLIB(10),
        DYNSYM(11),
        INIT_ARRAY(14),
        FINI_ARRAY(15),
        PREINIT_ARRAY(16),
        GROUP(17),
        SYMTAB_SHNDX(18),
        SUNW_CAPCHAIN(1879048175),
        SUNW_CAPINFO(1879048176),
        SUNW_SYMSORT(1879048177),
        SUNW_TLSSORT(1879048178),
        SUNW_LDYNSYM(1879048179),
        SUNW_DOF(1879048180),
        SUNW_CAP(1879048181),
        SUNW_SIGNATURE(1879048182),
        SUNW_ANNOTATE(1879048183),
        SUNW_DEBUGSTR(1879048184),
        SUNW_DEBUG(1879048185),
        SUNW_MOVE(1879048186),
        SUNW_COMDAT(1879048187),
        SUNW_SYMINFO(1879048188),
        SUNW_VERDEF(1879048189),
        SUNW_VERNEED(1879048190),
        SUNW_VERSYM(1879048191),
        SPARC_GOTDATA(1879048192),
        AMD64_UNWIND(1879048193),
        ARM_PREEMPTMAP(1879048194),
        ARM_ATTRIBUTES(1879048195);

        private final long id;
        ShType(long id) { this.id = id; }
        public long id() { return id; }
        private static final Map<Long, ShType> byId = new HashMap<Long, ShType>(38);
        static {
            for (ShType e : ShType.values())
                byId.put(e.id(), e);
        }
        public static ShType byId(long id) { return byId.get(id); }
    }

    public enum OsAbi {
        SYSTEM_V(0),
        HP_UX(1),
        NETBSD(2),
        GNU(3),
        SOLARIS(6),
        AIX(7),
        IRIX(8),
        FREEBSD(9),
        TRU64(10),
        MODESTO(11),
        OPENBSD(12),
        OPENVMS(13),
        NSK(14),
        AROS(15),
        FENIXOS(16),
        CLOUDABI(17),
        OPENVOS(18);

        private final long id;
        OsAbi(long id) { this.id = id; }
        public long id() { return id; }
        private static final Map<Long, OsAbi> byId = new HashMap<Long, OsAbi>(17);
        static {
            for (OsAbi e : OsAbi.values())
                byId.put(e.id(), e);
        }
        public static OsAbi byId(long id) { return byId.get(id); }
    }

    public enum Machine {
        NOT_SET(0),
        SPARC(2),
        X86(3),
        MIPS(8),
        POWERPC(20),
        ARM(40),
        SUPERH(42),
        IA_64(50),
        X86_64(62),
        AARCH64(183),
        RISCV(243),
        BPF(247);

        private final long id;
        Machine(long id) { this.id = id; }
        public long id() { return id; }
        private static final Map<Long, Machine> byId = new HashMap<Long, Machine>(12);
        static {
            for (Machine e : Machine.values())
                byId.put(e.id(), e);
        }
        public static Machine byId(long id) { return byId.get(id); }
    }

    public enum DynamicArrayTags {
        NULL(0),
        NEEDED(1),
        PLTRELSZ(2),
        PLTGOT(3),
        HASH(4),
        STRTAB(5),
        SYMTAB(6),
        RELA(7),
        RELASZ(8),
        RELAENT(9),
        STRSZ(10),
        SYMENT(11),
        INIT(12),
        FINI(13),
        SONAME(14),
        RPATH(15),
        SYMBOLIC(16),
        REL(17),
        RELSZ(18),
        RELENT(19),
        PLTREL(20),
        DEBUG(21),
        TEXTREL(22),
        JMPREL(23),
        BIND_NOW(24),
        INIT_ARRAY(25),
        FINI_ARRAY(26),
        INIT_ARRAYSZ(27),
        FINI_ARRAYSZ(28),
        RUNPATH(29),
        FLAGS(30),
        PREINIT_ARRAY(32),
        PREINIT_ARRAYSZ(33),
        MAXPOSTAGS(34),
        SUNW_AUXILIARY(1610612749),
        SUNW_FILTER(1610612750),
        SUNW_CAP(1610612752),
        SUNW_SYMTAB(1610612753),
        SUNW_SYMSZ(1610612754),
        SUNW_SORTENT(1610612755),
        SUNW_SYMSORT(1610612756),
        SUNW_SYMSORTSZ(1610612757),
        SUNW_TLSSORT(1610612758),
        SUNW_TLSSORTSZ(1610612759),
        SUNW_CAPINFO(1610612760),
        SUNW_STRPAD(1610612761),
        SUNW_CAPCHAIN(1610612762),
        SUNW_LDMACH(1610612763),
        SUNW_CAPCHAINENT(1610612765),
        SUNW_CAPCHAINSZ(1610612767),
        GNU_PRELINKED(1879047669),
        GNU_CONFLICTSZ(1879047670),
        GNU_LIBLISTSZ(1879047671),
        CHECKSUM(1879047672),
        PLTPADSZ(1879047673),
        MOVEENT(1879047674),
        MOVESZ(1879047675),
        FEATURE_1(1879047676),
        POSFLAG_1(1879047677),
        SYMINSZ(1879047678),
        SYMINENT(1879047679),
        GNU_HASH(1879047925),
        TLSDESC_PLT(1879047926),
        TLSDESC_GOT(1879047927),
        GNU_CONFLICT(1879047928),
        GNU_LIBLIST(1879047929),
        CONFIG(1879047930),
        DEPAUDIT(1879047931),
        AUDIT(1879047932),
        PLTPAD(1879047933),
        MOVETAB(1879047934),
        SYMINFO(1879047935),
        VERSYM(1879048176),
        RELACOUNT(1879048185),
        RELCOUNT(1879048186),
        FLAGS_1(1879048187),
        VERDEF(1879048188),
        VERDEFNUM(1879048189),
        VERNEED(1879048190),
        VERNEEDNUM(1879048191),
        SPARC_REGISTER(1879048193),
        AUXILIARY(2147483645),
        USED(2147483646),
        FILTER(2147483647);

        private final long id;
        DynamicArrayTags(long id) { this.id = id; }
        public long id() { return id; }
        private static final Map<Long, DynamicArrayTags> byId = new HashMap<Long, DynamicArrayTags>(84);
        static {
            for (DynamicArrayTags e : DynamicArrayTags.values())
                byId.put(e.id(), e);
        }
        public static DynamicArrayTags byId(long id) { return byId.get(id); }
    }

    public enum Bits {
        B32(1),
        B64(2);

        private final long id;
        Bits(long id) { this.id = id; }
        public long id() { return id; }
        private static final Map<Long, Bits> byId = new HashMap<Long, Bits>(2);
        static {
            for (Bits e : Bits.values())
                byId.put(e.id(), e);
        }
        public static Bits byId(long id) { return byId.get(id); }
    }

    public enum PhType {
        NULL_TYPE(0),
        LOAD(1),
        DYNAMIC(2),
        INTERP(3),
        NOTE(4),
        SHLIB(5),
        PHDR(6),
        TLS(7),
        GNU_EH_FRAME(1685382480),
        GNU_STACK(1685382481),
        GNU_RELRO(1685382482),
        PAX_FLAGS(1694766464),
        HIOS(1879048191),
        ARM_EXIDX(1879048193);

        private final long id;
        PhType(long id) { this.id = id; }
        public long id() { return id; }
        private static final Map<Long, PhType> byId = new HashMap<Long, PhType>(14);
        static {
            for (PhType e : PhType.values())
                byId.put(e.id(), e);
        }
        public static PhType byId(long id) { return byId.get(id); }
    }

    public enum ObjType {
        RELOCATABLE(1),
        EXECUTABLE(2),
        SHARED(3),
        CORE(4);

        private final long id;
        ObjType(long id) { this.id = id; }
        public long id() { return id; }
        private static final Map<Long, ObjType> byId = new HashMap<Long, ObjType>(4);
        static {
            for (ObjType e : ObjType.values())
                byId.put(e.id(), e);
        }
        public static ObjType byId(long id) { return byId.get(id); }
    }

    public Elf(KaitaiStream _io) {
        this(_io, null, null);
    }

    public Elf(KaitaiStream _io, KaitaiStruct _parent) {
        this(_io, _parent, null);
    }

    public Elf(KaitaiStream _io, KaitaiStruct _parent, Elf _root) {
        super(_io);
        this._parent = _parent;
        this._root = _root == null ? this : _root;
        _read();
    }
    private void _read() {
        this.magic = this._io.ensureFixedContents(new byte[] { 127, 69, 76, 70 });
        this.bits = Bits.byId(this._io.readU1());
        this.endian = Endian.byId(this._io.readU1());
        this.eiVersion = this._io.readU1();
        this.abi = OsAbi.byId(this._io.readU1());
        this.abiVersion = this._io.readU1();
        this.pad = this._io.readBytes(7);
        this.header = new EndianElf(this._io, this, _root);
    }
    public static class PhdrTypeFlags extends KaitaiStruct {

        public PhdrTypeFlags(KaitaiStream _io, long value) {
            this(_io, null, null, value);
        }

        public PhdrTypeFlags(KaitaiStream _io, Elf.EndianElf.ProgramHeader _parent, long value) {
            this(_io, _parent, null, value);
        }

        public PhdrTypeFlags(KaitaiStream _io, Elf.EndianElf.ProgramHeader _parent, Elf _root, long value) {
            super(_io);
            this._parent = _parent;
            this._root = _root;
            this.value = value;
            _read();
        }
        private void _read() {
        }
        private Boolean read;
        public Boolean read() {
            if (this.read != null)
                return this.read;
            boolean _tmp = (boolean) ((value() & 4) != 0);
            this.read = _tmp;
            return this.read;
        }
        private Boolean write;
        public Boolean write() {
            if (this.write != null)
                return this.write;
            boolean _tmp = (boolean) ((value() & 2) != 0);
            this.write = _tmp;
            return this.write;
        }
        private Boolean execute;
        public Boolean execute() {
            if (this.execute != null)
                return this.execute;
            boolean _tmp = (boolean) ((value() & 1) != 0);
            this.execute = _tmp;
            return this.execute;
        }
        private Boolean maskProc;
        public Boolean maskProc() {
            if (this.maskProc != null)
                return this.maskProc;
            boolean _tmp = (boolean) ((value() & 4026531840L) != 0);
            this.maskProc = _tmp;
            return this.maskProc;
        }
        private long value;
        private Elf _root;
        private Elf.EndianElf.ProgramHeader _parent;
        public long value() { return value; }
        public Elf _root() { return _root; }
        public Elf.EndianElf.ProgramHeader _parent() { return _parent; }
    }
    public static class SectionHeaderFlags extends KaitaiStruct {

        public SectionHeaderFlags(KaitaiStream _io, long value) {
            this(_io, null, null, value);
        }

        public SectionHeaderFlags(KaitaiStream _io, Elf.EndianElf.SectionHeader _parent, long value) {
            this(_io, _parent, null, value);
        }

        public SectionHeaderFlags(KaitaiStream _io, Elf.EndianElf.SectionHeader _parent, Elf _root, long value) {
            super(_io);
            this._parent = _parent;
            this._root = _root;
            this.value = value;
            _read();
        }
        private void _read() {
        }
        private Boolean merge;

        /**
         * might be merged
         */
        public Boolean merge() {
            if (this.merge != null)
                return this.merge;
            boolean _tmp = (boolean) ((value() & 16) != 0);
            this.merge = _tmp;
            return this.merge;
        }
        private Boolean maskOs;

        /**
         * OS-specific
         */
        public Boolean maskOs() {
            if (this.maskOs != null)
                return this.maskOs;
            boolean _tmp = (boolean) ((value() & 267386880) != 0);
            this.maskOs = _tmp;
            return this.maskOs;
        }
        private Boolean exclude;

        /**
         * section is excluded unless referenced or allocated (Solaris)
         */
        public Boolean exclude() {
            if (this.exclude != null)
                return this.exclude;
            boolean _tmp = (boolean) ((value() & 134217728) != 0);
            this.exclude = _tmp;
            return this.exclude;
        }
        private Boolean maskProc;

        /**
         * Processor-specific
         */
        public Boolean maskProc() {
            if (this.maskProc != null)
                return this.maskProc;
            boolean _tmp = (boolean) ((value() & 4026531840L) != 0);
            this.maskProc = _tmp;
            return this.maskProc;
        }
        private Boolean strings;

        /**
         * contains nul-terminated strings
         */
        public Boolean strings() {
            if (this.strings != null)
                return this.strings;
            boolean _tmp = (boolean) ((value() & 32) != 0);
            this.strings = _tmp;
            return this.strings;
        }
        private Boolean osNonConforming;

        /**
         * non-standard OS specific handling required
         */
        public Boolean osNonConforming() {
            if (this.osNonConforming != null)
                return this.osNonConforming;
            boolean _tmp = (boolean) ((value() & 256) != 0);
            this.osNonConforming = _tmp;
            return this.osNonConforming;
        }
        private Boolean alloc;

        /**
         * occupies memory during execution
         */
        public Boolean alloc() {
            if (this.alloc != null)
                return this.alloc;
            boolean _tmp = (boolean) ((value() & 2) != 0);
            this.alloc = _tmp;
            return this.alloc;
        }
        private Boolean execInstr;

        /**
         * executable
         */
        public Boolean execInstr() {
            if (this.execInstr != null)
                return this.execInstr;
            boolean _tmp = (boolean) ((value() & 4) != 0);
            this.execInstr = _tmp;
            return this.execInstr;
        }
        private Boolean infoLink;

        /**
         * 'sh_info' contains SHT index
         */
        public Boolean infoLink() {
            if (this.infoLink != null)
                return this.infoLink;
            boolean _tmp = (boolean) ((value() & 64) != 0);
            this.infoLink = _tmp;
            return this.infoLink;
        }
        private Boolean write;

        /**
         * writable
         */
        public Boolean write() {
            if (this.write != null)
                return this.write;
            boolean _tmp = (boolean) ((value() & 1) != 0);
            this.write = _tmp;
            return this.write;
        }
        private Boolean linkOrder;

        /**
         * preserve order after combining
         */
        public Boolean linkOrder() {
            if (this.linkOrder != null)
                return this.linkOrder;
            boolean _tmp = (boolean) ((value() & 128) != 0);
            this.linkOrder = _tmp;
            return this.linkOrder;
        }
        private Boolean ordered;

        /**
         * special ordering requirement (Solaris)
         */
        public Boolean ordered() {
            if (this.ordered != null)
                return this.ordered;
            boolean _tmp = (boolean) ((value() & 67108864) != 0);
            this.ordered = _tmp;
            return this.ordered;
        }
        private Boolean tls;

        /**
         * section hold thread-local data
         */
        public Boolean tls() {
            if (this.tls != null)
                return this.tls;
            boolean _tmp = (boolean) ((value() & 1024) != 0);
            this.tls = _tmp;
            return this.tls;
        }
        private Boolean group;

        /**
         * section is member of a group
         */
        public Boolean group() {
            if (this.group != null)
                return this.group;
            boolean _tmp = (boolean) ((value() & 512) != 0);
            this.group = _tmp;
            return this.group;
        }
        private long value;
        private Elf _root;
        private Elf.EndianElf.SectionHeader _parent;
        public long value() { return value; }
        public Elf _root() { return _root; }
        public Elf.EndianElf.SectionHeader _parent() { return _parent; }
    }
    public static class DtFlag1Values extends KaitaiStruct {

        public DtFlag1Values(KaitaiStream _io, long value) {
            this(_io, null, null, value);
        }

        public DtFlag1Values(KaitaiStream _io, Elf.EndianElf.DynamicSectionEntry _parent, long value) {
            this(_io, _parent, null, value);
        }

        public DtFlag1Values(KaitaiStream _io, Elf.EndianElf.DynamicSectionEntry _parent, Elf _root, long value) {
            super(_io);
            this._parent = _parent;
            this._root = _root;
            this.value = value;
            _read();
        }
        private void _read() {
        }
        private Boolean singleton;

        /**
         * Singleton symbols are used.
         */
        public Boolean singleton() {
            if (this.singleton != null)
                return this.singleton;
            boolean _tmp = (boolean) ((value() & 33554432) != 0);
            this.singleton = _tmp;
            return this.singleton;
        }
        private Boolean ignmuldef;
        public Boolean ignmuldef() {
            if (this.ignmuldef != null)
                return this.ignmuldef;
            boolean _tmp = (boolean) ((value() & 262144) != 0);
            this.ignmuldef = _tmp;
            return this.ignmuldef;
        }
        private Boolean loadfltr;

        /**
         * Trigger filtee loading at runtime.
         */
        public Boolean loadfltr() {
            if (this.loadfltr != null)
                return this.loadfltr;
            boolean _tmp = (boolean) ((value() & 16) != 0);
            this.loadfltr = _tmp;
            return this.loadfltr;
        }
        private Boolean initfirst;

        /**
         * Set RTLD_INITFIRST for this object
         */
        public Boolean initfirst() {
            if (this.initfirst != null)
                return this.initfirst;
            boolean _tmp = (boolean) ((value() & 32) != 0);
            this.initfirst = _tmp;
            return this.initfirst;
        }
        private Boolean symintpose;

        /**
         * Object has individual interposers.
         */
        public Boolean symintpose() {
            if (this.symintpose != null)
                return this.symintpose;
            boolean _tmp = (boolean) ((value() & 8388608) != 0);
            this.symintpose = _tmp;
            return this.symintpose;
        }
        private Boolean noreloc;
        public Boolean noreloc() {
            if (this.noreloc != null)
                return this.noreloc;
            boolean _tmp = (boolean) ((value() & 4194304) != 0);
            this.noreloc = _tmp;
            return this.noreloc;
        }
        private Boolean confalt;

        /**
         * Configuration alternative created.
         */
        public Boolean confalt() {
            if (this.confalt != null)
                return this.confalt;
            boolean _tmp = (boolean) ((value() & 8192) != 0);
            this.confalt = _tmp;
            return this.confalt;
        }
        private Boolean dispreldne;

        /**
         * Disp reloc applied at build time.
         */
        public Boolean dispreldne() {
            if (this.dispreldne != null)
                return this.dispreldne;
            boolean _tmp = (boolean) ((value() & 32768) != 0);
            this.dispreldne = _tmp;
            return this.dispreldne;
        }
        private Boolean rtldGlobal;

        /**
         * Set RTLD_GLOBAL for this object.
         */
        public Boolean rtldGlobal() {
            if (this.rtldGlobal != null)
                return this.rtldGlobal;
            boolean _tmp = (boolean) ((value() & 2) != 0);
            this.rtldGlobal = _tmp;
            return this.rtldGlobal;
        }
        private Boolean nodelete;

        /**
         * Set RTLD_NODELETE for this object.
         */
        public Boolean nodelete() {
            if (this.nodelete != null)
                return this.nodelete;
            boolean _tmp = (boolean) ((value() & 8) != 0);
            this.nodelete = _tmp;
            return this.nodelete;
        }
        private Boolean trans;
        public Boolean trans() {
            if (this.trans != null)
                return this.trans;
            boolean _tmp = (boolean) ((value() & 512) != 0);
            this.trans = _tmp;
            return this.trans;
        }
        private Boolean origin;

        /**
         * $ORIGIN must be handled.
         */
        public Boolean origin() {
            if (this.origin != null)
                return this.origin;
            boolean _tmp = (boolean) ((value() & 128) != 0);
            this.origin = _tmp;
            return this.origin;
        }
        private Boolean now;

        /**
         * Set RTLD_NOW for this object.
         */
        public Boolean now() {
            if (this.now != null)
                return this.now;
            boolean _tmp = (boolean) ((value() & 1) != 0);
            this.now = _tmp;
            return this.now;
        }
        private Boolean nohdr;
        public Boolean nohdr() {
            if (this.nohdr != null)
                return this.nohdr;
            boolean _tmp = (boolean) ((value() & 1048576) != 0);
            this.nohdr = _tmp;
            return this.nohdr;
        }
        private Boolean endfiltee;

        /**
         * Filtee terminates filters search.
         */
        public Boolean endfiltee() {
            if (this.endfiltee != null)
                return this.endfiltee;
            boolean _tmp = (boolean) ((value() & 16384) != 0);
            this.endfiltee = _tmp;
            return this.endfiltee;
        }
        private Boolean nodirect;

        /**
         * Object has no-direct binding.
         */
        public Boolean nodirect() {
            if (this.nodirect != null)
                return this.nodirect;
            boolean _tmp = (boolean) ((value() & 131072) != 0);
            this.nodirect = _tmp;
            return this.nodirect;
        }
        private Boolean globaudit;

        /**
         * Global auditing required.
         */
        public Boolean globaudit() {
            if (this.globaudit != null)
                return this.globaudit;
            boolean _tmp = (boolean) ((value() & 16777216) != 0);
            this.globaudit = _tmp;
            return this.globaudit;
        }
        private Boolean noksyms;
        public Boolean noksyms() {
            if (this.noksyms != null)
                return this.noksyms;
            boolean _tmp = (boolean) ((value() & 524288) != 0);
            this.noksyms = _tmp;
            return this.noksyms;
        }
        private Boolean interpose;

        /**
         * Object is used to interpose.
         */
        public Boolean interpose() {
            if (this.interpose != null)
                return this.interpose;
            boolean _tmp = (boolean) ((value() & 1024) != 0);
            this.interpose = _tmp;
            return this.interpose;
        }
        private Boolean nodump;

        /**
         * Object can't be dldump'ed.
         */
        public Boolean nodump() {
            if (this.nodump != null)
                return this.nodump;
            boolean _tmp = (boolean) ((value() & 4096) != 0);
            this.nodump = _tmp;
            return this.nodump;
        }
        private Boolean disprelpnd;

        /**
         * Disp reloc applied at run-time.
         */
        public Boolean disprelpnd() {
            if (this.disprelpnd != null)
                return this.disprelpnd;
            boolean _tmp = (boolean) ((value() & 65536) != 0);
            this.disprelpnd = _tmp;
            return this.disprelpnd;
        }
        private Boolean noopen;

        /**
         * Set RTLD_NOOPEN for this object.
         */
        public Boolean noopen() {
            if (this.noopen != null)
                return this.noopen;
            boolean _tmp = (boolean) ((value() & 64) != 0);
            this.noopen = _tmp;
            return this.noopen;
        }
        private Boolean stub;
        public Boolean stub() {
            if (this.stub != null)
                return this.stub;
            boolean _tmp = (boolean) ((value() & 67108864) != 0);
            this.stub = _tmp;
            return this.stub;
        }
        private Boolean direct;

        /**
         * Direct binding enabled.
         */
        public Boolean direct() {
            if (this.direct != null)
                return this.direct;
            boolean _tmp = (boolean) ((value() & 256) != 0);
            this.direct = _tmp;
            return this.direct;
        }
        private Boolean edited;

        /**
         * Object is modified after built.
         */
        public Boolean edited() {
            if (this.edited != null)
                return this.edited;
            boolean _tmp = (boolean) ((value() & 2097152) != 0);
            this.edited = _tmp;
            return this.edited;
        }
        private Boolean group;

        /**
         * Set RTLD_GROUP for this object.
         */
        public Boolean group() {
            if (this.group != null)
                return this.group;
            boolean _tmp = (boolean) ((value() & 4) != 0);
            this.group = _tmp;
            return this.group;
        }
        private Boolean pie;
        public Boolean pie() {
            if (this.pie != null)
                return this.pie;
            boolean _tmp = (boolean) ((value() & 134217728) != 0);
            this.pie = _tmp;
            return this.pie;
        }
        private Boolean nodeflib;

        /**
         * Ignore default lib search path.
         */
        public Boolean nodeflib() {
            if (this.nodeflib != null)
                return this.nodeflib;
            boolean _tmp = (boolean) ((value() & 2048) != 0);
            this.nodeflib = _tmp;
            return this.nodeflib;
        }
        private long value;
        private Elf _root;
        private Elf.EndianElf.DynamicSectionEntry _parent;
        public long value() { return value; }
        public Elf _root() { return _root; }
        public Elf.EndianElf.DynamicSectionEntry _parent() { return _parent; }
    }
    public static class EndianElf extends KaitaiStruct {
        public static EndianElf fromFile(String fileName) throws IOException {
            return new EndianElf(new ByteBufferKaitaiStream(fileName));
        }
        private Boolean _is_le;

        public EndianElf(KaitaiStream _io) {
            this(_io, null, null);
        }

        public EndianElf(KaitaiStream _io, Elf _parent) {
            this(_io, _parent, null);
        }

        public EndianElf(KaitaiStream _io, Elf _parent, Elf _root) {
            super(_io);
            this._parent = _parent;
            this._root = _root;
            _read();
        }
        private void _read() {
            switch (_root.endian()) {
            case LE: {
                boolean _tmp = (boolean) (true);
                this._is_le = _tmp;
                break;
            }
            case BE: {
                boolean _tmp = (boolean) (false);
                this._is_le = _tmp;
                break;
            }
            }

            if (_is_le == null) {
                throw new KaitaiStream.UndecidedEndiannessError();
            } else if (_is_le) {
                _readLE();
            } else {
                _readBE();
            }
        }
        private void _readLE() {
            this.eType = Elf.ObjType.byId(this._io.readU2le());
            this.machine = Elf.Machine.byId(this._io.readU2le());
            this.eVersion = this._io.readU4le();
            switch (_root.bits()) {
            case B32: {
                this.entryPoint = (long) (this._io.readU4le());
                break;
            }
            case B64: {
                this.entryPoint = this._io.readU8le();
                break;
            }
            }
            switch (_root.bits()) {
            case B32: {
                this.programHeaderOffset = (long) (this._io.readU4le());
                break;
            }
            case B64: {
                this.programHeaderOffset = this._io.readU8le();
                break;
            }
            }
            switch (_root.bits()) {
            case B32: {
                this.sectionHeaderOffset = (long) (this._io.readU4le());
                break;
            }
            case B64: {
                this.sectionHeaderOffset = this._io.readU8le();
                break;
            }
            }
            this.flags = this._io.readBytes(4);
            this.eEhsize = this._io.readU2le();
            this.programHeaderEntrySize = this._io.readU2le();
            this.qtyProgramHeader = this._io.readU2le();
            this.sectionHeaderEntrySize = this._io.readU2le();
            this.qtySectionHeader = this._io.readU2le();
            this.sectionNamesIdx = this._io.readU2le();
        }
        private void _readBE() {
            this.eType = Elf.ObjType.byId(this._io.readU2be());
            this.machine = Elf.Machine.byId(this._io.readU2be());
            this.eVersion = this._io.readU4be();
            switch (_root.bits()) {
            case B32: {
                this.entryPoint = (long) (this._io.readU4be());
                break;
            }
            case B64: {
                this.entryPoint = this._io.readU8be();
                break;
            }
            }
            switch (_root.bits()) {
            case B32: {
                this.programHeaderOffset = (long) (this._io.readU4be());
                break;
            }
            case B64: {
                this.programHeaderOffset = this._io.readU8be();
                break;
            }
            }
            switch (_root.bits()) {
            case B32: {
                this.sectionHeaderOffset = (long) (this._io.readU4be());
                break;
            }
            case B64: {
                this.sectionHeaderOffset = this._io.readU8be();
                break;
            }
            }
            this.flags = this._io.readBytes(4);
            this.eEhsize = this._io.readU2be();
            this.programHeaderEntrySize = this._io.readU2be();
            this.qtyProgramHeader = this._io.readU2be();
            this.sectionHeaderEntrySize = this._io.readU2be();
            this.qtySectionHeader = this._io.readU2be();
            this.sectionNamesIdx = this._io.readU2be();
        }
        public static class DynsymSectionEntry64 extends KaitaiStruct {
            private Boolean _is_le;

            public DynsymSectionEntry64(KaitaiStream _io, Elf.EndianElf.DynsymSection _parent, Elf _root, boolean _is_le) {
                super(_io);
                this._parent = _parent;
                this._root = _root;
                this._is_le = _is_le;
                _read();
            }
            private void _read() {

                if (_is_le == null) {
                    throw new KaitaiStream.UndecidedEndiannessError();
                } else if (_is_le) {
                    _readLE();
                } else {
                    _readBE();
                }
            }
            private void _readLE() {
                this.nameOffset = this._io.readU4le();
                this.info = this._io.readU1();
                this.other = this._io.readU1();
                this.shndx = this._io.readU2le();
                this.value = this._io.readU8le();
                this.size = this._io.readU8le();
            }
            private void _readBE() {
                this.nameOffset = this._io.readU4be();
                this.info = this._io.readU1();
                this.other = this._io.readU1();
                this.shndx = this._io.readU2be();
                this.value = this._io.readU8be();
                this.size = this._io.readU8be();
            }
            private long nameOffset;
            private int info;
            private int other;
            private int shndx;
            private long value;
            private long size;
            private Elf _root;
            private Elf.EndianElf.DynsymSection _parent;
            public long nameOffset() { return nameOffset; }
            public int info() { return info; }
            public int other() { return other; }
            public int shndx() { return shndx; }
            public long value() { return value; }
            public long size() { return size; }
            public Elf _root() { return _root; }
            public Elf.EndianElf.DynsymSection _parent() { return _parent; }
        }
        public static class ProgramHeader extends KaitaiStruct {
            private Boolean _is_le;

            public ProgramHeader(KaitaiStream _io, Elf.EndianElf _parent, Elf _root, boolean _is_le) {
                super(_io);
                this._parent = _parent;
                this._root = _root;
                this._is_le = _is_le;
                _read();
            }
            private void _read() {

                if (_is_le == null) {
                    throw new KaitaiStream.UndecidedEndiannessError();
                } else if (_is_le) {
                    _readLE();
                } else {
                    _readBE();
                }
            }
            private void _readLE() {
                this.type = Elf.PhType.byId(this._io.readU4le());
                if (_root.bits() == Elf.Bits.B64) {
                    this.flags64 = this._io.readU4le();
                }
                switch (_root.bits()) {
                case B32: {
                    this.offset = (long) (this._io.readU4le());
                    break;
                }
                case B64: {
                    this.offset = this._io.readU8le();
                    break;
                }
                }
                switch (_root.bits()) {
                case B32: {
                    this.vaddr = (long) (this._io.readU4le());
                    break;
                }
                case B64: {
                    this.vaddr = this._io.readU8le();
                    break;
                }
                }
                switch (_root.bits()) {
                case B32: {
                    this.paddr = (long) (this._io.readU4le());
                    break;
                }
                case B64: {
                    this.paddr = this._io.readU8le();
                    break;
                }
                }
                switch (_root.bits()) {
                case B32: {
                    this.filesz = (long) (this._io.readU4le());
                    break;
                }
                case B64: {
                    this.filesz = this._io.readU8le();
                    break;
                }
                }
                switch (_root.bits()) {
                case B32: {
                    this.memsz = (long) (this._io.readU4le());
                    break;
                }
                case B64: {
                    this.memsz = this._io.readU8le();
                    break;
                }
                }
                if (_root.bits() == Elf.Bits.B32) {
                    this.flags32 = this._io.readU4le();
                }
                switch (_root.bits()) {
                case B32: {
                    this.align = (long) (this._io.readU4le());
                    break;
                }
                case B64: {
                    this.align = this._io.readU8le();
                    break;
                }
                }
            }
            private void _readBE() {
                this.type = Elf.PhType.byId(this._io.readU4be());
                if (_root.bits() == Elf.Bits.B64) {
                    this.flags64 = this._io.readU4be();
                }
                switch (_root.bits()) {
                case B32: {
                    this.offset = (long) (this._io.readU4be());
                    break;
                }
                case B64: {
                    this.offset = this._io.readU8be();
                    break;
                }
                }
                switch (_root.bits()) {
                case B32: {
                    this.vaddr = (long) (this._io.readU4be());
                    break;
                }
                case B64: {
                    this.vaddr = this._io.readU8be();
                    break;
                }
                }
                switch (_root.bits()) {
                case B32: {
                    this.paddr = (long) (this._io.readU4be());
                    break;
                }
                case B64: {
                    this.paddr = this._io.readU8be();
                    break;
                }
                }
                switch (_root.bits()) {
                case B32: {
                    this.filesz = (long) (this._io.readU4be());
                    break;
                }
                case B64: {
                    this.filesz = this._io.readU8be();
                    break;
                }
                }
                switch (_root.bits()) {
                case B32: {
                    this.memsz = (long) (this._io.readU4be());
                    break;
                }
                case B64: {
                    this.memsz = this._io.readU8be();
                    break;
                }
                }
                if (_root.bits() == Elf.Bits.B32) {
                    this.flags32 = this._io.readU4be();
                }
                switch (_root.bits()) {
                case B32: {
                    this.align = (long) (this._io.readU4be());
                    break;
                }
                case B64: {
                    this.align = this._io.readU8be();
                    break;
                }
                }
            }
            private DynamicSection dynamic;
            public DynamicSection dynamic() {
                if (this.dynamic != null)
                    return this.dynamic;
                if (type() == Elf.PhType.DYNAMIC) {
                    KaitaiStream io = _root._io();
                    long _pos = io.pos();
                    io.seek(offset());
                    if (_is_le) {
                        this._raw_dynamic = io.readBytes(filesz());
                        KaitaiStream _io__raw_dynamic = new ByteBufferKaitaiStream(_raw_dynamic);
                        this.dynamic = new DynamicSection(_io__raw_dynamic, this, _root, _is_le);
                    } else {
                        this._raw_dynamic = io.readBytes(filesz());
                        KaitaiStream _io__raw_dynamic = new ByteBufferKaitaiStream(_raw_dynamic);
                        this.dynamic = new DynamicSection(_io__raw_dynamic, this, _root, _is_le);
                    }
                    io.seek(_pos);
                }
                return this.dynamic;
            }
            private PhdrTypeFlags flagsObj;
            public PhdrTypeFlags flagsObj() {
                if (this.flagsObj != null)
                    return this.flagsObj;
                if (_is_le) {
                    this.flagsObj = new PhdrTypeFlags(this._io, this, _root, (flags64() | flags32()));
                } else {
                    this.flagsObj = new PhdrTypeFlags(this._io, this, _root, (flags64() | flags32()));
                }
                return this.flagsObj;
            }
            private PhType type;
            private Long flags64;
            private Long offset;
            private Long vaddr;
            private Long paddr;
            private Long filesz;
            private Long memsz;
            private Long flags32;
            private Long align;
            private Elf _root;
            private Elf.EndianElf _parent;
            private byte[] _raw_dynamic;
            public PhType type() { return type; }
            public Long flags64() { return flags64; }
            public Long offset() { return offset; }
            public Long vaddr() { return vaddr; }
            public Long paddr() { return paddr; }
            public Long filesz() { return filesz; }
            public Long memsz() { return memsz; }
            public Long flags32() { return flags32; }
            public Long align() { return align; }
            public Elf _root() { return _root; }
            public Elf.EndianElf _parent() { return _parent; }
            public byte[] _raw_dynamic() { return _raw_dynamic; }
        }
        public static class DynamicSectionEntry extends KaitaiStruct {
            private Boolean _is_le;

            public DynamicSectionEntry(KaitaiStream _io, Elf.EndianElf.DynamicSection _parent, Elf _root, boolean _is_le) {
                super(_io);
                this._parent = _parent;
                this._root = _root;
                this._is_le = _is_le;
                _read();
            }
            private void _read() {

                if (_is_le == null) {
                    throw new KaitaiStream.UndecidedEndiannessError();
                } else if (_is_le) {
                    _readLE();
                } else {
                    _readBE();
                }
            }
            private void _readLE() {
                switch (_root.bits()) {
                case B32: {
                    this.tag = (long) (this._io.readU4le());
                    break;
                }
                case B64: {
                    this.tag = this._io.readU8le();
                    break;
                }
                }
                switch (_root.bits()) {
                case B32: {
                    this.valueOrPtr = (long) (this._io.readU4le());
                    break;
                }
                case B64: {
                    this.valueOrPtr = this._io.readU8le();
                    break;
                }
                }
            }
            private void _readBE() {
                switch (_root.bits()) {
                case B32: {
                    this.tag = (long) (this._io.readU4be());
                    break;
                }
                case B64: {
                    this.tag = this._io.readU8be();
                    break;
                }
                }
                switch (_root.bits()) {
                case B32: {
                    this.valueOrPtr = (long) (this._io.readU4be());
                    break;
                }
                case B64: {
                    this.valueOrPtr = this._io.readU8be();
                    break;
                }
                }
            }
            private DynamicArrayTags tagEnum;
            public DynamicArrayTags tagEnum() {
                if (this.tagEnum != null)
                    return this.tagEnum;
                this.tagEnum = Elf.DynamicArrayTags.byId(tag());
                return this.tagEnum;
            }
            private DtFlag1Values flag1Values;
            public DtFlag1Values flag1Values() {
                if (this.flag1Values != null)
                    return this.flag1Values;
                if (tagEnum() == Elf.DynamicArrayTags.FLAGS_1) {
                    if (_is_le) {
                        this.flag1Values = new DtFlag1Values(this._io, this, _root, valueOrPtr());
                    } else {
                        this.flag1Values = new DtFlag1Values(this._io, this, _root, valueOrPtr());
                    }
                }
                return this.flag1Values;
            }
            private Long tag;
            private Long valueOrPtr;
            private Elf _root;
            private Elf.EndianElf.DynamicSection _parent;
            public Long tag() { return tag; }
            public Long valueOrPtr() { return valueOrPtr; }
            public Elf _root() { return _root; }
            public Elf.EndianElf.DynamicSection _parent() { return _parent; }
        }
        public static class SectionHeader extends KaitaiStruct {
            private Boolean _is_le;

            public SectionHeader(KaitaiStream _io, Elf.EndianElf _parent, Elf _root, boolean _is_le) {
                super(_io);
                this._parent = _parent;
                this._root = _root;
                this._is_le = _is_le;
                _read();
            }
            private void _read() {

                if (_is_le == null) {
                    throw new KaitaiStream.UndecidedEndiannessError();
                } else if (_is_le) {
                    _readLE();
                } else {
                    _readBE();
                }
            }
            private void _readLE() {
                this.ofsName = this._io.readU4le();
                this.type = Elf.ShType.byId(this._io.readU4le());
                switch (_root.bits()) {
                case B32: {
                    this.flags = (long) (this._io.readU4le());
                    break;
                }
                case B64: {
                    this.flags = this._io.readU8le();
                    break;
                }
                }
                switch (_root.bits()) {
                case B32: {
                    this.addr = (long) (this._io.readU4le());
                    break;
                }
                case B64: {
                    this.addr = this._io.readU8le();
                    break;
                }
                }
                switch (_root.bits()) {
                case B32: {
                    this.ofsBody = (long) (this._io.readU4le());
                    break;
                }
                case B64: {
                    this.ofsBody = this._io.readU8le();
                    break;
                }
                }
                switch (_root.bits()) {
                case B32: {
                    this.lenBody = (long) (this._io.readU4le());
                    break;
                }
                case B64: {
                    this.lenBody = this._io.readU8le();
                    break;
                }
                }
                this.linkedSectionIdx = this._io.readU4le();
                this.info = this._io.readBytes(4);
                switch (_root.bits()) {
                case B32: {
                    this.align = (long) (this._io.readU4le());
                    break;
                }
                case B64: {
                    this.align = this._io.readU8le();
                    break;
                }
                }
                switch (_root.bits()) {
                case B32: {
                    this.entrySize = (long) (this._io.readU4le());
                    break;
                }
                case B64: {
                    this.entrySize = this._io.readU8le();
                    break;
                }
                }
            }
            private void _readBE() {
                this.ofsName = this._io.readU4be();
                this.type = Elf.ShType.byId(this._io.readU4be());
                switch (_root.bits()) {
                case B32: {
                    this.flags = (long) (this._io.readU4be());
                    break;
                }
                case B64: {
                    this.flags = this._io.readU8be();
                    break;
                }
                }
                switch (_root.bits()) {
                case B32: {
                    this.addr = (long) (this._io.readU4be());
                    break;
                }
                case B64: {
                    this.addr = this._io.readU8be();
                    break;
                }
                }
                switch (_root.bits()) {
                case B32: {
                    this.ofsBody = (long) (this._io.readU4be());
                    break;
                }
                case B64: {
                    this.ofsBody = this._io.readU8be();
                    break;
                }
                }
                switch (_root.bits()) {
                case B32: {
                    this.lenBody = (long) (this._io.readU4be());
                    break;
                }
                case B64: {
                    this.lenBody = this._io.readU8be();
                    break;
                }
                }
                this.linkedSectionIdx = this._io.readU4be();
                this.info = this._io.readBytes(4);
                switch (_root.bits()) {
                case B32: {
                    this.align = (long) (this._io.readU4be());
                    break;
                }
                case B64: {
                    this.align = this._io.readU8be();
                    break;
                }
                }
                switch (_root.bits()) {
                case B32: {
                    this.entrySize = (long) (this._io.readU4be());
                    break;
                }
                case B64: {
                    this.entrySize = this._io.readU8be();
                    break;
                }
                }
            }
            private Object body;
            public Object body() {
                if (this.body != null)
                    return this.body;
                KaitaiStream io = _root._io();
                long _pos = io.pos();
                io.seek(ofsBody());
                if (_is_le) {
                    switch (type()) {
                    case DYNAMIC: {
                        this._raw_body = io.readBytes(lenBody());
                        KaitaiStream _io__raw_body = new ByteBufferKaitaiStream(_raw_body);
                        this.body = new DynamicSection(_io__raw_body, this, _root, _is_le);
                        break;
                    }
                    case STRTAB: {
                        this._raw_body = io.readBytes(lenBody());
                        KaitaiStream _io__raw_body = new ByteBufferKaitaiStream(_raw_body);
                        this.body = new StringsStruct(_io__raw_body, this, _root, _is_le);
                        break;
                    }
//                    case DYNSTR: {
//                        this._raw_body = io.readBytes(lenBody());
//                        KaitaiStream _io__raw_body = new ByteBufferKaitaiStream(_raw_body);
//                        this.body = new StringsStruct(_io__raw_body, this, _root, _is_le);
//                        break;
//                    }
                    case DYNSYM: {
                        this._raw_body = io.readBytes(lenBody());
                        KaitaiStream _io__raw_body = new ByteBufferKaitaiStream(_raw_body);
                        this.body = new DynsymSection(_io__raw_body, this, _root, _is_le);
                        break;
                    }
                    default: {
                        this.body = io.readBytes(lenBody());
                        break;
                    }
                    }
                } else {
                    switch (type()) {
                    case DYNAMIC: {
                        this._raw_body = io.readBytes(lenBody());
                        KaitaiStream _io__raw_body = new ByteBufferKaitaiStream(_raw_body);
                        this.body = new DynamicSection(_io__raw_body, this, _root, _is_le);
                        break;
                    }
                    case STRTAB: {
                        this._raw_body = io.readBytes(lenBody());
                        KaitaiStream _io__raw_body = new ByteBufferKaitaiStream(_raw_body);
                        this.body = new StringsStruct(_io__raw_body, this, _root, _is_le);
                        break;
                    }
//                    case DYNSTR: {
//                        this._raw_body = io.readBytes(lenBody());
//                        KaitaiStream _io__raw_body = new ByteBufferKaitaiStream(_raw_body);
//                        this.body = new StringsStruct(_io__raw_body, this, _root, _is_le);
//                        break;
//                    }
                    case DYNSYM: {
                        this._raw_body = io.readBytes(lenBody());
                        KaitaiStream _io__raw_body = new ByteBufferKaitaiStream(_raw_body);
                        this.body = new DynsymSection(_io__raw_body, this, _root, _is_le);
                        break;
                    }
                    default: {
                        this.body = io.readBytes(lenBody());
                        break;
                    }
                    }
                }
                io.seek(_pos);
                return this.body;
            }
            private String name;
            public String name() {
                if (this.name != null)
                    return this.name;
                KaitaiStream io = _root.header().strings()._io();
                long _pos = io.pos();
                io.seek(ofsName());
                if (_is_le) {
                    this.name = new String(io.readBytesTerm(0, false, true, true), Charset.forName("ASCII"));
                } else {
                    this.name = new String(io.readBytesTerm(0, false, true, true), Charset.forName("ASCII"));
                }
                io.seek(_pos);
                return this.name;
            }
            private SectionHeaderFlags flagsObj;
            public SectionHeaderFlags flagsObj() {
                if (this.flagsObj != null)
                    return this.flagsObj;
                if (_is_le) {
                    this.flagsObj = new SectionHeaderFlags(this._io, this, _root, flags());
                } else {
                    this.flagsObj = new SectionHeaderFlags(this._io, this, _root, flags());
                }
                return this.flagsObj;
            }
            private long ofsName;
            private ShType type;
            private Long flags;
            private Long addr;
            private Long ofsBody;
            private Long lenBody;
            private long linkedSectionIdx;
            private byte[] info;
            private Long align;
            private Long entrySize;
            private Elf _root;
            private Elf.EndianElf _parent;
            private byte[] _raw_body;
            public long ofsName() { return ofsName; }
            public ShType type() { return type; }
            public Long flags() { return flags; }
            public Long addr() { return addr; }
            public Long ofsBody() { return ofsBody; }
            public Long lenBody() { return lenBody; }
            public long linkedSectionIdx() { return linkedSectionIdx; }
            public byte[] info() { return info; }
            public Long align() { return align; }
            public Long entrySize() { return entrySize; }
            public Elf _root() { return _root; }
            public Elf.EndianElf _parent() { return _parent; }
            public byte[] _raw_body() { return _raw_body; }
        }
        public static class DynamicSection extends KaitaiStruct {
            private Boolean _is_le;

            public DynamicSection(KaitaiStream _io, KaitaiStruct _parent, Elf _root, boolean _is_le) {
                super(_io);
                this._parent = _parent;
                this._root = _root;
                this._is_le = _is_le;
                _read();
            }
            private void _read() {

                if (_is_le == null) {
                    throw new KaitaiStream.UndecidedEndiannessError();
                } else if (_is_le) {
                    _readLE();
                } else {
                    _readBE();
                }
            }
            private void _readLE() {
                this.entries = new ArrayList<DynamicSectionEntry>();
                {
                    int i = 0;
                    while (!this._io.isEof()) {
                        this.entries.add(new DynamicSectionEntry(this._io, this, _root, _is_le));
                        i++;
                    }
                }
            }
            private void _readBE() {
                this.entries = new ArrayList<DynamicSectionEntry>();
                {
                    int i = 0;
                    while (!this._io.isEof()) {
                        this.entries.add(new DynamicSectionEntry(this._io, this, _root, _is_le));
                        i++;
                    }
                }
            }
            private ArrayList<DynamicSectionEntry> entries;
            private Elf _root;
            private KaitaiStruct _parent;
            public ArrayList<DynamicSectionEntry> entries() { return entries; }
            public Elf _root() { return _root; }
            public KaitaiStruct _parent() { return _parent; }
        }
        public static class DynsymSection extends KaitaiStruct {
            private Boolean _is_le;

            public DynsymSection(KaitaiStream _io, Elf.EndianElf.SectionHeader _parent, Elf _root, boolean _is_le) {
                super(_io);
                this._parent = _parent;
                this._root = _root;
                this._is_le = _is_le;
                _read();
            }
            private void _read() {

                if (_is_le == null) {
                    throw new KaitaiStream.UndecidedEndiannessError();
                } else if (_is_le) {
                    _readLE();
                } else {
                    _readBE();
                }
            }
            private void _readLE() {
                this.entries = new ArrayList<KaitaiStruct>();
                {
                    int i = 0;
                    while (!this._io.isEof()) {
                        switch (_root.bits()) {
                        case B32: {
                            this.entries.add(new DynsymSectionEntry32(this._io, this, _root, _is_le));
                            break;
                        }
                        case B64: {
                            this.entries.add(new DynsymSectionEntry64(this._io, this, _root, _is_le));
                            break;
                        }
                        }
                        i++;
                    }
                }
            }
            private void _readBE() {
                this.entries = new ArrayList<KaitaiStruct>();
                {
                    int i = 0;
                    while (!this._io.isEof()) {
                        switch (_root.bits()) {
                        case B32: {
                            this.entries.add(new DynsymSectionEntry32(this._io, this, _root, _is_le));
                            break;
                        }
                        case B64: {
                            this.entries.add(new DynsymSectionEntry64(this._io, this, _root, _is_le));
                            break;
                        }
                        }
                        i++;
                    }
                }
            }
            private ArrayList<KaitaiStruct> entries;
            private Elf _root;
            private Elf.EndianElf.SectionHeader _parent;
            public ArrayList<KaitaiStruct> entries() { return entries; }
            public Elf _root() { return _root; }
            public Elf.EndianElf.SectionHeader _parent() { return _parent; }
        }
        public static class DynsymSectionEntry32 extends KaitaiStruct {
            private Boolean _is_le;

            public DynsymSectionEntry32(KaitaiStream _io, Elf.EndianElf.DynsymSection _parent, Elf _root, boolean _is_le) {
                super(_io);
                this._parent = _parent;
                this._root = _root;
                this._is_le = _is_le;
                _read();
            }
            private void _read() {

                if (_is_le == null) {
                    throw new KaitaiStream.UndecidedEndiannessError();
                } else if (_is_le) {
                    _readLE();
                } else {
                    _readBE();
                }
            }
            private void _readLE() {
                this.nameOffset = this._io.readU4le();
                this.value = this._io.readU4le();
                this.size = this._io.readU4le();
                this.info = this._io.readU1();
                this.other = this._io.readU1();
                this.shndx = this._io.readU2le();
            }
            private void _readBE() {
                this.nameOffset = this._io.readU4be();
                this.value = this._io.readU4be();
                this.size = this._io.readU4be();
                this.info = this._io.readU1();
                this.other = this._io.readU1();
                this.shndx = this._io.readU2be();
            }
            private long nameOffset;
            private long value;
            private long size;
            private int info;
            private int other;
            private int shndx;
            private Elf _root;
            private Elf.EndianElf.DynsymSection _parent;
            public long nameOffset() { return nameOffset; }
            public long value() { return value; }
            public long size() { return size; }
            public int info() { return info; }
            public int other() { return other; }
            public int shndx() { return shndx; }
            public Elf _root() { return _root; }
            public Elf.EndianElf.DynsymSection _parent() { return _parent; }
        }
        public static class StringsStruct extends KaitaiStruct {
            private Boolean _is_le;

            public StringsStruct(KaitaiStream _io, KaitaiStruct _parent, Elf _root, boolean _is_le) {
                super(_io);
                this._parent = _parent;
                this._root = _root;
                this._is_le = _is_le;
                _read();
            }
            private void _read() {

                if (_is_le == null) {
                    throw new KaitaiStream.UndecidedEndiannessError();
                } else if (_is_le) {
                    _readLE();
                } else {
                    _readBE();
                }
            }
            private void _readLE() {
                this.entries = new ArrayList<String>();
                {
                    int i = 0;
                    while (!this._io.isEof()) {
                        this.entries.add(new String(this._io.readBytesTerm(0, false, true, true), Charset.forName("ASCII")));
                        i++;
                    }
                }
            }
            private void _readBE() {
                this.entries = new ArrayList<String>();
                {
                    int i = 0;
                    while (!this._io.isEof()) {
                        this.entries.add(new String(this._io.readBytesTerm(0, false, true, true), Charset.forName("ASCII")));
                        i++;
                    }
                }
            }
            private ArrayList<String> entries;
            private Elf _root;
            private KaitaiStruct _parent;
            public ArrayList<String> entries() { return entries; }
            public Elf _root() { return _root; }
            public KaitaiStruct _parent() { return _parent; }
        }
        private ArrayList<ProgramHeader> programHeaders;
        public ArrayList<ProgramHeader> programHeaders() {
            if (this.programHeaders != null)
                return this.programHeaders;
            long _pos = this._io.pos();
            this._io.seek(programHeaderOffset());
            if (_is_le) {
                this._raw_programHeaders = new ArrayList<byte[]>((int) (qtyProgramHeader()));
                programHeaders = new ArrayList<ProgramHeader>((int) (qtyProgramHeader()));
                for (int i = 0; i < qtyProgramHeader(); i++) {
                    this._raw_programHeaders.add(this._io.readBytes(programHeaderEntrySize()));
                    KaitaiStream _io__raw_programHeaders = new ByteBufferKaitaiStream(_raw_programHeaders.get(_raw_programHeaders.size() - 1));
                    this.programHeaders.add(new ProgramHeader(_io__raw_programHeaders, this, _root, _is_le));
                }
            } else {
                this._raw_programHeaders = new ArrayList<byte[]>((int) (qtyProgramHeader()));
                programHeaders = new ArrayList<ProgramHeader>((int) (qtyProgramHeader()));
                for (int i = 0; i < qtyProgramHeader(); i++) {
                    this._raw_programHeaders.add(this._io.readBytes(programHeaderEntrySize()));
                    KaitaiStream _io__raw_programHeaders = new ByteBufferKaitaiStream(_raw_programHeaders.get(_raw_programHeaders.size() - 1));
                    this.programHeaders.add(new ProgramHeader(_io__raw_programHeaders, this, _root, _is_le));
                }
            }
            this._io.seek(_pos);
            return this.programHeaders;
        }
        private ArrayList<SectionHeader> sectionHeaders;
        public ArrayList<SectionHeader> sectionHeaders() {
            if (this.sectionHeaders != null)
                return this.sectionHeaders;
            long _pos = this._io.pos();
            this._io.seek(sectionHeaderOffset());
            if (_is_le) {
                this._raw_sectionHeaders = new ArrayList<byte[]>((int) (qtySectionHeader()));
                sectionHeaders = new ArrayList<SectionHeader>((int) (qtySectionHeader()));
                for (int i = 0; i < qtySectionHeader(); i++) {
                    this._raw_sectionHeaders.add(this._io.readBytes(sectionHeaderEntrySize()));
                    KaitaiStream _io__raw_sectionHeaders = new ByteBufferKaitaiStream(_raw_sectionHeaders.get(_raw_sectionHeaders.size() - 1));
                    this.sectionHeaders.add(new SectionHeader(_io__raw_sectionHeaders, this, _root, _is_le));
                }
            } else {
                this._raw_sectionHeaders = new ArrayList<byte[]>((int) (qtySectionHeader()));
                sectionHeaders = new ArrayList<SectionHeader>((int) (qtySectionHeader()));
                for (int i = 0; i < qtySectionHeader(); i++) {
                    this._raw_sectionHeaders.add(this._io.readBytes(sectionHeaderEntrySize()));
                    KaitaiStream _io__raw_sectionHeaders = new ByteBufferKaitaiStream(_raw_sectionHeaders.get(_raw_sectionHeaders.size() - 1));
                    this.sectionHeaders.add(new SectionHeader(_io__raw_sectionHeaders, this, _root, _is_le));
                }
            }
            this._io.seek(_pos);
            return this.sectionHeaders;
        }
        private StringsStruct strings;
        public StringsStruct strings() {
            if (this.strings != null)
                return this.strings;
            long _pos = this._io.pos();
            this._io.seek(sectionHeaders().get((int) sectionNamesIdx()).ofsBody());
            if (_is_le) {
                this._raw_strings = this._io.readBytes(sectionHeaders().get((int) sectionNamesIdx()).lenBody());
                KaitaiStream _io__raw_strings = new ByteBufferKaitaiStream(_raw_strings);
                this.strings = new StringsStruct(_io__raw_strings, this, _root, _is_le);
            } else {
                this._raw_strings = this._io.readBytes(sectionHeaders().get((int) sectionNamesIdx()).lenBody());
                KaitaiStream _io__raw_strings = new ByteBufferKaitaiStream(_raw_strings);
                this.strings = new StringsStruct(_io__raw_strings, this, _root, _is_le);
            }
            this._io.seek(_pos);
            return this.strings;
        }
        private ObjType eType;
        private Machine machine;
        private long eVersion;
        private Long entryPoint;
        private Long programHeaderOffset;
        private Long sectionHeaderOffset;
        private byte[] flags;
        private int eEhsize;
        private int programHeaderEntrySize;
        private int qtyProgramHeader;
        private int sectionHeaderEntrySize;
        private int qtySectionHeader;
        private int sectionNamesIdx;
        private Elf _root;
        private Elf _parent;
        private ArrayList<byte[]> _raw_programHeaders;
        private ArrayList<byte[]> _raw_sectionHeaders;
        private byte[] _raw_strings;
        public ObjType eType() { return eType; }
        public Machine machine() { return machine; }
        public long eVersion() { return eVersion; }
        public Long entryPoint() { return entryPoint; }
        public Long programHeaderOffset() { return programHeaderOffset; }
        public Long sectionHeaderOffset() { return sectionHeaderOffset; }
        public byte[] flags() { return flags; }
        public int eEhsize() { return eEhsize; }
        public int programHeaderEntrySize() { return programHeaderEntrySize; }
        public int qtyProgramHeader() { return qtyProgramHeader; }
        public int sectionHeaderEntrySize() { return sectionHeaderEntrySize; }
        public int qtySectionHeader() { return qtySectionHeader; }
        public int sectionNamesIdx() { return sectionNamesIdx; }
        public Elf _root() { return _root; }
        public Elf _parent() { return _parent; }
        public ArrayList<byte[]> _raw_programHeaders() { return _raw_programHeaders; }
        public ArrayList<byte[]> _raw_sectionHeaders() { return _raw_sectionHeaders; }
        public byte[] _raw_strings() { return _raw_strings; }
    }
    private byte[] magic;
    private Bits bits;
    private Endian endian;
    private int eiVersion;
    private OsAbi abi;
    private int abiVersion;
    private byte[] pad;
    private EndianElf header;
    private Elf _root;
    private KaitaiStruct _parent;

    /**
     * File identification, must be 0x7f + "ELF".
     */
    public byte[] magic() { return magic; }

    /**
     * File class: designates target machine word size (32 or 64
     * bits). The size of many integer fields in this format will
     * depend on this setting.
     */
    public Bits bits() { return bits; }

    /**
     * Endianness used for all integers.
     */
    public Endian endian() { return endian; }

    /**
     * ELF header version.
     */
    public int eiVersion() { return eiVersion; }

    /**
     * Specifies which OS- and ABI-related extensions will be used
     * in this ELF file.
     */
    public OsAbi abi() { return abi; }

    /**
     * Version of ABI targeted by this ELF file. Interpretation
     * depends on `abi` attribute.
     */
    public int abiVersion() { return abiVersion; }
    public byte[] pad() { return pad; }
    public EndianElf header() { return header; }
    public Elf _root() { return _root; }
    public KaitaiStruct _parent() { return _parent; }
}
