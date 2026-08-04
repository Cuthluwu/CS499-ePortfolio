package edu.snhu.cs499.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import edu.snhu.cs499.exception.DuplicateRecordException;
import edu.snhu.cs499.exception.RecordNotFoundException;
import edu.snhu.cs499.model.Task;
import org.junit.jupiter.api.Test;

class TaskServiceTest {
    @Test
    void serviceOwnsStoredStateAndReturnsSnapshots() {
        TaskService service = new TaskService();
        Task task = new Task("T100", "Read Rubric", "Review every requirement.");
        service.add(task);
        task.setName("Changed Outside");

        Task result = service.findById("T100").orElseThrow();
        assertEquals("Read Rubric", result.getName());
        result.setName("Changed Copy");
        assertEquals("Read Rubric", service.findById("T100").orElseThrow().getName());
    }

    @Test
    void serviceAppliesConsistentDuplicateAndMissingPolicies() {
        TaskService service = new TaskService();
        service.add(new Task("T101", "Read Rubric", "Review every requirement."));

        assertThrows(DuplicateRecordException.class,
                () -> service.add(new Task("T101", "Write Tests", "Add boundary coverage.")));
        assertThrows(RecordNotFoundException.class,
                () -> service.update("T999", "Missing", "Missing task."));
        assertThrows(RecordNotFoundException.class, () -> service.deleteById("T999"));
    }

    @Test
    void updateChangesOnlyStoredRecordAfterValidation() {
        TaskService service = new TaskService();
        service.add(new Task("T102", "Old Name", "Old description."));

        service.update("T102", "New Name", "New description.");

        Task updated = service.findById("T102").orElseThrow();
        assertEquals("New Name", updated.getName());
        assertEquals("New description.", updated.getDescription());
    }

    @Test
    void multiFieldUpdateIsAtomicWhenValidationFails() {
        TaskService service = new TaskService();
        service.add(new Task("T106", "Old Name", "Old description."));

        assertThrows(IllegalArgumentException.class, () -> service.update(
                "T106", "New Name", "This invalid description is longer than the fifty character field limit."));

        Task unchanged = service.findById("T106").orElseThrow();
        assertEquals("Old Name", unchanged.getName());
        assertEquals("Old description.", unchanged.getDescription());
    }
}
