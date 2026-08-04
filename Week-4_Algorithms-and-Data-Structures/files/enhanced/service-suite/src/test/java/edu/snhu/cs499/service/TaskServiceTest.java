package edu.snhu.cs499.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertIterableEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import edu.snhu.cs499.exception.DuplicateRecordException;
import edu.snhu.cs499.exception.RecordNotFoundException;
import edu.snhu.cs499.model.Task;
import java.util.List;
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

    @Test
    void keywordSearchChecksNameAndDescriptionThenSortsByNameAndId() {
        TaskService service = new TaskService();
        service.add(new Task("T203", "Write Tests", "Cover the search behavior."));
        service.add(new Task("T201", "Archive Files", "Prepare the capstone package."));
        service.add(new Task("T202", "Read Rubric", "Check every capstone requirement."));

        List<String> identifiers = service.searchByKeyword("CAPSTONE").stream()
                .map(Task::getTaskId)
                .toList();

        assertIterableEquals(List.of("T201", "T202"), identifiers);
    }

    @Test
    void keywordSearchHandlesEmptyAndImmutableResults() {
        TaskService service = new TaskService();
        service.add(new Task("T204", "Write Tests", "Cover the search behavior."));

        List<Task> matches = service.searchByKeyword("search");
        assertEquals(1, matches.size());
        assertThrows(UnsupportedOperationException.class,
                () -> matches.add(new Task("T205", "Another", "Another valid task.")));
        assertEquals(0, service.searchByKeyword("missing").size());
        assertThrows(IllegalArgumentException.class, () -> service.searchByKeyword(null));
    }
}
