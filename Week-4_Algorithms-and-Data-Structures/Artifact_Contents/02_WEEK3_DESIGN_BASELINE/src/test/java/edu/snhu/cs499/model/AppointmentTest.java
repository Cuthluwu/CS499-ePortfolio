package edu.snhu.cs499.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.Date;
import org.junit.jupiter.api.Test;

class AppointmentTest {
    private static final Clock FIXED_CLOCK = Clock.fixed(
            Instant.parse("2026-08-03T12:00:00Z"), ZoneOffset.UTC);

    @Test
    void fixedClockMakesDateBoundaryDeterministic() {
        Date boundary = Date.from(FIXED_CLOCK.instant());
        Appointment appointment = new Appointment("A100", boundary, "Checkup", FIXED_CLOCK);

        assertEquals(boundary, appointment.getAppointmentDate());
        assertThrows(IllegalArgumentException.class, () -> new Appointment(
                "A101", Date.from(FIXED_CLOCK.instant().minusSeconds(1)), "Past", FIXED_CLOCK));
    }

    @Test
    void dateIsDefensivelyCopiedOnInputAndOutput() {
        Date input = Date.from(FIXED_CLOCK.instant().plusSeconds(3600));
        Appointment appointment = new Appointment("A102", input, "Checkup", FIXED_CLOCK);

        input.setTime(0);
        Date output = appointment.getAppointmentDate();
        output.setTime(0);

        assertEquals(Date.from(FIXED_CLOCK.instant().plusSeconds(3600)), appointment.getAppointmentDate());
    }

    @Test
    void descriptionRejectsBlankAndExcessiveText() {
        Date future = Date.from(FIXED_CLOCK.instant().plusSeconds(3600));
        assertThrows(IllegalArgumentException.class,
                () -> new Appointment("A103", future, " ", FIXED_CLOCK));
        assertThrows(IllegalArgumentException.class,
                () -> new Appointment("A103", future,
                        "This appointment description exceeds the required fifty character limit.", FIXED_CLOCK));
    }
}
