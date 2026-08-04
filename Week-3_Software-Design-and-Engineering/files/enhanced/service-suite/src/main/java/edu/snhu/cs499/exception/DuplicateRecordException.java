package edu.snhu.cs499.exception;

/** Indicates that a service already contains the requested identifier. */
public class DuplicateRecordException extends IllegalArgumentException {
    private static final long serialVersionUID = 1L;

    public DuplicateRecordException(String recordType, String identifier) {
        super(recordType + " already exists for ID: " + identifier);
    }
}
