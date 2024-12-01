package com.lefpap.taskscli.command;

import com.lefpap.taskscli.lib.TaskDetailsRenderer;
import com.lefpap.taskscli.lib.TaskTableRenderer;
import com.lefpap.taskscli.model.TaskStatus;
import com.lefpap.taskscli.model.dto.CliTask;
import com.lefpap.taskscli.model.dto.CliTaskCreate;
import com.lefpap.taskscli.model.dto.CliTaskUpdate;
import com.lefpap.taskscli.service.TaskService;
import org.springframework.shell.command.CommandContext;
import org.springframework.shell.command.annotation.Command;
import org.springframework.shell.command.annotation.Option;
import org.springframework.shell.component.ConfirmationInput;

import java.util.Objects;
import java.util.Scanner;


@Command(group = "Task Commands")
public class TaskCommands {

    private static final Scanner scanner = new Scanner(System.in);
    private final TaskService taskService;

    public TaskCommands(TaskService taskService) {
        this.taskService = taskService;
    }

    @Command(command = "list", description = "Lists all tasks")
    public String listTasks(
        CommandContext ctx,
        @Option(description = "Flag to display all tasks", longNames = "all", shortNames = 'a')
        Boolean all,
        @Option(description = "Filter tasks by status (PENDING, COMPLETED)", longNames = "status", shortNames = 's')
        TaskStatus status
    ) {

        TaskTableRenderer.Builder tableBuilder = TaskTableRenderer.builder()
            .withTerminalWidth(ctx.getTerminal().getWidth())
            .withTotalTaskCount(taskService.getTotalTasks())
            .withCompletedTaskCount(taskService.getTotalTasksOfStatus(TaskStatus.COMPLETED))
            .withPendingTaskCount(taskService.getTotalTasksOfStatus(TaskStatus.PENDING));

        // Show all tasks
        if (Boolean.TRUE.equals(all)) {
            return tableBuilder
                .withTitle("TASKS [ALL]")
                .withTasks(taskService.getTasks())
                .build()
                .render();
        }

        // Show tasks of status
        if (Objects.nonNull(status)) {
            return tableBuilder
                .withTitle("TASKS [%s]".formatted(status))
                .withTasks(taskService.getTasksOfStatus(status))
                .build()
                .render();
        }

        // Show only pending
        return tableBuilder
            .withTitle("TASKS [%s]".formatted(TaskStatus.PENDING))
            .withTasks(taskService.getTasksOfStatus(TaskStatus.PENDING))
            .build()
            .render();
    }

    @Command(command = "create", description = "Creates a new task")
    public String createTask(
        CommandContext ctx,
        @Option(required = true, longNames = "title", shortNames = 't', description = "The task title")
        String title
    ) {
        CliTask cliTask = taskService.createTask(new CliTaskCreate(title));
        return TaskDetailsRenderer.builder()
            .withTitle("✅ Task created successfully [ID: %s]".formatted(cliTask.id()))
            .withTask(cliTask)
            .withTerminalWidth(ctx.getTerminal().getWidth())
            .build()
            .render();
    }

    @Command(command = "update", description = "Updates an existing task")
    public String updateTask(
        CommandContext ctx,
        @Option(required = true, description = "The task id")
        Long id,
        @Option(required = true, description = "The task title", longNames = "title", shortNames = 't')
        String title
    ) {

        CliTask cliTask = taskService.updateTask(new CliTaskUpdate(id, title));
        return TaskDetailsRenderer.builder()
            .withTitle("✅ SUCCESS: Task updated [ID: %s]".formatted(id))
            .withTask(cliTask)
            .withTerminalWidth(ctx.getTerminal().getWidth())
            .build()
            .render();
    }

    @Command(command = "delete", description = "Deletes a task")
    public String deleteTask(
        @Option(required = true, description = "The task id")
        Long id
    ) {
        taskService.deleteTask(id);
        return "✅ SUCCESS: Task deleted [ID: %s]]".formatted(id);
    }

    @Command(command = "clear", description = "Deletes all tasks")
    public String clearTasks(
        @Option(description = "Delete all tasks", longNames = "all", shortNames = 'a')
        boolean all
    ) {
        if (all) {
            // Prompt for confirmation
            System.out.print("Are you sure you want to delete all tasks? (y/n): ");
            String response = scanner.nextLine().trim().toLowerCase();
            if (response.equalsIgnoreCase("n") || response.equalsIgnoreCase("no")) {
                return "💭 No tasks deleted";
            }
            taskService.clearTasks();
            return "✅ SUCCESS: All tasks deleted";
        }

        taskService.clearTasksOfStatus(TaskStatus.COMPLETED);
        return "✅ SUCCESS: Completed tasks deleted";
    }
}
