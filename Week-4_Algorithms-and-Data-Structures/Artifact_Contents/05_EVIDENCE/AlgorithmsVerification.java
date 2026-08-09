package edu.snhu.cs499.verification;

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
import java.util.List;

/** Standalone algorithm checks that require only a Java 17 JDK. */
public final class AlgorithmsVerification {
    private static int checks;

    private AlgorithmsVerification() {
    }

    public static void main(String[] args) {
        verifyContactSearch();
        verifyTaskSearch();
        verifyAppointmentRange();
        System.out.println("PASS: " + checks + " algorithms verification checks completed.");
    }

    private static void verifyContactSearch() {
        ContactService service = new ContactService();
        service.add(contact("C203", "Zoe", "Baker"));
        service.add(contact("C201", "Madison", "Parker"));
        service.add(contact("C202", "Ada", "Baker"));
        service.add(contact("C204", "June", "Harlow"));

        List<Contact> matches = service.searchByLastName("KeR");
        record(matches.size() == 3, "contact search matches substrings without case sensitivity");
        record(ids(matches).equals(List.of("C202", "C203", "C201")),
                "contact matches use last-name, first-name, and ID order");
        record(service.searchByLastName("missing").isEmpty(), "contact search handles no matches");
        expect(IllegalArgumentException.class, () -> service.searchByLastName(" "),
                "contact search rejects a blank query");
        expect(UnsupportedOperationException.class, matches::clear,
                "contact result list is immutable");
        matches.get(0).setFirstName("June");
        record("Ada".equals(service.findById("C202").orElseThrow().getFirstName()),
                "contact search returns snapshots");
    }

    private static void verifyTaskSearch() {
        TaskService service = new TaskService();
        service.add(new Task("T203", "Write Tests", "Cover the search behavior."));
        service.add(new Task("T201", "Archive Files", "Prepare the capstone package."));
        service.add(new Task("T202", "Read Rubric", "Check every capstone requirement."));

        List<Task> capstone = service.searchByKeyword("CAPSTONE");
        record(capstone.size() == 2, "task search checks descriptions without case sensitivity");
        record(taskIds(capstone).equals(List.of("T201", "T202")),
                "task matches are sorted by name and ID");
        record(service.searchByKeyword("write").get(0).getTaskId().equals("T203"),
                "task search also checks names");
        record(service.searchByKeyword("missing").isEmpty(), "task search handles no matches");
        expect(IllegalArgumentException.class, () -> service.searchByKeyword(null),
                "task search rejects null input");
        expect(UnsupportedOperationException.class,
                () -> capstone.add(new Task("T204", "Another", "Another task.")),
                "task result list is immutable");
    }

    private static void verifyAppointmentRange() {
        Clock clock = Clock.fixed(Instant.parse("2026-08-03T12:00:00Z"), ZoneOffset.UTC);
        AppointmentService service = new AppointmentService();
        service.add(appointment("A203", 7200, "Second tie", clock));
        service.add(appointment("A201", 3600, "First", clock));
        service.add(appointment("A202", 7200, "First tie", clock));
        service.add(appointment("A204", 14400, "Outside", clock));

        Date start = Date.from(clock.instant().plusSeconds(3600));
        Date end = Date.from(clock.instant().plusSeconds(7200));
        List<Appointment> matches = service.findByDateRange(start, end);
        record(matches.size() == 3, "date range includes both boundaries");
        record(appointmentIds(matches).equals(List.of("A201", "A202", "A203")),
                "appointments use date and ID order");
        record(matches.stream().noneMatch(item -> item.getAppointmentId().equals("A204")),
                "date range excludes values after the upper bound");
        expect(IllegalArgumentException.class, () -> service.findByDateRange(end, start),
                "date range rejects reversed bounds");
        expect(IllegalArgumentException.class, () -> service.findByDateRange(null, end),
                "date range rejects null bounds");
        expect(UnsupportedOperationException.class, matches::clear,
                "appointment result list is immutable");
        matches.get(0).setDescription("Changed Copy");
        record("First".equals(service.findById("A201").orElseThrow().getDescription()),
                "appointment range returns snapshots");
    }

    private static Contact contact(String id, String firstName, String lastName) {
        return new Contact(id, firstName, lastName, "2075551000", "13 Moonlit Way");
    }

    private static Appointment appointment(String id, long seconds, String description, Clock clock) {
        return new Appointment(id, Date.from(clock.instant().plusSeconds(seconds)), description, clock);
    }

    private static List<String> ids(List<Contact> contacts) {
        return contacts.stream().map(Contact::getContactId).toList();
    }

    private static List<String> taskIds(List<Task> tasks) {
        return tasks.stream().map(Task::getTaskId).toList();
    }

    private static List<String> appointmentIds(List<Appointment> appointments) {
        return appointments.stream().map(Appointment::getAppointmentId).toList();
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
