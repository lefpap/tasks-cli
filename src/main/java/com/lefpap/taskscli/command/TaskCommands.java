package com.lefpap.taskscli.command;

import com.lefpap.taskscli.lib.TaskDetailsRenderer;
import com.lefpap.taskscli.lib.TaskTableRenderer;
import com.lefpap.taskscli.model.TaskStatus;
import com.lefpap.taskscli.model.dto.*;
import com.lefpap.taskscli.service.TaskService;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import org.jline.terminal.Terminal;
import org.springframework.shell.command.annotation.Command;
import org.springframework.shell.command.annotation.Option;
import org.springframework.shell.context.InteractionMode;

import java.time.LocalDate;
import java.util.Optional;
import java.util.Scanner;


@Command(group = "Task Commands")
public class TaskCommands  {

    private static final Scanner scanner = new Scanner(System.in);

    private final TaskService taskService;
    private final Terminal terminal;

    public TaskCommands(TaskService taskService, Terminal terminal) {
        this.taskService = taskService;
        this.terminal = terminal;
    }

    @Command(command = "list", description = "Lists all tasks")
    public String listTasks(
        @Option(description = "Flag to display all tasks", shortNames = 'a')
        boolean all,
        @Option(description = "Filter tasks by status (PENDING, COMPLETED) Default: PENDING", shortNames = 's')
        TaskStatus status
    ) {

        CliTaskListOptions cliTaskListOptions = new CliTaskListOptions(all, status);

        TaskTableRenderer.Builder tableBuilder = TaskTableRenderer.builder()
            .withTerminal(terminal)
            .withTotalTaskCount(taskService.getTotalTasks())
            .withCompletedTaskCount(taskService.getTotalTasksOfStatus(TaskStatus.COMPLETED))
            .withPendingTaskCount(taskService.getTotalTasksOfStatus(TaskStatus.PENDING))
            .withTasks(taskService.getTasks(cliTaskListOptions));

        return tableBuilder.build().render();
    }

    @Command(command = "find", description = "Find a task by id")
    public String findTaskById(
        @Option(required = true, description = "The task id to find") Long id
    ) {
        CliTask cliTask = taskService.findTaskById(id);
        return TaskDetailsRenderer.builder()
            .withTask(cliTask)
            .build()
            .render();
    }

    @Command(command = "search", description = "Search tasks by title, creation date, or date range")
    public String searchTasks(
        @Option(description = "Search tasks by title (case-insensitive)") String title,
        @Option(description = "Search tasks created after this date (format: yyyy-MM-dd)", longNames = {"from", "date"}) String fromDate,
        @Option(description = "Search tasks created before this date (format: yyyy-MM-dd)", longNames = {"to"}) String toDate
    ) {

        CliTaskSearchParams cliTaskSearchParams = new CliTaskSearchParams(
            title,
            Optional.ofNullable(fromDate).map(LocalDate::parse).orElse(null),
            Optional.ofNullable(toDate).map(LocalDate::parse).orElse(null)
        );

        TaskTableRenderer.Builder tableBuilder = TaskTableRenderer.builder()
            .withTerminal(terminal)
            .withTotalTaskCount(taskService.getTotalTasks())
            .withCompletedTaskCount(taskService.getTotalTasksOfStatus(TaskStatus.COMPLETED))
            .withPendingTaskCount(taskService.getTotalTasksOfStatus(TaskStatus.PENDING))
            .withTasks(taskService.searchTasks(cliTaskSearchParams));

        return tableBuilder.build().render();
    }

    @Command(command = "create", description = "Creates a new task", interactionMode = InteractionMode.ALL)
    public String createTask(
        @Option(required = true, shortNames = 't', description = "The task title")
        String title
    ) {

        CliTask cliTask = taskService.createTask(new CliTaskCreate(title));
        return "✅ Task created successfully [ID: %s]".formatted(cliTask.id());
    }

    @Command(command = "update", description = "Updates an existing task")
    public String updateTask(
        @Option(required = true, description = "The task id")
        Long id,
        @Option(required = true, description = "The task title", longNames = "title", shortNames = 't')
        String title
    ) {

        CliTask cliTask = taskService.updateTask(new CliTaskUpdate(id, title));
        return "✅ SUCCESS: Task updated [ID: %d]".formatted(cliTask.id());
    }

    @Command(command = "delete", description = "Deletes a task")
    public String deleteTask(
        @Option(required = true, description = "The task id")
        Long id
    ) {
        taskService.deleteTask(id);
        return "✅ SUCCESS: Task deleted [ID: %d]]".formatted(id);
    }

    @Command(command = "toggle", description = "Toggles completion of a task")
    public String toggleTask(
        @Option(required = true, description = "The task id")
        @NotNull @Positive Long id
    ) {
        TaskStatus status = taskService.toggleTaskCompletion(id);
        return "✅ SUCCESS: Task is %s: [ID: %d]".formatted(status.name().toLowerCase(), id);
    }

    @Command(command = "clear", description = "By default deletes all the completed tasks, or all tasks if the `--all` flag is provided")
    public String clearTasks(
        @Option(description = "Delete all tasks", longNames = "all", shortNames = 'a')
        boolean all
    ) {

        String confirmationMessage = all
            ? "Are you sure you want to delete ALL tasks? (y/n): "
            : "Are you sure you want to delete the COMPLETED tasks? (y/n): ";

        if (!confirmAction(confirmationMessage)) {
            return "💭 No tasks deleted";
        }

        long deletedCount = deleteTasks(all);

        if (deletedCount == 0) {
            return "💭 No tasks to delete";
        }

        return "✅ SUCCESS: Completed tasks deleted (%d)".formatted(deletedCount);
    }

    private long deleteTasks(boolean all) {
        return all
            ? taskService.clearTasks()
            : taskService.clearTasksOfStatus(TaskStatus.COMPLETED);
    }

    private boolean confirmAction(String message) {
        System.out.print(message);
        String response = scanner.nextLine().trim().toLowerCase();
        return response.equals("y") || response.equals("yes");
    }
}
