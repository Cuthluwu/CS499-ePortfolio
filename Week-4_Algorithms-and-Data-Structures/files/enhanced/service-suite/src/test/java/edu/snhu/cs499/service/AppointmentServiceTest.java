package edu.snhu.cs499.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertIterableEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import edu.snhu.cs499.exception.DuplicateRecordException;
import edu.snhu.cs499.exception.RecordNotFoundException;
import edu.snhu.cs499.model.Appointment;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.Date;
import java.util.List;
import org.junit.jupiter.api.Test;

class AppointmentServiceTest {
    private static final Clock FIXED_CLOCK = Clock.fixed(
            Instant.parse("2026-08-03T12:00:00Z"), ZoneOffset.UTC);

    @Test
    void serviceCopiesAppointmentsAndProvidesConsistentCrud() {
        AppointmentService service = new AppointmentService();
        Appointment appointment = appointment("A100", 3600, "Original");
        service.add(appointment);
        appointment.setDescription("Changed Outside");

        assertEquals("Original", service.findById("A100").orElseThrow().getDescription());
        assertThrows(DuplicateRecordException.class, () -> service.add(appointment("A100", 7200, "Duplicate")));

        service.update("A100", Date.from(FIXED_CLOCK.instant().plusSeconds(10800)), "Updated");
        assertEquals("Updated", service.findById("A100").orElseThrow().getDescription());

        service.deleteById("A100");
        assertEquals(0, service.size());
        assertThrows(RecordNotFoundException.class, () -> service.deleteById("A100"));
    }

    @Test
    void multiFieldUpdateIsAtomicWhenValidationFails() {
        AppointmentService service = new AppointmentService();
        Appointment original = appointment("A101", 3600, "Original");
        service.add(original);

        assertThrows(IllegalArgumentException.class, () -> service.update(
                "A101", Date.from(FIXED_CLOCK.instant().plusSeconds(7200)),
                "This invalid appointment description exceeds the fifty character field limit."));

        Appointment unchanged = service.findById("A101").orElseThrow();
        assertEquals(original.getAppointmentDate(), unchanged.getAppointmentDate());
        assertEquals("Original", unchanged.getDescription());
    }

    @Test
    void dateRangeIsInclusiveAndUsesIdAsTieBreaker() {
        AppointmentService service = new AppointmentService();
        service.add(appointment("A203", 7200, "Second at same time"));
        service.add(appointment("A201", 3600, "First"));
        service.add(appointment("A202", 7200, "First at same time"));
        service.add(appointment("A204", 14400, "Outside"));

        Date start = Date.from(FIXED_CLOCK.instant().plusSeconds(3600));
        Date end = Date.from(FIXED_CLOCK.instant().plusSeconds(7200));
        List<String> identifiers = service.findByDateRange(start, end).stream()
                .map(Appointment::getAppointmentId)
                .toList();

        assertIterableEquals(List.of("A201", "A202", "A203"), identifiers);
    }

    @Test
    void dateRangeRejectsInvalidBoundsAndReturnsImmutableSnapshots() {
        AppointmentService service = new AppointmentService();
        service.add(appointment("A205", 3600, "Included"));
        Date start = Date.from(FIXED_CLOCK.instant());
        Date end = Date.from(FIXED_CLOCK.instant().plusSeconds(7200));

        List<Appointment> results = service.findByDateRange(start, end);
        results.get(0).setDescription("Changed Copy");

        assertEquals("Included", service.findById("A205").orElseThrow().getDescription());
        assertThrows(UnsupportedOperationException.class, () -> results.clear());
        assertThrows(IllegalArgumentException.class, () -> service.findByDateRange(end, start));
        assertThrows(IllegalArgumentException.class, () -> service.findByDateRange(null, end));
    }

    private Appointment appointment(String identifier, long secondsFromNow, String description) {
        return new Appointment(identifier, Date.from(FIXED_CLOCK.instant().plusSeconds(secondsFromNow)),
                description, FIXED_CLOCK);
    }
}
