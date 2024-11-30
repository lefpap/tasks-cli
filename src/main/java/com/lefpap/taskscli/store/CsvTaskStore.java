package com.lefpap.taskscli.store;

import com.lefpap.taskscli.lib.CsvTaskLoader;
import com.lefpap.taskscli.model.Task;
import com.lefpap.taskscli.model.TaskStatus;
import jakarta.annotation.Nonnull;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
@Profile("csv")
public class CsvTaskStore implements TaskStore {

    private final CsvTaskLoader csvTaskLoader;
    private Map<Long, Task> taskCache;

    public CsvTaskStore(CsvTaskLoader csvTaskLoader) {
        this.csvTaskLoader = csvTaskLoader;
    }

    private Map<Long, Task> tasksCache() {
        if (Objects.isNull(taskCache)) {
            this.taskCache = csvTaskLoader.loadTasks();
        }

        return taskCache;
    }

    @Override
    public List<Task> findAll() {
        return List.copyOf(tasksCache().values());
    }

    @Override
    public List<Task> findByStatus(@Nonnull TaskStatus status) {
        return List.copyOf(taskCache.values().stream()
            .filter(task -> status.equals(task.status()))
            .toList());
    }

    @Override
    public List<Task> findByStatusAnyOf(@Nonnull TaskStatus... statuses) {
        return List.copyOf(taskCache.values().stream()
            .filter(task -> Arrays.stream(statuses)
                .anyMatch(status -> status.equals(task.status())))
            .toList());
    }

    @Override
    public Optional<Task> findOne(@Nonnull Long id) {
        return Optional.ofNullable(tasksCache().get(id));
    }

    @Override
    public Task save(@Nonnull Task task) {
        return Optional.ofNullable(task.id())
            .map(_ -> updateTask(task))
            .orElseGet(() -> createTask(task));
    }

    private Task createTask(Task task) {
        // Create a new task if no ID is provided
        Long newId = generateId();
        Task newTask = new Task(newId, task.title(), TaskStatus.PENDING);
        tasksCache().put(newId, newTask);
        csvTaskLoader.saveTasks(tasksCache());
        return newTask;
    }

    private Task updateTask(Task task) {
        // Update an existing task or add it if it doesn't exist
        Task updatedTask = new Task(task.id(), task.title(), task.status());
        tasksCache().put(task.id(), updatedTask);
        csvTaskLoader.saveTasks(tasksCache());
        return updatedTask;
    }

    private long generateId() {
        return tasksCache().values().stream()
            .mapToLong(Task::id)
            .max()
            .orElse(0L) + 1;
    }

    @Override
    public void delete(@Nonnull Long id) {
        tasksCache().remove(id);
        csvTaskLoader.saveTasks(tasksCache());
    }

    @Override
    public void clear() {
        tasksCache().clear();
        csvTaskLoader.saveTasks(tasksCache());
    }
}
