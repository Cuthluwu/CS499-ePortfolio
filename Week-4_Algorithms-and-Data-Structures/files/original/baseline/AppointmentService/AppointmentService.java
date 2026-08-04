import java.util.HashMap;
import java.util.Map;

/**
 * Stores appointments in memory and enforces unique appointment IDs.
 */
public class AppointmentService {
    private final Map<String, Appointment> appointments = new HashMap<>();

    public void addAppointment(Appointment appointment) {
        if (appointment == null) {
            throw new IllegalArgumentException("Appointment cannot be null.");
        }

        if (appointments.containsKey(appointment.getAppointmentId())) {
            throw new IllegalArgumentException("An appointment with this ID already exists.");
        }

        appointments.put(appointment.getAppointmentId(), appointment);
    }

    public void deleteAppointment(String appointmentId) {
        Appointment appointment = findAppointment(appointmentId);
        appointments.remove(appointment.getAppointmentId());
    }

    public Appointment getAppointment(String appointmentId) {
        return appointments.get(appointmentId);
    }

    public int getAppointmentCount() {
        return appointments.size();
    }

    private Appointment findAppointment(String appointmentId) {
        Appointment appointment = appointments.get(appointmentId);

        if (appointment == null) {
            throw new IllegalArgumentException("No appointment was found with that ID.");
        }

        return appointment;
    }
}
