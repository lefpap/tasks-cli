package com.lefpap.taskscli.mapper;

import com.lefpap.taskscli.model.Task;
import com.lefpap.taskscli.model.dto.CliTask;
import com.lefpap.taskscli.model.dto.CliTaskCreate;
import com.lefpap.taskscli.model.dto.CliTaskUpdate;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;

@Component
public class TaskMapper {

    public CliTask toCliTask(Task task) {
        if (Objects.isNull(task)) {
            return null;
        }

        return new CliTask(
            task.id(),
            task.title(),
            task.status()
        );
    }

    public List<CliTask> toCliTaskList(List<Task> tasks) {
        if (Objects.isNull(tasks) || tasks.isEmpty()) {
            return List.of();
        }

        return tasks.stream()
            .map(this::toCliTask)
            .toList();
    }

    public Task toTask(CliTaskCreate cliTaskCreate) {

        if (Objects.isNull(cliTaskCreate)) {
            return null;
        }

        return new Task(
            null,
            cliTaskCreate.title(),
            null
        );
    }

    public Task mergeToTask(CliTaskUpdate cliTaskUpdate, Task task) {
        if (Objects.isNull(cliTaskUpdate) || Objects.isNull(task)) {
            return null;
        }

        return new Task(
            cliTaskUpdate.id(),
            cliTaskUpdate.title(),
            task.status()
        );
    }
}
