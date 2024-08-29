package casper;

import exception.CasperBotOutOfBoundsException;

import java.util.ArrayList;
import java.util.List;

public class TaskList {
    private final List<Task> taskList;

    public TaskList() {
        this.taskList = new ArrayList<>();
    }

    /**
     * Checks if the task list is empty
     * @return True if task list is empty, false otherwise
     */
    public boolean isEmpty() {
        return this.taskList.isEmpty();
    }

    /**
     * Returns the number of task in the task list
     */
    public int getNumberOfTasks() {
        return this.taskList.size();
    }

    /**
     * Retrieves a task by its index in the task list
     * @param index The index of the task in the task list (0-based indexing)
     * @return The task at index
     * @throws CasperBotOutOfBoundsException If index is negative or >= length of task list
     */
    public Task getTask(int index) throws CasperBotOutOfBoundsException {
        if (index >= 0 && index < this.taskList.size()) {
            return this.taskList.get(index);
        } else {
            throw new CasperBotOutOfBoundsException();
        }
    }

    /**
     * Deletes task specified from the task list
     */
    public void deleteTask(Task task) {
        this.taskList.remove(task);
    }

    /**
     * Adds task to task list
     */
    public void addTask(Task task) {
        this.taskList.add(task);
    }

    /**
     * Returns a TaskList containing all tasks matching the keyword
     */
    public TaskList findMatchTasks(String keyword) {
        TaskList matched = new TaskList();
        for (Task task: this.taskList) {
            if (task.toString().contains(keyword)) {
                matched.addTask(task);
            }
        }
        return matched;
    }
}
