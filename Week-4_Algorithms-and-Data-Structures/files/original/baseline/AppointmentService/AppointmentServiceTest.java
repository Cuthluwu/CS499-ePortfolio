import static org.junit.jupiter.api.Assertions.*;

import java.util.Calendar;
import java.util.Date;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class AppointmentServiceTest {
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

        service.addAppointment(appointment);

        assertEquals(1, service.getAppointmentCount());
        assertEquals(appointment, service.getAppointment("A200"));
    }

    @Test
    public void testCannotAddDuplicateAppointmentId() {
        Appointment first = new Appointment("A201", futureDate(), "Morning checkup");
        Appointment duplicate = new Appointment("A201", futureDate(), "Afternoon checkup");

        service.addAppointment(first);

        assertThrows(IllegalArgumentException.class, () -> service.addAppointment(duplicate));
        assertEquals(1, service.getAppointmentCount());
    }

    @Test
    public void testCannotAddNullAppointment() {
        assertThrows(IllegalArgumentException.class, () -> service.addAppointment(null));
    }

    @Test
    public void testDeleteAppointmentById() {
        Appointment appointment = new Appointment("A202", futureDate(), "Dental cleaning");

        service.addAppointment(appointment);
        service.deleteAppointment("A202");

        assertEquals(0, service.getAppointmentCount());
        assertNull(service.getAppointment("A202"));
    }

    @Test
    public void testCannotDeleteMissingAppointmentId() {
        assertThrows(IllegalArgumentException.class, () -> service.deleteAppointment("MISSING"));
    }

    @Test
    public void testMultipleAppointmentsRemainIndependent() {
        Appointment first = new Appointment("A203", futureDate(), "Vet appointment");
        Appointment second = new Appointment("A204", futureDate(), "Advising meeting");

        service.addAppointment(first);
        service.addAppointment(second);
        service.deleteAppointment("A203");

        assertEquals(1, service.getAppointmentCount());
        assertNull(service.getAppointment("A203"));
        assertEquals(second, service.getAppointment("A204"));
    }
}
