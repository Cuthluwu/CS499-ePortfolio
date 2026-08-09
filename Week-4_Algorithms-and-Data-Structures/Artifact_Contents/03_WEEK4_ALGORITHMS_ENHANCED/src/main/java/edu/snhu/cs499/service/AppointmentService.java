package edu.snhu.cs499.service;

import edu.snhu.cs499.exception.DuplicateRecordException;
import edu.snhu.cs499.exception.RecordNotFoundException;
import edu.snhu.cs499.model.Appointment;
import edu.snhu.cs499.repository.RecordRepository;
import edu.snhu.cs499.validation.Validation;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/** Manages appointments without exposing mutable Date values from internal records. */
public final class AppointmentService implements RecordRepository<Appointment> {
    private static final Comparator<Appointment> DATE_ORDER = Comparator
            .comparingLong(Appointment::getAppointmentTimeMillis)
            .thenComparing(Appointment::getAppointmentId);

    private final Map<String, Appointment> appointments = new HashMap<>();

    @Override
    public void add(Appointment appointment) {
        if (appointment == null) {
            throw new IllegalArgumentException("Appointment cannot be null.");
        }
        String identifier = appointment.getAppointmentId();
        if (appointments.containsKey(identifier)) {
            throw new DuplicateRecordException("Appointment", identifier);
        }
        appointments.put(identifier, new Appointment(appointment));
    }

    @Override
    public Optional<Appointment> findById(String identifier) {
        String normalized = Validation.identifier(identifier, "Appointment ID");
        return Optional.ofNullable(appointments.get(normalized)).map(Appointment::new);
    }

    @Override
    public void deleteById(String identifier) {
        String normalized = Validation.identifier(identifier, "Appointment ID");
        if (appointments.remove(normalized) == null) {
            throw new RecordNotFoundException("Appointment", normalized);
        }
    }

    public void update(String identifier, Date appointmentDate, String description) {
        Appointment updated = new Appointment(requireStored(identifier));
        updated.setAppointmentDate(appointmentDate);
        updated.setDescription(description);
        appointments.put(updated.getAppointmentId(), updated);
    }

    /**
     * Returns appointments in an inclusive date range, ordered by date and ID.
     */
    public List<Appointment> findByDateRange(Date startInclusive, Date endInclusive) {
        if (startInclusive == null || endInclusive == null) {
            throw new IllegalArgumentException("Start and end dates are required.");
        }
        long startMillis = startInclusive.getTime();
        long endMillis = endInclusive.getTime();
        if (startMillis > endMillis) {
            throw new IllegalArgumentException("Start date cannot be after end date.");
        }

        return appointments.values().stream()
                .filter(appointment -> appointment.getAppointmentTimeMillis() >= startMillis
                        && appointment.getAppointmentTimeMillis() <= endMillis)
                .map(Appointment::new)
                .sorted(DATE_ORDER)
                .toList();
    }

    @Override
    public int size() {
        return appointments.size();
    }

    private Appointment requireStored(String identifier) {
        String normalized = Validation.identifier(identifier, "Appointment ID");
        Appointment appointment = appointments.get(normalized);
        if (appointment == null) {
            throw new RecordNotFoundException("Appointment", normalized);
        }
        return appointment;
    }
}
