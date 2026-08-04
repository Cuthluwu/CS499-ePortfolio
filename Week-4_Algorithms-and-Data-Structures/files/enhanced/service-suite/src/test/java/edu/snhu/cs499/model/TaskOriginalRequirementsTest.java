package edu.snhu.cs499.model;

import static org.junit.jupiter.api.Assertions.*;

import java.lang.reflect.Method;

import org.junit.jupiter.api.Test;

class TaskOriginalRequirementsTest {

    @Test
    public void testTaskCreatedWithValidFields() {
        Task task = new Task("TASK01", "Finish CS320", "Complete the Project One unit tests.");

        assertEquals("TASK01", task.getTaskId());
        assertEquals("Finish CS320", task.getName());
        assertEquals("Complete the Project One unit tests.", task.getDescription());
    }

    @Test
    public void testTaskIdAcceptsTenCharacters() {
        Task task = new Task("1234567890", "Valid Name", "Valid description.");

        assertEquals("1234567890", task.getTaskId());
    }

    @Test
    public void testTaskIdCannotBeNull() {
        assertThrows(IllegalArgumentException.class, () ->
            new Task(null, "Valid Name", "Valid description."));
    }

    @Test
    public void testTaskIdCannotBeLongerThanTenCharacters() {
        assertThrows(IllegalArgumentException.class, () ->
            new Task("TASK1234567", "Valid Name", "Valid description."));
    }

    @Test
    public void testTaskIdIsNotUpdatable() {
        for (Method method : Task.class.getDeclaredMethods()) {
            assertNotEquals("setTaskId", method.getName());
        }
    }

    @Test
    public void testNameAcceptsTwentyCharacters() {
        Task task = new Task("T001", "12345678901234567890", "Valid description.");

        assertEquals("12345678901234567890", task.getName());
    }

    @Test
    public void testNameCannotBeNull() {
        assertThrows(IllegalArgumentException.class, () ->
            new Task("T002", null, "Valid description."));
    }

    @Test
    public void testNameCannotBeLongerThanTwentyCharacters() {
        assertThrows(IllegalArgumentException.class, () ->
            new Task("T003", "This task name is too long", "Valid description."));
    }

    @Test
    public void testDescriptionAcceptsFiftyCharacters() {
        String description = "12345678901234567890123456789012345678901234567890";
        Task task = new Task("T004", "Valid Name", description);

        assertEquals(description, task.getDescription());
    }

    @Test
    public void testDescriptionCannotBeNull() {
        assertThrows(IllegalArgumentException.class, () ->
            new Task("T005", "Valid Name", null));
    }

    @Test
    public void testDescriptionCannotBeLongerThanFiftyCharacters() {
        assertThrows(IllegalArgumentException.class, () ->
            new Task("T006", "Valid Name", "This task description is too long for the required field limit."));
    }

    @Test
    public void testNameAndDescriptionCanBeUpdated() {
        Task task = new Task("T007", "Old Name", "Old description.");

        task.setName("New Name");
        task.setDescription("New description.");

        assertEquals("New Name", task.getName());
        assertEquals("New description.", task.getDescription());
    }

    @Test
    public void testUpdatedFieldsStillUseValidation() {
        Task task = new Task("T008", "Valid Name", "Valid description.");

        assertThrows(IllegalArgumentException.class, () -> task.setName(null));
        assertThrows(IllegalArgumentException.class, () -> task.setName("This updated task name is too long"));
        assertThrows(IllegalArgumentException.class, () -> task.setDescription(null));
        assertThrows(IllegalArgumentException.class, () ->
            task.setDescription("This updated description is too long because it passes the fifty character limit."));
    }
}
