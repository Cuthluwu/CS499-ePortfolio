package com.example.project.model;

/** Immutable result returned by the database repository. */
public final class WeightEntry {
    private final long entryId;
    private final long userId;
    private final double weight;
    private final String entryDate;
    private final String note;

    public WeightEntry(long entryId, long userId, double weight, String entryDate, String note) {
        this.entryId = entryId;
        this.userId = userId;
        this.weight = weight;
        this.entryDate = entryDate;
        this.note = note;
    }

    public long getEntryId() {
        return entryId;
    }

    public long getUserId() {
        return userId;
    }

    public double getWeight() {
        return weight;
    }

    public String getEntryDate() {
        return entryDate;
    }

    public String getNote() {
        return note;
    }
}
