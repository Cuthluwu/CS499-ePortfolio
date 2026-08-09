import java.util.HashMap;
import java.util.Map;

/**
 * Stores tasks in memory and enforces unique task IDs.
 */
public class TaskService {
    private final Map<String, Task> tasks = new HashMap<>();

    public void addTask(Task task) {
        if (task == null) {
            throw new IllegalArgumentException("Task cannot be null.");
        }

        if (tasks.containsKey(task.getTaskId())) {
            throw new IllegalArgumentException("A task with this ID already exists.");
        }

        tasks.put(task.getTaskId(), task);
    }

    public void deleteTask(String taskId) {
        Task task = findTask(taskId);
        tasks.remove(task.getTaskId());
    }

    public void updateName(String taskId, String name) {
        findTask(taskId).setName(name);
    }

    public void updateDescription(String taskId, String description) {
        findTask(taskId).setDescription(description);
    }

    public Task getTask(String taskId) {
        return tasks.get(taskId);
    }

    public int getTaskCount() {
        return tasks.size();
    }

    private Task findTask(String taskId) {
        Task task = tasks.get(taskId);

        if (task == null) {
            throw new IllegalArgumentException("No task was found with that ID.");
        }

        return task;
    }
}
