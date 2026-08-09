package edu.snhu.cs499.service;

import edu.snhu.cs499.exception.DuplicateRecordException;
import edu.snhu.cs499.exception.RecordNotFoundException;
import edu.snhu.cs499.model.Task;
import edu.snhu.cs499.repository.RecordRepository;
import edu.snhu.cs499.validation.Validation;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/** Manages tasks without exposing the mutable records stored internally. */
public final class TaskService implements RecordRepository<Task> {
    private final Map<String, Task> tasks = new HashMap<>();

    @Override
    public void add(Task task) {
        if (task == null) {
            throw new IllegalArgumentException("Task cannot be null.");
        }
        String identifier = task.getTaskId();
        if (tasks.containsKey(identifier)) {
            throw new DuplicateRecordException("Task", identifier);
        }
        tasks.put(identifier, new Task(task));
    }

    @Override
    public Optional<Task> findById(String identifier) {
        String normalized = Validation.identifier(identifier, "Task ID");
        return Optional.ofNullable(tasks.get(normalized)).map(Task::new);
    }

    @Override
    public void deleteById(String identifier) {
        String normalized = Validation.identifier(identifier, "Task ID");
        if (tasks.remove(normalized) == null) {
            throw new RecordNotFoundException("Task", normalized);
        }
    }

    public void update(String identifier, String name, String description) {
        Task updated = new Task(requireStored(identifier));
        updated.setName(name);
        updated.setDescription(description);
        tasks.put(updated.getTaskId(), updated);
    }

    public void updateName(String identifier, String name) {
        Task current = requireStored(identifier);
        update(current.getTaskId(), name, current.getDescription());
    }

    public void updateDescription(String identifier, String description) {
        Task current = requireStored(identifier);
        update(current.getTaskId(), current.getName(), description);
    }

    @Override
    public int size() {
        return tasks.size();
    }

    private Task requireStored(String identifier) {
        String normalized = Validation.identifier(identifier, "Task ID");
        Task task = tasks.get(normalized);
        if (task == null) {
            throw new RecordNotFoundException("Task", normalized);
        }
        return task;
    }
}
