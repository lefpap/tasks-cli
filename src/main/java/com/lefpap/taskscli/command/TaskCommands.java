package com.lefpap.taskscli.command;

import com.lefpap.taskscli.model.Task;
import com.lefpap.taskscli.model.dto.CliTask;
import com.lefpap.taskscli.model.dto.CliTaskCreate;
import com.lefpap.taskscli.service.TaskService;
import com.lefpap.taskscli.store.TaskStore;
import org.springframework.shell.command.annotation.Command;
import org.springframework.shell.command.annotation.Option;

import java.util.stream.Collectors;

@Command(group = "Task Commands")
public class TaskCommands {

    private final TaskService taskService;

    public TaskCommands(TaskService taskService) {
        this.taskService = taskService;
    }

    @Command(command = "list", description = "Lists all tasks")
    public String listTasks() {
        return taskService.getTasks().stream()
            .map(CliTask::title)
            .collect(Collectors.joining("\n"));
    }

    @Command(command = "create", description = "Creates a new task")
    public String createTask(
        @Option(required = true, longNames = "title", shortNames = 't', description = "The task title" )
        String title
    ) {
        CliTaskCreate cliTaskCreate = new CliTaskCreate(title);
        return taskService.createTask(cliTaskCreate).toString();
    }
}
