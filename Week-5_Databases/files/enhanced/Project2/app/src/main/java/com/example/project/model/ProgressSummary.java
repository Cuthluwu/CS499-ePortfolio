package com.example.project.model;

/** Earliest-to-latest progress computed from user-scoped database queries. */
public final class ProgressSummary {
    private final int entryCount;
    private final Double firstWeight;
    private final String firstDate;
    private final Double latestWeight;
    private final String latestDate;

    public ProgressSummary(
            int entryCount,
            Double firstWeight,
            String firstDate,
            Double latestWeight,
            String latestDate) {
        this.entryCount = entryCount;
        this.firstWeight = firstWeight;
        this.firstDate = firstDate;
        this.latestWeight = latestWeight;
        this.latestDate = latestDate;
    }

    public int getEntryCount() {
        return entryCount;
    }

    public Double getFirstWeight() {
        return firstWeight;
    }

    public String getFirstDate() {
        return firstDate;
    }

    public Double getLatestWeight() {
        return latestWeight;
    }

    public String getLatestDate() {
        return latestDate;
    }

    public boolean hasData() {
        return entryCount > 0 && firstWeight != null && latestWeight != null;
    }

    public double getChange() {
        return hasData() ? latestWeight - firstWeight : 0.0;
    }
}
