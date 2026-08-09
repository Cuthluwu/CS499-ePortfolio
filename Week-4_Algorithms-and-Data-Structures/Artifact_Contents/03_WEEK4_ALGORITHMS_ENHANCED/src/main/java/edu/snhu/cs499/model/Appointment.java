package edu.snhu.cs499.model;

import edu.snhu.cs499.validation.Validation;
import java.time.Clock;
import java.util.Date;
import java.util.Objects;

/** Represents one scheduled appointment and protects its mutable Date value. */
public final class Appointment {
    public static final int MAX_DESCRIPTION_LENGTH = 50;

    private final String appointmentId;
    private Date appointmentDate;
    private String description;
    private final Clock clock;

    public Appointment(String appointmentId, Date appointmentDate, String description) {
        this(appointmentId, appointmentDate, description, Clock.systemUTC());
    }

    public Appointment(String appointmentId, Date appointmentDate, String description, Clock clock) {
        this.clock = Objects.requireNonNull(clock, "Clock is required.");
        this.appointmentId = Validation.identifier(appointmentId, "Appointment ID");
        this.appointmentDate = Validation.presentOrFutureDate(appointmentDate, clock);
        this.description = Validation.requiredText(
                description, "Appointment description", MAX_DESCRIPTION_LENGTH);
    }

    public Appointment(Appointment source) {
        this(source.appointmentId, source.appointmentDate, source.description, source.clock);
    }

    public String getAppointmentId() {
        return appointmentId;
    }

    public Date getAppointmentDate() {
        return new Date(appointmentDate.getTime());
    }

    /**
     * Returns the appointment timestamp without allocating a mutable Date copy.
     * The primitive value cannot expose or modify the stored Date instance.
     */
    public long getAppointmentTimeMillis() {
        return appointmentDate.getTime();
    }

    public String getDescription() {
        return description;
    }

    public void setAppointmentDate(Date appointmentDate) {
        this.appointmentDate = Validation.presentOrFutureDate(appointmentDate, clock);
    }

    public void setDescription(String description) {
        this.description = Validation.requiredText(
                description, "Appointment description", MAX_DESCRIPTION_LENGTH);
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof Appointment appointment)) {
            return false;
        }
        return appointmentId.equals(appointment.appointmentId)
                && appointmentDate.equals(appointment.appointmentDate)
                && description.equals(appointment.description);
    }

    @Override
    public int hashCode() {
        return Objects.hash(appointmentId, appointmentDate, description);
    }
}
