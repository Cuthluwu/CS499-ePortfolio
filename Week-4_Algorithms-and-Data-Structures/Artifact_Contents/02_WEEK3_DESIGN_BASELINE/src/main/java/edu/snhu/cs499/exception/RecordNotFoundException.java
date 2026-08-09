package edu.snhu.cs499.exception;

/** Indicates that a requested service record does not exist. */
public class RecordNotFoundException extends IllegalArgumentException {
    private static final long serialVersionUID = 1L;

    public RecordNotFoundException(String recordType, String identifier) {
        super(recordType + " was not found for ID: " + identifier);
    }
}
