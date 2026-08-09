import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class TaskServiceTest {
    private TaskService service;

    @BeforeEach
    public void setUp() {
        service = new TaskService();
    }

    @Test
    public void testAddTaskWithUniqueId() {
        Task task = new Task("T100", "Clean Desk", "Make the workspace easier to use.");

        service.addTask(task);

        assertEquals(1, service.getTaskCount());
        assertEquals(task, service.getTask("T100"));
    }

    @Test
    public void testCannotAddDuplicateTaskId() {
        Task first = new Task("T101", "Read Rubric", "Review the assignment requirements.");
        Task duplicate = new Task("T101", "Write Tests", "Create JUnit tests.");

        service.addTask(first);

        assertThrows(IllegalArgumentException.class, () -> service.addTask(duplicate));
        assertEquals(1, service.getTaskCount());
    }

    @Test
    public void testCannotAddNullTask() {
        assertThrows(IllegalArgumentException.class, () -> service.addTask(null));
    }

    @Test
    public void testDeleteTaskById() {
        Task task = new Task("T102", "Feed Pets", "Make sure all pets are fed.");

        service.addTask(task);
        service.deleteTask("T102");

        assertEquals(0, service.getTaskCount());
        assertNull(service.getTask("T102"));
    }

    @Test
    public void testCannotDeleteMissingTask() {
        assertThrows(IllegalArgumentException.class, () -> service.deleteTask("MISSING"));
    }

    @Test
    public void testUpdateTaskNameById() {
        service.addTask(new Task("T103", "Old Task", "Valid description."));

        service.updateName("T103", "Updated Task");

        assertEquals("Updated Task", service.getTask("T103").getName());
    }

    @Test
    public void testUpdateTaskDescriptionById() {
        service.addTask(new Task("T104", "Valid Name", "Old description."));

        service.updateDescription("T104", "Updated description.");

        assertEquals("Updated description.", service.getTask("T104").getDescription());
    }

    @Test
    public void testCannotUpdateMissingTask() {
        assertThrows(IllegalArgumentException.class, () -> service.updateName("MISSING", "New Name"));
        assertThrows(IllegalArgumentException.class, () -> service.updateDescription("MISSING", "New description."));
    }

    @Test
    public void testUpdatedFieldsStillMeetTaskRules() {
        service.addTask(new Task("T105", "Valid Name", "Valid description."));

        assertThrows(IllegalArgumentException.class, () -> service.updateName("T105", null));
        assertThrows(IllegalArgumentException.class, () -> service.updateName("T105", "This updated name is too long"));
        assertThrows(IllegalArgumentException.class, () -> service.updateDescription("T105", null));
        assertThrows(IllegalArgumentException.class, () ->
            service.updateDescription("T105", "This updated description is way too long for the project requirement."));
    }
}
