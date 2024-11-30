package com.lefpap.taskscli.mapper;

import com.lefpap.taskscli.model.Task;
import com.lefpap.taskscli.model.dto.CliTask;
import com.lefpap.taskscli.model.dto.CliTaskCreate;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class TaskMapper {

    public CliTask toCliTask(Task task) {
        if (task == null) {
            return null;
        }

        return new CliTask(
            task.id(),
            task.title()
        );
    }

    public List<CliTask> toCliTaskList(List<Task> tasks) {
        if (tasks == null || tasks.isEmpty()) {
            return List.of();
        }

        return tasks.stream()
            .map(this::toCliTask)
            .toList();
    }

    public Task toTask(CliTaskCreate cliTaskCreate) {
        if (cliTaskCreate == null) {
            return null;
        }

        return new Task(
            null,
            cliTaskCreate.title(),
            null
        );
    }
}
