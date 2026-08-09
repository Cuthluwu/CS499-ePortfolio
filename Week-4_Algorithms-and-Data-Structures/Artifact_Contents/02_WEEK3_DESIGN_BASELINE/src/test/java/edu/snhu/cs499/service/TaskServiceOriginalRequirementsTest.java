package edu.snhu.cs499.service;

import edu.snhu.cs499.model.Task;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class TaskServiceOriginalRequirementsTest {
    private TaskService service;

    @BeforeEach
    public void setUp() {
        service = new TaskService();
    }

    @Test
    public void testAddTaskWithUniqueId() {
        Task task = new Task("T100", "Clean Desk", "Make the workspace easier to use.");

        service.add(task);

        assertEquals(1, service.size());
        assertEquals(task, service.findById("T100").orElse(null));
    }

    @Test
    public void testCannotAddDuplicateTaskId() {
        Task first = new Task("T101", "Read Rubric", "Review the assignment requirements.");
        Task duplicate = new Task("T101", "Write Tests", "Create JUnit tests.");

        service.add(first);

        assertThrows(IllegalArgumentException.class, () -> service.add(duplicate));
        assertEquals(1, service.size());
    }

    @Test
    public void testCannotAddNullTask() {
        assertThrows(IllegalArgumentException.class, () -> service.add(null));
    }

    @Test
    public void testDeleteTaskById() {
        Task task = new Task("T102", "Feed Pets", "Make sure all pets are fed.");

        service.add(task);
        service.deleteById("T102");

        assertEquals(0, service.size());
        assertTrue(service.findById("T102").isEmpty());
    }

    @Test
    public void testCannotDeleteMissingTask() {
        assertThrows(IllegalArgumentException.class, () -> service.deleteById("MISSING"));
    }

    @Test
    public void testUpdateTaskNameById() {
        service.add(new Task("T103", "Old Task", "Valid description."));

        service.updateName("T103", "Updated Task");

        assertEquals("Updated Task", service.findById("T103").orElseThrow().getName());
    }

    @Test
    public void testUpdateTaskDescriptionById() {
        service.add(new Task("T104", "Valid Name", "Old description."));

        service.updateDescription("T104", "Updated description.");

        assertEquals("Updated description.", service.findById("T104").orElseThrow().getDescription());
    }

    @Test
    public void testCannotUpdateMissingTask() {
        assertThrows(IllegalArgumentException.class, () -> service.updateName("MISSING", "New Name"));
        assertThrows(IllegalArgumentException.class, () -> service.updateDescription("MISSING", "New description."));
    }

    @Test
    public void testUpdatedFieldsStillMeetTaskRules() {
        service.add(new Task("T105", "Valid Name", "Valid description."));

        assertThrows(IllegalArgumentException.class, () -> service.updateName("T105", null));
        assertThrows(IllegalArgumentException.class, () -> service.updateName("T105", "This updated name is too long"));
        assertThrows(IllegalArgumentException.class, () -> service.updateDescription("T105", null));
        assertThrows(IllegalArgumentException.class, () ->
            service.updateDescription("T105", "This updated description is way too long for the project requirement."));
    }
}
