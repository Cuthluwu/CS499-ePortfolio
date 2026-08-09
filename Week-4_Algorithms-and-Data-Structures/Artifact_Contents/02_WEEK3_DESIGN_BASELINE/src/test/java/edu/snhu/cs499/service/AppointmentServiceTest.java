package edu.snhu.cs499.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import edu.snhu.cs499.exception.DuplicateRecordException;
import edu.snhu.cs499.exception.RecordNotFoundException;
import edu.snhu.cs499.model.Appointment;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.Date;
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

    private Appointment appointment(String identifier, long secondsFromNow, String description) {
        return new Appointment(identifier, Date.from(FIXED_CLOCK.instant().plusSeconds(secondsFromNow)),
                description, FIXED_CLOCK);
    }
}
