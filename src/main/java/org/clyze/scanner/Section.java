package org.clyze.scanner;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.*;

/**
 * A representation of a section in a binary.
 */
public class Section {
    private final String name;
    private final String lib;
    private final int size;
    private final long vma;
    private final long offset;
    private SortedMap<Long, String> foundStrings;
    private final Set<Long> words;

    Section(String name, String lib, int size, long vma, long offset) {
        this.name = name;
        this.lib = lib;
        this.size = size;
        this.vma = vma;
        this.offset = offset;
        System.out.println("Section " + name + ": offset = " + offset + ", size = " + size + ", vma = " + vma);
        this.words = new HashSet<>();
    }

    /**
     * Read section data from the library file.
     *
     * @return the data as a byte array
     */
    private byte[] readData() {
        try {
            RandomAccessFile raf = new RandomAccessFile(lib, "r");
            raf.seek(offset);
            byte[] data = new byte[size];
            raf.readFully(data);
            System.out.println("Section fully read (offset=" + offset + ", size=" + size + "): " + name);
            return data;
        } catch (IOException ex) {
            System.err.println("Failed to read section " + name + " (offset=" + offset + ", size=" + size + "): " + ex.getMessage());
            ex.printStackTrace();
            return new byte[0];
        }
    }

    /**
     * Scan the 'data' buffer for NULL-terminated strings.
     *
     * @return a collection of the strings found
     */
    SortedMap<Long, String> strings() {
        if (this.foundStrings != null)
            return this.foundStrings;

        this.foundStrings = new TreeMap<>();
        try {
            byte[] data = readData();
            StringBuilder foundString = new StringBuilder();
            long addr = vma;
            for (int i = 0; i < data.length; i++)
                if (data[i] == 0) {
                    if (!foundString.toString().equals("")) {
                        foundStrings.put(addr, foundString.toString());
                        foundString = new StringBuilder();
                    }
                    // Compute address of next string.
                    addr = vma + i + 1;
                } else
                    foundString.append((char) data[i]);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return this.foundStrings;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder("Section [lib = " + lib + ", vma = " + vma + ", file offset = " + offset + ", size = " + size + "]\n");
        strings().forEach((Long addr, String s) -> sb.append(addr).append(": String '").append(s).append("'\n"));
        return sb.toString();
    }

    /**
     * Return the data as a set of machine-word pointers.
     *
     * @param wordSize       the word size of the architecture
     * @param littleEndian   the endianness of the architecture
     * @return a set of machine word values
     */
    public Set<Long> analyzeWords(int wordSize, boolean littleEndian) {

        int countSize = size;
        if (size % wordSize != 0) {
            int size2 = (size / wordSize) * wordSize;
            System.err.println("Section size " + size + " not a multiple of " + wordSize + ", reading only first " + size2 + " bytes.");
            countSize = size2;
        }

        int[] factors = new int[wordSize];
        for (int i = 0; i < wordSize; i++)
            if (littleEndian)
                factors[i] = 1 << (i * wordSize);
            else
                factors[wordSize - i - 1] = 1 << (i * wordSize);

        byte[] data = readData();
        for (int offset = 0; offset < countSize; offset += wordSize) {
            long value = 0;
            for (int i = 0; i < wordSize; i++)
                value += (long) factors[i] * data[offset + i];
            words.add(value);
        }
        System.err.println("Words in data section: " + words.size());
        return words;
    }
}
