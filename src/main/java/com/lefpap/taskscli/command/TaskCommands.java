package com.lefpap.taskscli.command;

import com.lefpap.taskscli.model.dto.CliTask;
import com.lefpap.taskscli.model.dto.CliTaskCreate;
import com.lefpap.taskscli.model.dto.CliTaskUpdate;
import com.lefpap.taskscli.service.TaskService;
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
            .map(this::rendered)
            .collect(Collectors.joining("\n"));
    }

    private String rendered(CliTask task) {
        // TODO: refactor the rendering of tasks
        String line = "-".repeat(20);
        return """
            %s
            ID: %s
            Title: %s
            Status: %s
            %s
            """
            .formatted(line, task.id(), task.title(), task.status(), line);
    }

    @Command(command = "create", description = "Creates a new task")
    public String createTask(
        @Option(required = true, longNames = "title", shortNames = 't', description = "The task title")
        String title
    ) {
        CliTask cliTask = taskService.createTask(new CliTaskCreate(title));
        return "Created task [id: %s]".formatted(cliTask.id());
    }

    @Command(command = "update", description = "Updates an existing task")
    public String updateTask(
        @Option(required = true, description = "The task id")
        Long id,
        @Option(required = true, description = "The task title", longNames = "title", shortNames = 't')
        String title
    ) {

        CliTask cliTask = taskService.updateTask(new CliTaskUpdate(id, title));
        return "Updated task [id: %s]".formatted(cliTask.id());
    }

    @Command(command = "delete", description = "Deletes a task")
    public String deleteTask(
        @Option(required = true, description = "The task id")
        Long id
    ) {
        taskService.deleteTask(id);
        return "Deleted task [id: %s]".formatted(id);
    }

    @Command(command = "clear", description = "Deletes all tasks")
    public String clearTasks() {
        taskService.clearTasks();
        return "Deleted all tasks";
    }
}
