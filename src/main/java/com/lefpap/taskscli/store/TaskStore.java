package com.lefpap.taskscli.store;

import com.lefpap.taskscli.model.Task;
import com.lefpap.taskscli.model.TaskStatus;
import jakarta.annotation.Nonnull;

import java.util.List;
import java.util.Optional;

public interface TaskStore {
    List<Task> findAll();
    long countAll();
    List<Task> findByStatus(@Nonnull TaskStatus status);
    long countByStatus(@Nonnull TaskStatus status);
    Optional<Task> findOne(@Nonnull Long id);
    Task save(@Nonnull Task task);
    void delete(@Nonnull Long id);
    long clear();
    long clearTasksOfStatus(@Nonnull TaskStatus status);
}
