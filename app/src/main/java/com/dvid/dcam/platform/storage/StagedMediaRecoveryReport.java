package com.dvid.dcam.platform.storage;

public final class StagedMediaRecoveryReport {
    private final int recovered;
    private final int preserved;
    private final int duplicates;

    StagedMediaRecoveryReport(int recovered, int preserved, int duplicates) {
        this.recovered = recovered;
        this.preserved = preserved;
        this.duplicates = duplicates;
    }

    public int getRecovered() { return recovered; }
    public int getPreserved() { return preserved; }
    public int getDuplicates() { return duplicates; }
}
