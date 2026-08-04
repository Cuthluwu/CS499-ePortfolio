package edu.snhu.cs499.model;

import static org.junit.jupiter.api.Assertions.*;

import java.lang.reflect.Method;
import java.util.Calendar;
import java.util.Date;

import org.junit.jupiter.api.Test;

class AppointmentOriginalRequirementsTest {

    private Date daysFromToday(int days) {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_YEAR, days);
        return calendar.getTime();
    }

    @Test
    public void testAppointmentCreatedWithValidFields() {
        Date date = daysFromToday(7);
        Appointment appointment = new Appointment("A100", date, "Yearly eye exam");

        assertEquals("A100", appointment.getAppointmentId());
        assertEquals(date, appointment.getAppointmentDate());
        assertEquals("Yearly eye exam", appointment.getDescription());
    }

    @Test
    public void testAppointmentIdAcceptsTenCharacters() {
        Appointment appointment = new Appointment("1234567890", daysFromToday(3), "Valid description");

        assertEquals("1234567890", appointment.getAppointmentId());
    }

    @Test
    public void testAppointmentIdCannotBeNull() {
        assertThrows(IllegalArgumentException.class, () ->
            new Appointment(null, daysFromToday(3), "Valid description"));
    }

    @Test
    public void testAppointmentIdCannotBeLongerThanTenCharacters() {
        assertThrows(IllegalArgumentException.class, () ->
            new Appointment("APPT1234567", daysFromToday(3), "Valid description"));
    }

    @Test
    public void testAppointmentIdIsNotUpdatable() {
        for (Method method : Appointment.class.getDeclaredMethods()) {
            assertNotEquals("setAppointmentId", method.getName());
        }
    }

    @Test
    public void testAppointmentDateCannotBeNull() {
        assertThrows(IllegalArgumentException.class, () ->
            new Appointment("A101", null, "Valid description"));
    }

    @Test
    public void testAppointmentDateCannotBeInThePast() {
        assertThrows(IllegalArgumentException.class, () ->
            new Appointment("A102", daysFromToday(-3), "Valid description"));
    }

    @Test
    public void testDescriptionAcceptsFiftyCharacters() {
        String description = "12345678901234567890123456789012345678901234567890";
        Appointment appointment = new Appointment("A103", daysFromToday(3), description);

        assertEquals(description, appointment.getDescription());
    }

    @Test
    public void testDescriptionCannotBeNull() {
        assertThrows(IllegalArgumentException.class, () ->
            new Appointment("A104", daysFromToday(3), null));
    }

    @Test
    public void testDescriptionCannotBeLongerThanFiftyCharacters() {
        assertThrows(IllegalArgumentException.class, () ->
            new Appointment("A105", daysFromToday(3), "This appointment description goes beyond the fifty character limit."));
    }

    @Test
    public void testAppointmentDateCanBeUpdatedToFutureDate() {
        Appointment appointment = new Appointment("A106", daysFromToday(3), "Original appointment");
        Date newDate = daysFromToday(14);

        appointment.setAppointmentDate(newDate);

        assertEquals(newDate, appointment.getAppointmentDate());
    }

    @Test
    public void testAppointmentDateCannotBeUpdatedToPastDate() {
        Appointment appointment = new Appointment("A107", daysFromToday(3), "Original appointment");

        assertThrows(IllegalArgumentException.class, () -> appointment.setAppointmentDate(daysFromToday(-1)));
    }

    @Test
    public void testDescriptionCanBeUpdated() {
        Appointment appointment = new Appointment("A108", daysFromToday(3), "Original appointment");

        appointment.setDescription("Updated appointment note");

        assertEquals("Updated appointment note", appointment.getDescription());
    }

    @Test
    public void testDescriptionCannotBeUpdatedToInvalidValue() {
        Appointment appointment = new Appointment("A109", daysFromToday(3), "Original appointment");

        assertThrows(IllegalArgumentException.class, () -> appointment.setDescription(null));
        assertThrows(IllegalArgumentException.class, () ->
            appointment.setDescription("This updated appointment description is too long for the allowed field."));
    }

    @Test
    public void testAppointmentDateIsDefensivelyCopied() {
        Date date = daysFromToday(5);
        Appointment appointment = new Appointment("A110", date, "Defensive copy check");

        date.setTime(daysFromToday(-5).getTime());

        assertTrue(appointment.getAppointmentDate().after(new Date()));
    }
}
