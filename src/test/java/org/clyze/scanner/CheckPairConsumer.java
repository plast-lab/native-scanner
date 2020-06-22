package org.clyze.scanner;

class CheckPairConsumer implements NativeDatabaseConsumer {
    public final String mName;
    public final String mType;
    public boolean methodNameFound = false;
    public boolean methodTypeFound = false;

    public CheckPairConsumer(String mName, String mType) {
        this.mName = mName;
        this.mType = mType;
    }

    @Override
    public void add(String predicateFile, String arg, String... args) {
        // System.out.println(predicateFile + "('" + args[1] + "')");
        if (predicateFile.equals(BinaryAnalysis.NATIVE_METHODTYPE_CANDIDATE) &&
                args[1].equals(mType)) {
            System.out.println("Found: " + args[1]);
            methodTypeFound = true;
        } else if (predicateFile.equals(BinaryAnalysis.NATIVE_NAME_CANDIDATE) &&
                args[1].equals(mName)) {
            System.out.println("Found: " + args[1]);
            methodNameFound = true;
        }
    }
}
