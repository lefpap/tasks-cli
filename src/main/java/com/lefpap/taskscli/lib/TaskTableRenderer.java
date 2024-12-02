package com.lefpap.taskscli.lib;

import com.lefpap.taskscli.model.TaskStatus;
import com.lefpap.taskscli.model.dto.CliTask;
import org.jline.terminal.Terminal;
import org.springframework.shell.table.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public final class TaskTableRenderer {

    private static final String TASK_COLUMN_ID = "ID";
    private static final String TASK_COLUMN_TITLE = "Title";
    private static final String TASK_COLUMN_COMPLETED = "COMPLETED";

    private static final String TASK_COMPLETED_VALUE = "[X]";
    private static final String TASK_PENDING_VALUE = "[ ]";

    private final Terminal terminal;
    private final List<CliTask> tasks;
    private final Long totalTaskCount;
    private final Long completedTaskCount;
    private final Long pendingTaskCount;

    private TaskTableRenderer(Builder builder) {
        // Prevent instantiation
        this.terminal = builder.terminal;
        this.tasks = List.copyOf(builder.tasks);
        this.totalTaskCount = builder.totalTaskCount;
        this.completedTaskCount = builder.completedTaskCount;
        this.pendingTaskCount = builder.pendingTaskCount;
    }

    public static Builder builder() {
        return new Builder();
    }

    public String render() {
        if (tasks.isEmpty()) {
            return "💭 No tasks found.";
        }

        Table tasksTable = buildTaskTable();
        String metadata = buildMetaData();

        return """
            %s%s
            """
            .formatted(
                metadata,
                tasksTable.render(terminal.getWidth())
            );
    }

    private Table buildTaskTable() {
        TableModelBuilder<String> modelBuilder = new TableModelBuilder<>();
        modelBuilder.addRow()
            .addValue(TASK_COLUMN_ID)
            .addValue(TASK_COLUMN_TITLE)
            .addValue(TASK_COLUMN_COMPLETED);

        tasks.forEach(cliTask -> modelBuilder.addRow()
            .addValue(String.valueOf(cliTask.id()))
            .addValue(cliTask.title())
            .addValue(TaskStatus.COMPLETED.equals(cliTask.status())
                ? TASK_COMPLETED_VALUE
                : TASK_PENDING_VALUE));

        return new TableBuilder(modelBuilder.build())
            .addFullBorder(BorderStyle.fancy_heavy)
            .on(CellMatchers.column(0)).addSizer(new AbsoluteWidthSizeConstraints(5))
            .on(CellMatchers.column(1)).addSizer(new AbsoluteWidthSizeConstraints(50))
            .on(CellMatchers.column(2)).addAligner(SimpleVerticalAligner.middle).addAligner(SimpleHorizontalAligner.center)
            .build();
    }

    private String buildMetaData() {
        return """
            TASKS (%d/%d)
            Completed: %s | Pending: %s
            """
            .formatted(tasks.size(), totalTaskCount, completedTaskCount, pendingTaskCount);
    }

    public static class Builder {
        private List<CliTask> tasks = new ArrayList<>();
        private Terminal terminal;
        private long totalTaskCount;
        private long completedTaskCount;
        private long pendingTaskCount;

        public Builder withTasks(List<CliTask> tasks) {
            this.tasks = (tasks != null)
                ? new ArrayList<>(tasks)
                : new ArrayList<>();

            return this;
        }

        public Builder addTasks(List<CliTask> tasks) {
            if (Objects.isNull(tasks)) {
                this.tasks = new ArrayList<>();
            }
            this.tasks.addAll(new ArrayList<>(tasks));
            return this;
        }

        public Builder addTask(CliTask task) {
            if (Objects.isNull(task)) {
                return this;
            }
            this.tasks.add(task);
            return this;
        }

        public Builder withTerminal(Terminal terminal) {
            this.terminal = terminal;
            return this;
        }

        public Builder withTotalTaskCount(long totalTaskCount) {
            this.totalTaskCount = totalTaskCount;
            return this;
        }

        public Builder withCompletedTaskCount(long completedTaskCount) {
            this.completedTaskCount = completedTaskCount;
            return this;
        }

        public Builder withPendingTaskCount(long pendingTaskCount) {
            this.pendingTaskCount = pendingTaskCount;
            return this;
        }

        public TaskTableRenderer build() {
            return new TaskTableRenderer(this);
        }
    }

}
