package com.lefpap.taskscli.store;

import com.lefpap.taskscli.model.Task;
import com.lefpap.taskscli.model.TaskStatus;
import jakarta.annotation.Nonnull;

import java.util.List;
import java.util.Optional;

public interface TaskStore {
    List<Task> findAll();
    List<Task> findByStatus(@Nonnull TaskStatus status);
    List<Task> findByStatusAnyOf(@Nonnull TaskStatus... statuses);
    Optional<Task> findOne(@Nonnull Long id);
    Task save(@Nonnull Task task);
    void delete(@Nonnull Long id);
    void clear();
}
