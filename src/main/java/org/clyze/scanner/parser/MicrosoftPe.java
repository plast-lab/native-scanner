// This is a generated file! Please edit source .ksy file and use kaitai-struct-compiler to rebuild

package org.clyze.scanner.parser;

import io.kaitai.struct.ByteBufferKaitaiStream;
import io.kaitai.struct.KaitaiStruct;
import io.kaitai.struct.KaitaiStream;
import java.io.IOException;
import java.util.Map;
import java.util.HashMap;
import java.util.ArrayList;
import java.nio.charset.Charset;


/**
 * @see <a href="http://www.microsoft.com/whdc/system/platform/firmware/PECOFF.mspx">Source</a>
 */
public class MicrosoftPe extends KaitaiStruct {
    public static MicrosoftPe fromFile(String fileName) throws IOException {
        return new MicrosoftPe(new ByteBufferKaitaiStream(fileName));
    }

    public enum PeFormat {
        ROM_IMAGE(263),
        PE32(267),
        PE32_PLUS(523);

        private final long id;
        PeFormat(long id) { this.id = id; }
        public long id() { return id; }
        private static final Map<Long, PeFormat> byId = new HashMap<Long, PeFormat>(3);
        static {
            for (PeFormat e : PeFormat.values())
                byId.put(e.id(), e);
        }
        public static PeFormat byId(long id) { return byId.get(id); }
    }

    public MicrosoftPe(KaitaiStream _io) {
        this(_io, null, null);
    }

    public MicrosoftPe(KaitaiStream _io, KaitaiStruct _parent) {
        this(_io, _parent, null);
    }

    public MicrosoftPe(KaitaiStream _io, KaitaiStruct _parent, MicrosoftPe _root) {
        super(_io);
        this._parent = _parent;
        this._root = _root == null ? this : _root;
        _read();
    }
    private void _read() {
        this.mz = new MzPlaceholder(this._io, this, _root);
    }

    /**
     * @see <a href="https://docs.microsoft.com/en-us/windows/desktop/debug/pe-format#the-attribute-certificate-table-image-only">Source</a>
     */
    public static class CertificateEntry extends KaitaiStruct {
        public static CertificateEntry fromFile(String fileName) throws IOException {
            return new CertificateEntry(new ByteBufferKaitaiStream(fileName));
        }

        public enum CertificateRevision {
            REVISION_1_0(256),
            REVISION_2_0(512);

            private final long id;
            CertificateRevision(long id) { this.id = id; }
            public long id() { return id; }
            private static final Map<Long, CertificateRevision> byId = new HashMap<Long, CertificateRevision>(2);
            static {
                for (CertificateRevision e : CertificateRevision.values())
                    byId.put(e.id(), e);
            }
            public static CertificateRevision byId(long id) { return byId.get(id); }
        }

        public enum CertificateType {
            X509(1),
            PKCS_SIGNED_DATA(2),
            RESERVED_1(3),
            TS_STACK_SIGNED(4);

            private final long id;
            CertificateType(long id) { this.id = id; }
            public long id() { return id; }
            private static final Map<Long, CertificateType> byId = new HashMap<Long, CertificateType>(4);
            static {
                for (CertificateType e : CertificateType.values())
                    byId.put(e.id(), e);
            }
            public static CertificateType byId(long id) { return byId.get(id); }
        }

        public CertificateEntry(KaitaiStream _io) {
            this(_io, null, null);
        }

        public CertificateEntry(KaitaiStream _io, MicrosoftPe.CertificateTable _parent) {
            this(_io, _parent, null);
        }

        public CertificateEntry(KaitaiStream _io, MicrosoftPe.CertificateTable _parent, MicrosoftPe _root) {
            super(_io);
            this._parent = _parent;
            this._root = _root;
            _read();
        }
        private void _read() {
            this.length = this._io.readU4le();
            this.revision = CertificateRevision.byId(this._io.readU2le());
            this.certificateType = CertificateType.byId(this._io.readU2le());
            this.certificateBytes = this._io.readBytes((length() - 8));
        }
        private long length;
        private CertificateRevision revision;
        private CertificateType certificateType;
        private byte[] certificateBytes;
        private MicrosoftPe _root;
        private MicrosoftPe.CertificateTable _parent;

        /**
         * Specifies the length of the attribute certificate entry.
         */
        public long length() { return length; }

        /**
         * Contains the certificate version number.
         */
        public CertificateRevision revision() { return revision; }

        /**
         * Specifies the type of content in bCertificate
         */
        public CertificateType certificateType() { return certificateType; }

        /**
         * Contains a certificate, such as an Authenticode signature.
         */
        public byte[] certificateBytes() { return certificateBytes; }
        public MicrosoftPe _root() { return _root; }
        public MicrosoftPe.CertificateTable _parent() { return _parent; }
    }
    public static class OptionalHeaderWindows extends KaitaiStruct {
        public static OptionalHeaderWindows fromFile(String fileName) throws IOException {
            return new OptionalHeaderWindows(new ByteBufferKaitaiStream(fileName));
        }

        public enum SubsystemEnum {
            UNKNOWN(0),
            NATIVE(1),
            WINDOWS_GUI(2),
            WINDOWS_CUI(3),
            POSIX_CUI(7),
            WINDOWS_CE_GUI(9),
            EFI_APPLICATION(10),
            EFI_BOOT_SERVICE_DRIVER(11),
            EFI_RUNTIME_DRIVER(12),
            EFI_ROM(13),
            XBOX(14),
            WINDOWS_BOOT_APPLICATION(16);

            private final long id;
            SubsystemEnum(long id) { this.id = id; }
            public long id() { return id; }
            private static final Map<Long, SubsystemEnum> byId = new HashMap<Long, SubsystemEnum>(12);
            static {
                for (SubsystemEnum e : SubsystemEnum.values())
                    byId.put(e.id(), e);
            }
            public static SubsystemEnum byId(long id) { return byId.get(id); }
        }

        public OptionalHeaderWindows(KaitaiStream _io) {
            this(_io, null, null);
        }

        public OptionalHeaderWindows(KaitaiStream _io, MicrosoftPe.OptionalHeader _parent) {
            this(_io, _parent, null);
        }

        public OptionalHeaderWindows(KaitaiStream _io, MicrosoftPe.OptionalHeader _parent, MicrosoftPe _root) {
            super(_io);
            this._parent = _parent;
            this._root = _root;
            _read();
        }
        private void _read() {
            if (_parent().std().format() == MicrosoftPe.PeFormat.PE32) {
                this.imageBase32 = this._io.readU4le();
            }
            if (_parent().std().format() == MicrosoftPe.PeFormat.PE32_PLUS) {
                this.imageBase64 = this._io.readU8le();
            }
            this.sectionAlignment = this._io.readU4le();
            this.fileAlignment = this._io.readU4le();
            this.majorOperatingSystemVersion = this._io.readU2le();
            this.minorOperatingSystemVersion = this._io.readU2le();
            this.majorImageVersion = this._io.readU2le();
            this.minorImageVersion = this._io.readU2le();
            this.majorSubsystemVersion = this._io.readU2le();
            this.minorSubsystemVersion = this._io.readU2le();
            this.win32VersionValue = this._io.readU4le();
            this.sizeOfImage = this._io.readU4le();
            this.sizeOfHeaders = this._io.readU4le();
            this.checkSum = this._io.readU4le();
            this.subsystem = SubsystemEnum.byId(this._io.readU2le());
            this.dllCharacteristics = this._io.readU2le();
            if (_parent().std().format() == MicrosoftPe.PeFormat.PE32) {
                this.sizeOfStackReserve32 = this._io.readU4le();
            }
            if (_parent().std().format() == MicrosoftPe.PeFormat.PE32_PLUS) {
                this.sizeOfStackReserve64 = this._io.readU8le();
            }
            if (_parent().std().format() == MicrosoftPe.PeFormat.PE32) {
                this.sizeOfStackCommit32 = this._io.readU4le();
            }
            if (_parent().std().format() == MicrosoftPe.PeFormat.PE32_PLUS) {
                this.sizeOfStackCommit64 = this._io.readU8le();
            }
            if (_parent().std().format() == MicrosoftPe.PeFormat.PE32) {
                this.sizeOfHeapReserve32 = this._io.readU4le();
            }
            if (_parent().std().format() == MicrosoftPe.PeFormat.PE32_PLUS) {
                this.sizeOfHeapReserve64 = this._io.readU8le();
            }
            if (_parent().std().format() == MicrosoftPe.PeFormat.PE32) {
                this.sizeOfHeapCommit32 = this._io.readU4le();
            }
            if (_parent().std().format() == MicrosoftPe.PeFormat.PE32_PLUS) {
                this.sizeOfHeapCommit64 = this._io.readU8le();
            }
            this.loaderFlags = this._io.readU4le();
            this.numberOfRvaAndSizes = this._io.readU4le();
        }
        private Long imageBase32;
        private Long imageBase64;
        private long sectionAlignment;
        private long fileAlignment;
        private int majorOperatingSystemVersion;
        private int minorOperatingSystemVersion;
        private int majorImageVersion;
        private int minorImageVersion;
        private int majorSubsystemVersion;
        private int minorSubsystemVersion;
        private long win32VersionValue;
        private long sizeOfImage;
        private long sizeOfHeaders;
        private long checkSum;
        private SubsystemEnum subsystem;
        private int dllCharacteristics;
        private Long sizeOfStackReserve32;
        private Long sizeOfStackReserve64;
        private Long sizeOfStackCommit32;
        private Long sizeOfStackCommit64;
        private Long sizeOfHeapReserve32;
        private Long sizeOfHeapReserve64;
        private Long sizeOfHeapCommit32;
        private Long sizeOfHeapCommit64;
        private long loaderFlags;
        private long numberOfRvaAndSizes;
        private MicrosoftPe _root;
        private MicrosoftPe.OptionalHeader _parent;
        public Long imageBase32() { return imageBase32; }
        public Long imageBase64() { return imageBase64; }
        public long sectionAlignment() { return sectionAlignment; }
        public long fileAlignment() { return fileAlignment; }
        public int majorOperatingSystemVersion() { return majorOperatingSystemVersion; }
        public int minorOperatingSystemVersion() { return minorOperatingSystemVersion; }
        public int majorImageVersion() { return majorImageVersion; }
        public int minorImageVersion() { return minorImageVersion; }
        public int majorSubsystemVersion() { return majorSubsystemVersion; }
        public int minorSubsystemVersion() { return minorSubsystemVersion; }
        public long win32VersionValue() { return win32VersionValue; }
        public long sizeOfImage() { return sizeOfImage; }
        public long sizeOfHeaders() { return sizeOfHeaders; }
        public long checkSum() { return checkSum; }
        public SubsystemEnum subsystem() { return subsystem; }
        public int dllCharacteristics() { return dllCharacteristics; }
        public Long sizeOfStackReserve32() { return sizeOfStackReserve32; }
        public Long sizeOfStackReserve64() { return sizeOfStackReserve64; }
        public Long sizeOfStackCommit32() { return sizeOfStackCommit32; }
        public Long sizeOfStackCommit64() { return sizeOfStackCommit64; }
        public Long sizeOfHeapReserve32() { return sizeOfHeapReserve32; }
        public Long sizeOfHeapReserve64() { return sizeOfHeapReserve64; }
        public Long sizeOfHeapCommit32() { return sizeOfHeapCommit32; }
        public Long sizeOfHeapCommit64() { return sizeOfHeapCommit64; }
        public long loaderFlags() { return loaderFlags; }
        public long numberOfRvaAndSizes() { return numberOfRvaAndSizes; }
        public MicrosoftPe _root() { return _root; }
        public MicrosoftPe.OptionalHeader _parent() { return _parent; }
    }
    public static class OptionalHeaderDataDirs extends KaitaiStruct {
        public static OptionalHeaderDataDirs fromFile(String fileName) throws IOException {
            return new OptionalHeaderDataDirs(new ByteBufferKaitaiStream(fileName));
        }

        public OptionalHeaderDataDirs(KaitaiStream _io) {
            this(_io, null, null);
        }

        public OptionalHeaderDataDirs(KaitaiStream _io, MicrosoftPe.OptionalHeader _parent) {
            this(_io, _parent, null);
        }

        public OptionalHeaderDataDirs(KaitaiStream _io, MicrosoftPe.OptionalHeader _parent, MicrosoftPe _root) {
            super(_io);
            this._parent = _parent;
            this._root = _root;
            _read();
        }
        private void _read() {
            this.exportTable = new DataDir(this._io, this, _root);
            this.importTable = new DataDir(this._io, this, _root);
            this.resourceTable = new DataDir(this._io, this, _root);
            this.exceptionTable = new DataDir(this._io, this, _root);
            this.certificateTable = new DataDir(this._io, this, _root);
            this.baseRelocationTable = new DataDir(this._io, this, _root);
            this.debug = new DataDir(this._io, this, _root);
            this.architecture = new DataDir(this._io, this, _root);
            this.globalPtr = new DataDir(this._io, this, _root);
            this.tlsTable = new DataDir(this._io, this, _root);
            this.loadConfigTable = new DataDir(this._io, this, _root);
            this.boundImport = new DataDir(this._io, this, _root);
            this.iat = new DataDir(this._io, this, _root);
            this.delayImportDescriptor = new DataDir(this._io, this, _root);
            this.clrRuntimeHeader = new DataDir(this._io, this, _root);
        }
        private DataDir exportTable;
        private DataDir importTable;
        private DataDir resourceTable;
        private DataDir exceptionTable;
        private DataDir certificateTable;
        private DataDir baseRelocationTable;
        private DataDir debug;
        private DataDir architecture;
        private DataDir globalPtr;
        private DataDir tlsTable;
        private DataDir loadConfigTable;
        private DataDir boundImport;
        private DataDir iat;
        private DataDir delayImportDescriptor;
        private DataDir clrRuntimeHeader;
        private MicrosoftPe _root;
        private MicrosoftPe.OptionalHeader _parent;
        public DataDir exportTable() { return exportTable; }
        public DataDir importTable() { return importTable; }
        public DataDir resourceTable() { return resourceTable; }
        public DataDir exceptionTable() { return exceptionTable; }
        public DataDir certificateTable() { return certificateTable; }
        public DataDir baseRelocationTable() { return baseRelocationTable; }
        public DataDir debug() { return debug; }
        public DataDir architecture() { return architecture; }
        public DataDir globalPtr() { return globalPtr; }
        public DataDir tlsTable() { return tlsTable; }
        public DataDir loadConfigTable() { return loadConfigTable; }
        public DataDir boundImport() { return boundImport; }
        public DataDir iat() { return iat; }
        public DataDir delayImportDescriptor() { return delayImportDescriptor; }
        public DataDir clrRuntimeHeader() { return clrRuntimeHeader; }
        public MicrosoftPe _root() { return _root; }
        public MicrosoftPe.OptionalHeader _parent() { return _parent; }
    }
    public static class DataDir extends KaitaiStruct {
        public static DataDir fromFile(String fileName) throws IOException {
            return new DataDir(new ByteBufferKaitaiStream(fileName));
        }

        public DataDir(KaitaiStream _io) {
            this(_io, null, null);
        }

        public DataDir(KaitaiStream _io, MicrosoftPe.OptionalHeaderDataDirs _parent) {
            this(_io, _parent, null);
        }

        public DataDir(KaitaiStream _io, MicrosoftPe.OptionalHeaderDataDirs _parent, MicrosoftPe _root) {
            super(_io);
            this._parent = _parent;
            this._root = _root;
            _read();
        }
        private void _read() {
            this.virtualAddress = this._io.readU4le();
            this.size = this._io.readU4le();
        }
        private long virtualAddress;
        private long size;
        private MicrosoftPe _root;
        private MicrosoftPe.OptionalHeaderDataDirs _parent;
        public long virtualAddress() { return virtualAddress; }
        public long size() { return size; }
        public MicrosoftPe _root() { return _root; }
        public MicrosoftPe.OptionalHeaderDataDirs _parent() { return _parent; }
    }
    public static class CoffSymbol extends KaitaiStruct {
        public static CoffSymbol fromFile(String fileName) throws IOException {
            return new CoffSymbol(new ByteBufferKaitaiStream(fileName));
        }

        public CoffSymbol(KaitaiStream _io) {
            this(_io, null, null);
        }

        public CoffSymbol(KaitaiStream _io, MicrosoftPe.CoffHeader _parent) {
            this(_io, _parent, null);
        }

        public CoffSymbol(KaitaiStream _io, MicrosoftPe.CoffHeader _parent, MicrosoftPe _root) {
            super(_io);
            this._parent = _parent;
            this._root = _root;
            _read();
        }
        private void _read() {
            this._raw_nameAnnoying = this._io.readBytes(8);
            KaitaiStream _io__raw_nameAnnoying = new ByteBufferKaitaiStream(_raw_nameAnnoying);
            this.nameAnnoying = new Annoyingstring(_io__raw_nameAnnoying, this, _root);
            this.value = this._io.readU4le();
            this.sectionNumber = this._io.readU2le();
            this.type = this._io.readU2le();
            this.storageClass = this._io.readU1();
            this.numberOfAuxSymbols = this._io.readU1();
        }
        private Section section;
        public Section section() {
            if (this.section != null)
                return this.section;
            this.section = _root.pe().sections().get((int) (sectionNumber() - 1));
            return this.section;
        }
        private byte[] data;
        public byte[] data() {
            if (this.data != null)
                return this.data;
            long _pos = this._io.pos();
            this._io.seek((section().pointerToRawData() + value()));
            this.data = this._io.readBytes(1);
            this._io.seek(_pos);
            return this.data;
        }
        private Annoyingstring nameAnnoying;
        private long value;
        private int sectionNumber;
        private int type;
        private int storageClass;
        private int numberOfAuxSymbols;
        private MicrosoftPe _root;
        private MicrosoftPe.CoffHeader _parent;
        private byte[] _raw_nameAnnoying;
        public Annoyingstring nameAnnoying() { return nameAnnoying; }
        public long value() { return value; }
        public int sectionNumber() { return sectionNumber; }
        public int type() { return type; }
        public int storageClass() { return storageClass; }
        public int numberOfAuxSymbols() { return numberOfAuxSymbols; }
        public MicrosoftPe _root() { return _root; }
        public MicrosoftPe.CoffHeader _parent() { return _parent; }
        public byte[] _raw_nameAnnoying() { return _raw_nameAnnoying; }
    }
    public static class PeHeader extends KaitaiStruct {
        public static PeHeader fromFile(String fileName) throws IOException {
            return new PeHeader(new ByteBufferKaitaiStream(fileName));
        }

        public PeHeader(KaitaiStream _io) {
            this(_io, null, null);
        }

        public PeHeader(KaitaiStream _io, MicrosoftPe _parent) {
            this(_io, _parent, null);
        }

        public PeHeader(KaitaiStream _io, MicrosoftPe _parent, MicrosoftPe _root) {
            super(_io);
            this._parent = _parent;
            this._root = _root;
            _read();
        }
        private void _read() {
            this.peSignature = this._io.ensureFixedContents(new byte[] { 80, 69, 0, 0 });
            this.coffHdr = new CoffHeader(this._io, this, _root);
            this._raw_optionalHdr = this._io.readBytes(coffHdr().sizeOfOptionalHeader());
            KaitaiStream _io__raw_optionalHdr = new ByteBufferKaitaiStream(_raw_optionalHdr);
            this.optionalHdr = new OptionalHeader(_io__raw_optionalHdr, this, _root);
            sections = new ArrayList<Section>((int) (coffHdr().numberOfSections()));
            for (int i = 0; i < coffHdr().numberOfSections(); i++) {
                this.sections.add(new Section(this._io, this, _root));
            }
        }
        private CertificateTable certificateTable;
        public CertificateTable certificateTable() {
            if (this.certificateTable != null)
                return this.certificateTable;
            if (optionalHdr().dataDirs().certificateTable().virtualAddress() != 0) {
                long _pos = this._io.pos();
                this._io.seek(optionalHdr().dataDirs().certificateTable().virtualAddress());
                this._raw_certificateTable = this._io.readBytes(optionalHdr().dataDirs().certificateTable().size());
                KaitaiStream _io__raw_certificateTable = new ByteBufferKaitaiStream(_raw_certificateTable);
                this.certificateTable = new CertificateTable(_io__raw_certificateTable, this, _root);
                this._io.seek(_pos);
            }
            return this.certificateTable;
        }
        private byte[] peSignature;
        private CoffHeader coffHdr;
        private OptionalHeader optionalHdr;
        private ArrayList<Section> sections;
        private MicrosoftPe _root;
        private MicrosoftPe _parent;
        private byte[] _raw_optionalHdr;
        private byte[] _raw_certificateTable;
        public byte[] peSignature() { return peSignature; }
        public CoffHeader coffHdr() { return coffHdr; }
        public OptionalHeader optionalHdr() { return optionalHdr; }
        public ArrayList<Section> sections() { return sections; }
        public MicrosoftPe _root() { return _root; }
        public MicrosoftPe _parent() { return _parent; }
        public byte[] _raw_optionalHdr() { return _raw_optionalHdr; }
        public byte[] _raw_certificateTable() { return _raw_certificateTable; }
    }
    public static class OptionalHeader extends KaitaiStruct {
        public static OptionalHeader fromFile(String fileName) throws IOException {
            return new OptionalHeader(new ByteBufferKaitaiStream(fileName));
        }

        public OptionalHeader(KaitaiStream _io) {
            this(_io, null, null);
        }

        public OptionalHeader(KaitaiStream _io, MicrosoftPe.PeHeader _parent) {
            this(_io, _parent, null);
        }

        public OptionalHeader(KaitaiStream _io, MicrosoftPe.PeHeader _parent, MicrosoftPe _root) {
            super(_io);
            this._parent = _parent;
            this._root = _root;
            _read();
        }
        private void _read() {
            this.std = new OptionalHeaderStd(this._io, this, _root);
            this.windows = new OptionalHeaderWindows(this._io, this, _root);
            this.dataDirs = new OptionalHeaderDataDirs(this._io, this, _root);
        }
        private OptionalHeaderStd std;
        private OptionalHeaderWindows windows;
        private OptionalHeaderDataDirs dataDirs;
        private MicrosoftPe _root;
        private MicrosoftPe.PeHeader _parent;
        public OptionalHeaderStd std() { return std; }
        public OptionalHeaderWindows windows() { return windows; }
        public OptionalHeaderDataDirs dataDirs() { return dataDirs; }
        public MicrosoftPe _root() { return _root; }
        public MicrosoftPe.PeHeader _parent() { return _parent; }
    }
    public static class Section extends KaitaiStruct {
        public static Section fromFile(String fileName) throws IOException {
            return new Section(new ByteBufferKaitaiStream(fileName));
        }

        public Section(KaitaiStream _io) {
            this(_io, null, null);
        }

        public Section(KaitaiStream _io, MicrosoftPe.PeHeader _parent) {
            this(_io, _parent, null);
        }

        public Section(KaitaiStream _io, MicrosoftPe.PeHeader _parent, MicrosoftPe _root) {
            super(_io);
            this._parent = _parent;
            this._root = _root;
            _read();
        }
        private void _read() {
            this.name = new String(KaitaiStream.bytesStripRight(this._io.readBytes(8), (byte) 0), Charset.forName("UTF-8"));
            this.virtualSize = this._io.readU4le();
            this.virtualAddress = this._io.readU4le();
            this.sizeOfRawData = this._io.readU4le();
            this.pointerToRawData = this._io.readU4le();
            this.pointerToRelocations = this._io.readU4le();
            this.pointerToLinenumbers = this._io.readU4le();
            this.numberOfRelocations = this._io.readU2le();
            this.numberOfLinenumbers = this._io.readU2le();
            this.characteristics = this._io.readU4le();
        }
        private byte[] body;
        public byte[] body() {
            if (this.body != null)
                return this.body;
            long _pos = this._io.pos();
            this._io.seek(pointerToRawData());
            this.body = this._io.readBytes(sizeOfRawData());
            this._io.seek(_pos);
            return this.body;
        }
        private String name;
        private long virtualSize;
        private long virtualAddress;
        private long sizeOfRawData;
        private long pointerToRawData;
        private long pointerToRelocations;
        private long pointerToLinenumbers;
        private int numberOfRelocations;
        private int numberOfLinenumbers;
        private long characteristics;
        private MicrosoftPe _root;
        private MicrosoftPe.PeHeader _parent;
        public String name() { return name; }
        public long virtualSize() { return virtualSize; }
        public long virtualAddress() { return virtualAddress; }
        public long sizeOfRawData() { return sizeOfRawData; }
        public long pointerToRawData() { return pointerToRawData; }
        public long pointerToRelocations() { return pointerToRelocations; }
        public long pointerToLinenumbers() { return pointerToLinenumbers; }
        public int numberOfRelocations() { return numberOfRelocations; }
        public int numberOfLinenumbers() { return numberOfLinenumbers; }
        public long characteristics() { return characteristics; }
        public MicrosoftPe _root() { return _root; }
        public MicrosoftPe.PeHeader _parent() { return _parent; }
    }
    public static class CertificateTable extends KaitaiStruct {
        public static CertificateTable fromFile(String fileName) throws IOException {
            return new CertificateTable(new ByteBufferKaitaiStream(fileName));
        }

        public CertificateTable(KaitaiStream _io) {
            this(_io, null, null);
        }

        public CertificateTable(KaitaiStream _io, MicrosoftPe.PeHeader _parent) {
            this(_io, _parent, null);
        }

        public CertificateTable(KaitaiStream _io, MicrosoftPe.PeHeader _parent, MicrosoftPe _root) {
            super(_io);
            this._parent = _parent;
            this._root = _root;
            _read();
        }
        private void _read() {
            this.items = new ArrayList<CertificateEntry>();
            {
                int i = 0;
                while (!this._io.isEof()) {
                    this.items.add(new CertificateEntry(this._io, this, _root));
                    i++;
                }
            }
        }
        private ArrayList<CertificateEntry> items;
        private MicrosoftPe _root;
        private MicrosoftPe.PeHeader _parent;
        public ArrayList<CertificateEntry> items() { return items; }
        public MicrosoftPe _root() { return _root; }
        public MicrosoftPe.PeHeader _parent() { return _parent; }
    }
    public static class MzPlaceholder extends KaitaiStruct {
        public static MzPlaceholder fromFile(String fileName) throws IOException {
            return new MzPlaceholder(new ByteBufferKaitaiStream(fileName));
        }

        public MzPlaceholder(KaitaiStream _io) {
            this(_io, null, null);
        }

        public MzPlaceholder(KaitaiStream _io, MicrosoftPe _parent) {
            this(_io, _parent, null);
        }

        public MzPlaceholder(KaitaiStream _io, MicrosoftPe _parent, MicrosoftPe _root) {
            super(_io);
            this._parent = _parent;
            this._root = _root;
            _read();
        }
        private void _read() {
            this.magic = this._io.ensureFixedContents(new byte[] { 77, 90 });
            this.data1 = this._io.readBytes(58);
            this.ofsPe = this._io.readU4le();
        }
        private byte[] magic;
        private byte[] data1;
        private long ofsPe;
        private MicrosoftPe _root;
        private MicrosoftPe _parent;
        public byte[] magic() { return magic; }
        public byte[] data1() { return data1; }

        /**
         * In PE file, an offset to PE header
         */
        public long ofsPe() { return ofsPe; }
        public MicrosoftPe _root() { return _root; }
        public MicrosoftPe _parent() { return _parent; }
    }
    public static class OptionalHeaderStd extends KaitaiStruct {
        public static OptionalHeaderStd fromFile(String fileName) throws IOException {
            return new OptionalHeaderStd(new ByteBufferKaitaiStream(fileName));
        }

        public OptionalHeaderStd(KaitaiStream _io) {
            this(_io, null, null);
        }

        public OptionalHeaderStd(KaitaiStream _io, MicrosoftPe.OptionalHeader _parent) {
            this(_io, _parent, null);
        }

        public OptionalHeaderStd(KaitaiStream _io, MicrosoftPe.OptionalHeader _parent, MicrosoftPe _root) {
            super(_io);
            this._parent = _parent;
            this._root = _root;
            _read();
        }
        private void _read() {
            this.format = MicrosoftPe.PeFormat.byId(this._io.readU2le());
            this.majorLinkerVersion = this._io.readU1();
            this.minorLinkerVersion = this._io.readU1();
            this.sizeOfCode = this._io.readU4le();
            this.sizeOfInitializedData = this._io.readU4le();
            this.sizeOfUninitializedData = this._io.readU4le();
            this.addressOfEntryPoint = this._io.readU4le();
            this.baseOfCode = this._io.readU4le();
            if (format() == MicrosoftPe.PeFormat.PE32) {
                this.baseOfData = this._io.readU4le();
            }
        }
        private PeFormat format;
        private int majorLinkerVersion;
        private int minorLinkerVersion;
        private long sizeOfCode;
        private long sizeOfInitializedData;
        private long sizeOfUninitializedData;
        private long addressOfEntryPoint;
        private long baseOfCode;
        private Long baseOfData;
        private MicrosoftPe _root;
        private MicrosoftPe.OptionalHeader _parent;
        public PeFormat format() { return format; }
        public int majorLinkerVersion() { return majorLinkerVersion; }
        public int minorLinkerVersion() { return minorLinkerVersion; }
        public long sizeOfCode() { return sizeOfCode; }
        public long sizeOfInitializedData() { return sizeOfInitializedData; }
        public long sizeOfUninitializedData() { return sizeOfUninitializedData; }
        public long addressOfEntryPoint() { return addressOfEntryPoint; }
        public long baseOfCode() { return baseOfCode; }
        public Long baseOfData() { return baseOfData; }
        public MicrosoftPe _root() { return _root; }
        public MicrosoftPe.OptionalHeader _parent() { return _parent; }
    }

    /**
     * @see "3.3. COFF File Header (Object and Image)"
     */
    public static class CoffHeader extends KaitaiStruct {
        public static CoffHeader fromFile(String fileName) throws IOException {
            return new CoffHeader(new ByteBufferKaitaiStream(fileName));
        }

        public enum MachineType {
            UNKNOWN(0),
            I386(332),
            R4000(358),
            WCEMIPSV2(361),
            ALPHA(388),
            SH3(418),
            SH3DSP(419),
            SH4(422),
            SH5(424),
            ARM(448),
            THUMB(450),
            ARMNT(452),
            AM33(467),
            POWERPC(496),
            POWERPCFP(497),
            IA64(512),
            MIPS16(614),
            MIPSFPU(870),
            MIPSFPU16(1126),
            EBC(3772),
            RISCV32(20530),
            RISCV64(20580),
            RISCV128(20776),
            AMD64(34404),
            M32R(36929),
            ARM64(43620);

            private final long id;
            MachineType(long id) { this.id = id; }
            public long id() { return id; }
            private static final Map<Long, MachineType> byId = new HashMap<Long, MachineType>(26);
            static {
                for (MachineType e : MachineType.values())
                    byId.put(e.id(), e);
            }
            public static MachineType byId(long id) { return byId.get(id); }
        }

        public CoffHeader(KaitaiStream _io) {
            this(_io, null, null);
        }

        public CoffHeader(KaitaiStream _io, MicrosoftPe.PeHeader _parent) {
            this(_io, _parent, null);
        }

        public CoffHeader(KaitaiStream _io, MicrosoftPe.PeHeader _parent, MicrosoftPe _root) {
            super(_io);
            this._parent = _parent;
            this._root = _root;
            _read();
        }
        private void _read() {
            this.machine = MachineType.byId(this._io.readU2le());
            this.numberOfSections = this._io.readU2le();
            this.timeDateStamp = this._io.readU4le();
            this.pointerToSymbolTable = this._io.readU4le();
            this.numberOfSymbols = this._io.readU4le();
            this.sizeOfOptionalHeader = this._io.readU2le();
            this.characteristics = this._io.readU2le();
        }
        private Integer symbolTableSize;
        public Integer symbolTableSize() {
            if (this.symbolTableSize != null)
                return this.symbolTableSize;
            int _tmp = (int) ((numberOfSymbols() * 18));
            this.symbolTableSize = _tmp;
            return this.symbolTableSize;
        }
        private Integer symbolNameTableOffset;
        public Integer symbolNameTableOffset() {
            if (this.symbolNameTableOffset != null)
                return this.symbolNameTableOffset;
            int _tmp = (int) ((pointerToSymbolTable() + symbolTableSize()));
            this.symbolNameTableOffset = _tmp;
            return this.symbolNameTableOffset;
        }
        private Long symbolNameTableSize;
        public Long symbolNameTableSize() {
            if (this.symbolNameTableSize != null)
                return this.symbolNameTableSize;
            long _pos = this._io.pos();
            this._io.seek(symbolNameTableOffset());
            this.symbolNameTableSize = this._io.readU4le();
            this._io.seek(_pos);
            return this.symbolNameTableSize;
        }
        private ArrayList<CoffSymbol> symbolTable;
        public ArrayList<CoffSymbol> symbolTable() {
            if (this.symbolTable != null)
                return this.symbolTable;
            long _pos = this._io.pos();
            this._io.seek(pointerToSymbolTable());
            symbolTable = new ArrayList<CoffSymbol>((int) (numberOfSymbols()));
            for (int i = 0; i < numberOfSymbols(); i++) {
                this.symbolTable.add(new CoffSymbol(this._io, this, _root));
            }
            this._io.seek(_pos);
            return this.symbolTable;
        }
        private MachineType machine;
        private int numberOfSections;
        private long timeDateStamp;
        private long pointerToSymbolTable;
        private long numberOfSymbols;
        private int sizeOfOptionalHeader;
        private int characteristics;
        private MicrosoftPe _root;
        private MicrosoftPe.PeHeader _parent;
        public MachineType machine() { return machine; }
        public int numberOfSections() { return numberOfSections; }
        public long timeDateStamp() { return timeDateStamp; }
        public long pointerToSymbolTable() { return pointerToSymbolTable; }
        public long numberOfSymbols() { return numberOfSymbols; }
        public int sizeOfOptionalHeader() { return sizeOfOptionalHeader; }
        public int characteristics() { return characteristics; }
        public MicrosoftPe _root() { return _root; }
        public MicrosoftPe.PeHeader _parent() { return _parent; }
    }
    public static class Annoyingstring extends KaitaiStruct {
        public static Annoyingstring fromFile(String fileName) throws IOException {
            return new Annoyingstring(new ByteBufferKaitaiStream(fileName));
        }

        public Annoyingstring(KaitaiStream _io) {
            this(_io, null, null);
        }

        public Annoyingstring(KaitaiStream _io, MicrosoftPe.CoffSymbol _parent) {
            this(_io, _parent, null);
        }

        public Annoyingstring(KaitaiStream _io, MicrosoftPe.CoffSymbol _parent, MicrosoftPe _root) {
            super(_io);
            this._parent = _parent;
            this._root = _root;
            _read();
        }
        private void _read() {
        }
        private String nameFromOffset;
        public String nameFromOffset() {
            if (this.nameFromOffset != null)
                return this.nameFromOffset;
            if (nameZeroes() == 0) {
                KaitaiStream io = _root._io();
                long _pos = io.pos();
                io.seek((nameZeroes() == 0 ? (_parent()._parent().symbolNameTableOffset() + nameOffset()) : 0));
                this.nameFromOffset = new String(io.readBytesTerm(0, false, true, false), Charset.forName("ascii"));
                io.seek(_pos);
            }
            return this.nameFromOffset;
        }
        private Long nameOffset;
        public Long nameOffset() {
            if (this.nameOffset != null)
                return this.nameOffset;
            long _pos = this._io.pos();
            this._io.seek(4);
            this.nameOffset = this._io.readU4le();
            this._io.seek(_pos);
            return this.nameOffset;
        }
        private String name;
        public String name() {
            if (this.name != null)
                return this.name;
            this.name = (nameZeroes() == 0 ? nameFromOffset() : nameFromShort());
            return this.name;
        }
        private Long nameZeroes;
        public Long nameZeroes() {
            if (this.nameZeroes != null)
                return this.nameZeroes;
            long _pos = this._io.pos();
            this._io.seek(0);
            this.nameZeroes = this._io.readU4le();
            this._io.seek(_pos);
            return this.nameZeroes;
        }
        private String nameFromShort;
        public String nameFromShort() {
            if (this.nameFromShort != null)
                return this.nameFromShort;
            if (nameZeroes() != 0) {
                long _pos = this._io.pos();
                this._io.seek(0);
                this.nameFromShort = new String(this._io.readBytesTerm(0, false, true, false), Charset.forName("ascii"));
                this._io.seek(_pos);
            }
            return this.nameFromShort;
        }
        private MicrosoftPe _root;
        private MicrosoftPe.CoffSymbol _parent;
        public MicrosoftPe _root() { return _root; }
        public MicrosoftPe.CoffSymbol _parent() { return _parent; }
    }
    private PeHeader pe;
    public PeHeader pe() {
        if (this.pe != null)
            return this.pe;
        long _pos = this._io.pos();
        this._io.seek(mz().ofsPe());
        this.pe = new PeHeader(this._io, this, _root);
        this._io.seek(_pos);
        return this.pe;
    }
    private MzPlaceholder mz;
    private MicrosoftPe _root;
    private KaitaiStruct _parent;
    public MzPlaceholder mz() { return mz; }
    public MicrosoftPe _root() { return _root; }
    public KaitaiStruct _parent() { return _parent; }
}
