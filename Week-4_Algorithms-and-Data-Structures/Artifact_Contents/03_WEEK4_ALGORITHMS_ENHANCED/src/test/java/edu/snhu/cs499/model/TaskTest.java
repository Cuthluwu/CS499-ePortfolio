package edu.snhu.cs499.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

class TaskTest {
    @Test
    void taskPreservesOriginalLimitsAndNormalizesText() {
        Task task = new Task(" T100 ", " Read Rubric ", " Review every requirement. ");

        assertEquals("T100", task.getTaskId());
        assertEquals("Read Rubric", task.getName());
        assertEquals("Review every requirement.", task.getDescription());
    }

    @Test
    void blankTextIsRejectedDuringCreationAndUpdate() {
        assertThrows(IllegalArgumentException.class, () -> new Task("T101", " ", "Valid description."));
        Task task = new Task("T102", "Valid Name", "Valid description.");
        assertThrows(IllegalArgumentException.class, () -> task.setDescription("  "));
    }

    @Test
    void excessiveTextIsRejected() {
        assertThrows(IllegalArgumentException.class,
                () -> new Task("T103", "This task name exceeds twenty", "Valid description."));
        assertThrows(IllegalArgumentException.class,
                () -> new Task("T103", "Valid Name",
                        "This description is longer than the fifty character project limit."));
    }

    @Test
    void copyIsIndependentFromSource() {
        Task source = new Task("T104", "Original", "Original description.");
        Task copy = new Task(source);

        copy.setName("Updated");

        assertEquals("Original", source.getName());
        assertEquals("Updated", copy.getName());
    }
}
