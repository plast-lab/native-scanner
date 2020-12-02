package org.clyze.scanner;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class BasicTest {

    private static String getTestELFLibrary() {
        return getTestNativeLibrary("/libjackpal-androidterm5.so", ".so");
    }

    private static String getTestDLL() {
        return getTestNativeLibrary("/localfile_1_0_0.dll", ".dll");
    }

    private static String getTestNativeLibrary(String libResource, String extension) {
        try {
            Path tmpPath = Files.createTempFile("lib", extension);
            tmpPath.toFile().deleteOnExit();
            InputStream resourceAsStream = BasicTest.class.getResourceAsStream(libResource);
            Files.copy(resourceAsStream, tmpPath, StandardCopyOption.REPLACE_EXISTING);
            return tmpPath.toString();
        } catch (IOException ex) {
            ex.printStackTrace();
            throw new RuntimeException("Error: could not extract " + libResource);
        }
    }

    @Test
    public void libraryStringsMatch_ELF_Builtin() {
        libraryStringsMatch_ELF(BinaryAnalysis.AnalysisType.BUILTIN);
    }

    @Test
    public void libraryStringsMatch_ELF_Binutils() {
        libraryStringsMatch_ELF(BinaryAnalysis.AnalysisType.BINUTILS);
    }

    @Test
    public void libraryStringsMatch_ELF_Radare() {
        libraryStringsMatch_ELF(BinaryAnalysis.AnalysisType.RADARE);
    }

    public void libraryStringsMatch_ELF(BinaryAnalysis.AnalysisType aType) {
        libraryStringsMatch(aType, new CheckPairConsumer("setPtyWindowSizeInternal", "(IIIII)V"), getTestELFLibrary());
    }

    @Test
    public void libraryStringsMatch_DLL_Builtin() {
        libraryStringsMatch_DLL(BinaryAnalysis.AnalysisType.BUILTIN);
    }

    @Test
    public void libraryStringsMatch_DLL_Binutils() {
        libraryStringsMatch_DLL(BinaryAnalysis.AnalysisType.BINUTILS);
    }

    @Test
    public void libraryStringsMatch_DLL_Radare() {
        libraryStringsMatch_DLL(BinaryAnalysis.AnalysisType.RADARE);
    }

    public void libraryStringsMatch_DLL(BinaryAnalysis.AnalysisType aType) {
        libraryStringsMatch(aType, new CheckPairConsumer("setName", "(Ljava/lang/String;)V"), getTestDLL());
    }

    @SuppressWarnings("ConstantConditions")
    public void libraryStringsMatch(BinaryAnalysis.AnalysisType analysisType, CheckPairConsumer p, String libPath) {
        System.out.println("Testing " + libPath + " [mode: " + analysisType.name() + "]");
        boolean onlyPreciseStrings = false;
        boolean truncateAddresses = false;
        boolean demangle = false;
        BinaryAnalysis analysis = NativeScanner.create(p, analysisType, libPath, onlyPreciseStrings, truncateAddresses, demangle);
        (new NativeScanner(true, null)).scanBinaryCode(analysis);

        assertTrue(p.methodNameFound);
        assertTrue(p.methodTypeFound);
    }
}
