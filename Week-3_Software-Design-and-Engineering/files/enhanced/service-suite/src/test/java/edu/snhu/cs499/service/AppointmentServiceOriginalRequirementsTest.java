package edu.snhu.cs499.service;

import edu.snhu.cs499.model.Appointment;
import static org.junit.jupiter.api.Assertions.*;

import java.util.Calendar;
import java.util.Date;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class AppointmentServiceOriginalRequirementsTest {
    private AppointmentService service;

    private Date futureDate() {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_YEAR, 4);
        return calendar.getTime();
    }

    @BeforeEach
    public void setUp() {
        service = new AppointmentService();
    }

    @Test
    public void testAddAppointmentWithUniqueId() {
        Appointment appointment = new Appointment("A200", futureDate(), "Morning checkup");

        service.add(appointment);

        assertEquals(1, service.size());
        assertEquals(appointment, service.findById("A200").orElse(null));
    }

    @Test
    public void testCannotAddDuplicateAppointmentId() {
        Appointment first = new Appointment("A201", futureDate(), "Morning checkup");
        Appointment duplicate = new Appointment("A201", futureDate(), "Afternoon checkup");

        service.add(first);

        assertThrows(IllegalArgumentException.class, () -> service.add(duplicate));
        assertEquals(1, service.size());
    }

    @Test
    public void testCannotAddNullAppointment() {
        assertThrows(IllegalArgumentException.class, () -> service.add(null));
    }

    @Test
    public void testDeleteAppointmentById() {
        Appointment appointment = new Appointment("A202", futureDate(), "Dental cleaning");

        service.add(appointment);
        service.deleteById("A202");

        assertEquals(0, service.size());
        assertTrue(service.findById("A202").isEmpty());
    }

    @Test
    public void testCannotDeleteMissingAppointmentId() {
        assertThrows(IllegalArgumentException.class, () -> service.deleteById("MISSING"));
    }

    @Test
    public void testMultipleAppointmentsRemainIndependent() {
        Appointment first = new Appointment("A203", futureDate(), "Vet appointment");
        Appointment second = new Appointment("A204", futureDate(), "Advising meeting");

        service.add(first);
        service.add(second);
        service.deleteById("A203");

        assertEquals(1, service.size());
        assertTrue(service.findById("A203").isEmpty());
        assertEquals(second, service.findById("A204").orElse(null));
    }
}
