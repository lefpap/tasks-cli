package com.lefpap.taskscli.store;

import com.lefpap.taskscli.model.Task;
import com.lefpap.taskscli.model.TaskStatus;
import jakarta.annotation.Nonnull;
import org.springframework.stereotype.Repository;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Repository
public class InMemoryTaskStore implements TaskStore {

    private final Map<Long, Task> tasks = new ConcurrentHashMap<>();
    private final AtomicLong idGenerator = new AtomicLong(0);

    public InMemoryTaskStore() {
        tasks.put(idGenerator.incrementAndGet(), new Task(idGenerator.get(), "Buy milk", TaskStatus.PENDING));
        tasks.put(idGenerator.incrementAndGet(), new Task(idGenerator.get(), "Do homework", TaskStatus.PENDING));
        tasks.put(idGenerator.incrementAndGet(), new Task(idGenerator.get(), "Go to gym", TaskStatus.PENDING));
        tasks.put(idGenerator.incrementAndGet(), new Task(idGenerator.get(), "Read my book", TaskStatus.FAILED));
        tasks.put(idGenerator.incrementAndGet(), new Task(idGenerator.get(), "Finish the game", TaskStatus.ARCHIVED));
        tasks.put(idGenerator.incrementAndGet(), new Task(idGenerator.get(), "Go on a date", TaskStatus.PENDING));
        tasks.put(idGenerator.incrementAndGet(), new Task(idGenerator.get(), "Prepare my meal", TaskStatus.PENDING));
        tasks.put(idGenerator.incrementAndGet(), new Task(idGenerator.get(), "Clean the house", TaskStatus.COMPLETED));
        tasks.put(idGenerator.incrementAndGet(), new Task(idGenerator.get(), "Code the project", TaskStatus.PENDING));
    }

    @Override
    public List<Task> findAll() {
        return List.copyOf(tasks.values());
    }

    @Override
    public List<Task> findByStatus(TaskStatus status) {
        return List.copyOf(tasks.values().stream()
            .filter(task -> status.equals(task.status()))
            .toList()
        );
    }

    @Override
    public List<Task> findByStatusAnyOf(TaskStatus... statuses) {
        return List.copyOf(tasks.values().stream()
            .filter(task -> Arrays.stream(statuses).anyMatch(status -> status.equals(task.status())))
            .toList()
        );
    }

    @Override
    public Optional<Task> findOne(@Nonnull Long id) {
        return Optional.ofNullable(tasks.get(id));
    }

    @Override
    public Task save(@Nonnull Task task) {
        return Optional.ofNullable(task.id())
            .map(_ -> updateTask(task))
            .orElseGet(() -> createTask(task));
    }

    private Task createTask(Task task) {
        // Create a new task if no ID is provided
        Long newId = idGenerator.incrementAndGet();
        Task newTask = new Task(newId, task.title(), TaskStatus.PENDING);
        tasks.put(newId, newTask);
        return newTask;
    }

    private Task updateTask(Task task) {
        // Update an existing task or add it if it doesn't exist
        Task updatedTask = new Task(task.id(), task.title(), task.status());
        tasks.put(task.id(), updatedTask);
        return updatedTask;
    }

    @Override
    public void delete(@Nonnull Long id) {
        tasks.remove(id);
    }

    @Override
    public void clear() {
        tasks.clear();
    }
}
