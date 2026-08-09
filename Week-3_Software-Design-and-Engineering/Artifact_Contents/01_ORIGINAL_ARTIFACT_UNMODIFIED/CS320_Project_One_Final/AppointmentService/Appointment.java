import java.util.Date;

/**
 * Madison Parker
 * CS 320 Project One
 *
 * Appointment represents one scheduled appointment. The appointment ID is
 * final, and Date values are copied to protect the object from outside changes.
 */
public class Appointment {
    private final String appointmentId;
    private Date appointmentDate;
    private String description;

    public Appointment(String appointmentId, Date appointmentDate, String description) {
        validateAppointmentId(appointmentId);
        validateAppointmentDate(appointmentDate);
        validateDescription(description);

        this.appointmentId = appointmentId;
        this.appointmentDate = new Date(appointmentDate.getTime());
        this.description = description;
    }

    public String getAppointmentId() {
        return appointmentId;
    }

    public Date getAppointmentDate() {
        return new Date(appointmentDate.getTime());
    }

    public String getDescription() {
        return description;
    }

    public void setAppointmentDate(Date appointmentDate) {
        validateAppointmentDate(appointmentDate);
        this.appointmentDate = new Date(appointmentDate.getTime());
    }

    public void setDescription(String description) {
        validateDescription(description);
        this.description = description;
    }

    private void validateAppointmentId(String appointmentId) {
        if (appointmentId == null || appointmentId.length() > 10) {
            throw new IllegalArgumentException("Appointment ID is required and cannot be longer than 10 characters.");
        }
    }

    private void validateAppointmentDate(Date appointmentDate) {
        if (appointmentDate == null || appointmentDate.before(new Date())) {
            throw new IllegalArgumentException("Appointment date is required and cannot be in the past.");
        }
    }

    private void validateDescription(String description) {
        if (description == null || description.length() > 50) {
            throw new IllegalArgumentException("Description is required and cannot be longer than 50 characters.");
        }
    }
}
