package edu.snhu.cs499.repository;

import java.util.Optional;

/** Defines the operations shared by the in-memory record services. */
public interface RecordRepository<T> {
    void add(T record);

    Optional<T> findById(String identifier);

    void deleteById(String identifier);

    int size();
}
