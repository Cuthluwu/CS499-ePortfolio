package edu.snhu.cs499.model;

import edu.snhu.cs499.validation.Validation;
import java.util.Objects;

/** Represents one task while preserving the original CS 320 field limits. */
public final class Task {
    public static final int MAX_NAME_LENGTH = 20;
    public static final int MAX_DESCRIPTION_LENGTH = 50;

    private final String taskId;
    private String name;
    private String description;

    public Task(String taskId, String name, String description) {
        this.taskId = Validation.identifier(taskId, "Task ID");
        this.name = Validation.requiredText(name, "Task name", MAX_NAME_LENGTH);
        this.description = Validation.requiredText(description, "Task description", MAX_DESCRIPTION_LENGTH);
    }

    public Task(Task source) {
        this(source.taskId, source.name, source.description);
    }

    public String getTaskId() {
        return taskId;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public void setName(String name) {
        this.name = Validation.requiredText(name, "Task name", MAX_NAME_LENGTH);
    }

    public void setDescription(String description) {
        this.description = Validation.requiredText(description, "Task description", MAX_DESCRIPTION_LENGTH);
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof Task task)) {
            return false;
        }
        return taskId.equals(task.taskId) && name.equals(task.name) && description.equals(task.description);
    }

    @Override
    public int hashCode() {
        return Objects.hash(taskId, name, description);
    }
}
