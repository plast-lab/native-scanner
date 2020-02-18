package org.clyze.scanner;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class BasicTest {

    private static String getTestNativeLibrary() {
        final String LIB_RESOURCE = "/libjackpal-androidterm5.so";
        try {
            Path tmpPath = Files.createTempFile("lib", ".so");
            InputStream resourceAsStream = BasicTest.class.getResourceAsStream(LIB_RESOURCE);
            Files.copy(resourceAsStream, tmpPath, StandardCopyOption.REPLACE_EXISTING);
            return tmpPath.toString();
        } catch (IOException ex) {
            ex.printStackTrace();
            throw new RuntimeException("Error: could not extract " + LIB_RESOURCE);
        }
    }

    @SuppressWarnings("ConstantConditions")
    @Test
    public void libraryStringsMatch() {
        CheckPair p = new CheckPair();
        NativeDatabaseConsumer dbc = (predicateFile, arg, args) -> {
            // System.out.println(predicateFile + "('" + args[1] + "')");
            if (predicateFile.equals(BinaryAnalysis.NATIVE_METHODTYPE_CANDIDATE) &&
                args[1].equals("(IIIII)V")) {
                System.out.println("Found: " + args[1]);
                p.methodTypeFound = true;
            } else if (predicateFile.equals(BinaryAnalysis.NATIVE_NAME_CANDIDATE) &&
                       args[1].equals("setPtyWindowSizeInternal")) {
                System.out.println("Found: " + args[1]);
                p.methodNameFound = true;
            }
        };
        // NativeDatabaseConsumer dbc = new BasicDatabaseConsumer();

        String libPath = getTestNativeLibrary();
        boolean radareMode = false;
        boolean onlyPreciseStrings = false;
        boolean truncateAddresses = false;
        boolean demangle = false;
        BinaryAnalysis analysis = NativeScanner.create(dbc, radareMode, libPath, onlyPreciseStrings, truncateAddresses, demangle);
        (new NativeScanner(null)).scanBinaryCode(analysis);

        assertTrue(p.methodNameFound);
        assertTrue(p.methodTypeFound);
    }
}
