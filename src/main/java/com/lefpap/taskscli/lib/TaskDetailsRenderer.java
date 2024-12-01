package com.lefpap.taskscli.lib;

import com.lefpap.taskscli.model.TaskStatus;
import com.lefpap.taskscli.model.dto.CliTask;
import org.springframework.shell.table.*;

public final class TaskDetailsRenderer {

    private static final String TASK_LABEL_ID = "ID";
    private static final String TASK_LABEL_TITLE = "Title";
    private static final String TASK_LABEL_COMPLETED = "COMPLETED";

    private static final String TASK_COMPLETED_VALUE = "[X]";
    private static final String TASK_PENDING_VALUE = "[ ]";

    private final int terminalWidth;
    private final CliTask task;
    private final String title;

    private TaskDetailsRenderer(Builder builder) {
        // Prevent instantiation
        this.terminalWidth = builder.terminalWidth;
        this.task = builder.task;
        this.title = builder.title;
    }

    public static Builder builder() {
        return new Builder();
    }

    public String render() {
        Table taskDetails = buildTaskDetails();

        return """
            %s
            %s
            """
            .formatted(
                title,
                taskDetails.render(terminalWidth)
            );
    }

    private Table buildTaskDetails() {
        TableModelBuilder<String> modelBuilder = new TableModelBuilder<>();
        modelBuilder
            .addRow()
                .addValue(TASK_LABEL_ID)
                .addValue(String.valueOf(task.id()))
            .addRow()
                .addValue(TASK_LABEL_TITLE)
                .addValue(task.title())
            .addRow()
                .addValue(TASK_LABEL_COMPLETED)
                .addValue(TaskStatus.COMPLETED.equals(task.status())
                    ? TASK_COMPLETED_VALUE
                    : TASK_PENDING_VALUE
                );

        return new TableBuilder(modelBuilder.build())
            .addFullBorder(BorderStyle.fancy_heavy)
            .on(CellMatchers.column(0)).addSizer(new NoWrapSizeConstraints())
            .on(CellMatchers.column(1)).addSizer(new NoWrapSizeConstraints())
            .on(CellMatchers.column(2))
                .addAligner(SimpleHorizontalAligner.center)
                .addSizer(new NoWrapSizeConstraints())
            .build();
    }

    public static class Builder {
        private CliTask task;
        private String title;
        private int terminalWidth;

        public Builder withTask(CliTask task) {
            this.task = task;
            return this;
        }

        public Builder withTitle(String title) {
            this.title = title;
            return this;
        }

        public Builder withTerminalWidth(int width) {
            this.terminalWidth = width;
            return this;
        }

        public TaskDetailsRenderer build() {
            return new TaskDetailsRenderer(this);
        }
    }

}
