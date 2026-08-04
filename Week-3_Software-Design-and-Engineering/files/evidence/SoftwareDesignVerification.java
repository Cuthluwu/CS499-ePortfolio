package edu.snhu.cs499.verification;

import edu.snhu.cs499.exception.DuplicateRecordException;
import edu.snhu.cs499.exception.RecordNotFoundException;
import edu.snhu.cs499.model.Appointment;
import edu.snhu.cs499.model.Contact;
import edu.snhu.cs499.model.Task;
import edu.snhu.cs499.service.AppointmentService;
import edu.snhu.cs499.service.ContactService;
import edu.snhu.cs499.service.TaskService;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.Date;

/** Standalone checks that require only a Java 17 JDK. */
public final class SoftwareDesignVerification {
    private static int checks;

    private SoftwareDesignVerification() {
    }

    public static void main(String[] args) {
        verifyContacts();
        verifyTasks();
        verifyAppointments();
        System.out.println("PASS: " + checks + " software-design verification checks completed.");
    }

    private static void verifyContacts() {
        Contact normalized = new Contact(" C100 ", " Madison ", " Parker ", "2075551000", " 13 Moonlit Way ");
        record("C100".equals(normalized.getContactId()), "contact ID is normalized");
        record("Madison".equals(normalized.getFirstName()), "contact name is normalized");
        expect(IllegalArgumentException.class,
                () -> new Contact("C101", " ", "Parker", "2075551000", "13 Moonlit Way"),
                "blank contact field is rejected");

        ContactService service = new ContactService();
        service.add(normalized);
        normalized.setAddress("22 Pine St");
        record("13 Moonlit Way".equals(service.findById("C100").orElseThrow().getAddress()),
                "service stores a contact copy");
        Contact result = service.findById("C100").orElseThrow();
        result.setFirstName("June");
        record("Madison".equals(service.findById("C100").orElseThrow().getFirstName()),
                "lookup returns a contact snapshot");
        expect(DuplicateRecordException.class, () -> service.add(new Contact(
                "C100", "June", "Harlow", "2075552000", "22 Pine St")),
                "duplicate contact ID is rejected with a specific exception");
        record(service.findById("C999").isEmpty(), "missing contact uses Optional.empty");
        service.updateName("C100", "June", "Harlow");
        record("Harlow".equals(service.findById("C100").orElseThrow().getLastName()),
                "contact update uses the service boundary");
        expect(IllegalArgumentException.class, () -> service.updateName("C100", "Ada", "TooManyLetters"),
                "invalid multi-field contact update is rejected");
        record("June".equals(service.findById("C100").orElseThrow().getFirstName()),
                "failed contact update leaves stored state unchanged");
        service.deleteById("C100");
        record(service.size() == 0, "contact delete updates service state");
        expect(RecordNotFoundException.class, () -> service.deleteById("C100"),
                "missing contact delete uses a specific exception");
    }

    private static void verifyTasks() {
        Task task = new Task("T100", "Read Rubric", "Review every requirement.");
        TaskService service = new TaskService();
        service.add(task);
        task.setName("Changed Outside");
        record("Read Rubric".equals(service.findById("T100").orElseThrow().getName()),
                "service stores a task copy");
        service.update("T100", "Write Tests", "Add boundary coverage.");
        record("Write Tests".equals(service.findById("T100").orElseThrow().getName()),
                "task update persists validated values");
        expect(IllegalArgumentException.class, () -> service.update(
                "T100", "Changed", "This invalid description is longer than the fifty character field limit."),
                "invalid multi-field task update is rejected");
        record("Write Tests".equals(service.findById("T100").orElseThrow().getName()),
                "failed task update leaves stored state unchanged");
        expect(DuplicateRecordException.class,
                () -> service.add(new Task("T100", "Duplicate", "Duplicate task.")),
                "duplicate task ID is rejected");
        expect(IllegalArgumentException.class,
                () -> new Task("T101", "This task name is over twenty characters", "Valid description."),
                "task length limit is preserved");
    }

    private static void verifyAppointments() {
        Clock clock = Clock.fixed(Instant.parse("2026-08-03T12:00:00Z"), ZoneOffset.UTC);
        Date future = Date.from(clock.instant().plusSeconds(3600));
        Appointment appointment = new Appointment("A100", future, "Original", clock);
        future.setTime(0);
        record(appointment.getAppointmentDate().after(Date.from(clock.instant())),
                "appointment copies the input date");
        Date returned = appointment.getAppointmentDate();
        returned.setTime(0);
        record(appointment.getAppointmentDate().after(Date.from(clock.instant())),
                "appointment copies the output date");
        expect(IllegalArgumentException.class, () -> new Appointment(
                "A101", Date.from(clock.instant().minusSeconds(1)), "Past", clock),
                "past appointment is rejected against a deterministic clock");

        AppointmentService service = new AppointmentService();
        service.add(appointment);
        appointment.setDescription("Changed Outside");
        record("Original".equals(service.findById("A100").orElseThrow().getDescription()),
                "service stores an appointment copy");
        service.update("A100", Date.from(clock.instant().plusSeconds(7200)), "Updated");
        record("Updated".equals(service.findById("A100").orElseThrow().getDescription()),
                "appointment update persists through the service");
        Date acceptedDate = service.findById("A100").orElseThrow().getAppointmentDate();
        expect(IllegalArgumentException.class, () -> service.update(
                "A100", Date.from(clock.instant().plusSeconds(10800)),
                "This invalid appointment description exceeds the fifty character field limit."),
                "invalid multi-field appointment update is rejected");
        record(acceptedDate.equals(service.findById("A100").orElseThrow().getAppointmentDate()),
                "failed appointment update leaves stored state unchanged");
        expect(IllegalArgumentException.class, () -> service.findById(" "),
                "blank service identifier is rejected");
    }

    private static void record(boolean condition, String description) {
        if (!condition) {
            throw new AssertionError(description);
        }
        checks++;
        System.out.println("PASS " + checks + ": " + description);
    }

    private static void expect(Class<? extends Throwable> type, Runnable action, String description) {
        try {
            action.run();
        } catch (Throwable error) {
            record(type.isInstance(error), description);
            return;
        }
        throw new AssertionError(description);
    }
}
