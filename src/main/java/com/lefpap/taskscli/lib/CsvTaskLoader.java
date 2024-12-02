package com.lefpap.taskscli.lib;

import com.lefpap.taskscli.config.properties.TaskStoreCsvProps;
import com.lefpap.taskscli.model.Task;
import com.lefpap.taskscli.model.TaskStatus;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Path;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
@Profile("csv")
public final class CsvTaskLoader {

    private final TaskStoreCsvProps taskCsvProps;
    private final CsvUtils csvUtils;

    public CsvTaskLoader(TaskStoreCsvProps taskCsvProps) {
        this.taskCsvProps = taskCsvProps;
        this.csvUtils = CsvUtils.create(taskCsvProps.delimiter());
    }

    public Map<Long, Task> loadTasks() {
        Path filePath = Path.of(taskCsvProps.filepath());
        try {
            List<String[]> csvRows = csvUtils.readCsv(filePath);
            return csvRows.stream()
                .map(this::convert)
                .collect(Collectors.toMap(Task::id, Function.identity()));
        } catch (IOException e) {
            throw new RuntimeException("Failed to load tasks from CSV file", e);
        }
    }

    private Task convert(String[] source) {
        if (source.length < 3) {
            throw new IllegalArgumentException("Invalid CSV row: " + String.join(",", source));
        }

        Long id = Long.parseLong(source[0]);
        String title = source[1];
        TaskStatus status = TaskStatus.valueOf(source[2]);
        //TODO: update conversions from csv to to task and vise versa
        Instant createdAt = Instant.parse(source[3]);
        Instant updatedAt = source[4].equals("null")
            ? null
            : Instant.parse(source[4]);

        return Task.empty()
            .withId(id)
            .withTitle(title)
            .withStatus(status)
            .withCreatedAt(createdAt)
            .withUpdatedAt(updatedAt);
    }

    public void saveTasks(Map<Long, Task> taskMap) {
        Path filePath = Path.of(taskCsvProps.filepath());
        try {
            List<String[]> csvRows = taskMap.values().stream()
                .map(this::convert)
                .toList();
            csvUtils.writeCsv(filePath, csvRows);
        } catch (IOException e) {
            throw new RuntimeException("Failed to save tasks to CSV file", e);
        }
    }

    private String[] convert(Task task) {
        return new String[] {
            task.id().toString(),
            task.title(),
            task.status().name(),
            String.valueOf(task.createdAt()),
            String.valueOf(task.updatedAt())
        };
    }
}
