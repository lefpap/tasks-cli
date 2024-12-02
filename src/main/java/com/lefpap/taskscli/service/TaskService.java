package com.lefpap.taskscli.service;

import com.lefpap.taskscli.exception.TaskNotFoundException;
import com.lefpap.taskscli.mapper.TaskMapper;
import com.lefpap.taskscli.model.Task;
import com.lefpap.taskscli.model.TaskStatus;
import com.lefpap.taskscli.model.dto.*;
import com.lefpap.taskscli.store.TaskStore;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;

@Service
public class TaskService {

    private final TaskStore taskStore;
    private final TaskMapper taskMapper;

    public TaskService(TaskStore taskStore, TaskMapper taskMapper) {
        this.taskStore = taskStore;
        this.taskMapper = taskMapper;
    }

    public List<CliTask> getTasks(CliTaskListOptions cliTaskListOptions) {
        return Optional.ofNullable(cliTaskListOptions)
            .map(options -> {
                if (cliTaskListOptions.all()) {
                    return taskMapper.toCliTaskList(taskStore.findAll());
                }
                if (Objects.nonNull(cliTaskListOptions.status())) {
                    return taskMapper.toCliTaskList(taskStore.findByStatus(cliTaskListOptions.status()));
                }

                return taskMapper.toCliTaskList(taskStore.findAll());
            })
            .orElseGet(() -> taskMapper.toCliTaskList(taskStore.findAll()));

    }

    public long getTotalTasks() {
        return taskStore.countAll();
    }

    public CliTask findTaskById(Long id) {
        Task task = taskStore.findOne(id)
            .orElseThrow(() -> new TaskNotFoundException(id));

        return taskMapper.toCliTask(task);
    }

    public List<CliTask> searchTasks(CliTaskSearchParams cliTaskSearchParams) {
        // TODO: improve searching of tasks

        Stream<Task> taskStream = taskStore.findAll().stream();
        if (Objects.nonNull(cliTaskSearchParams.title())) {
            taskStream = taskStream.filter(task -> task.title().contains(cliTaskSearchParams.title()));
        }

        LocalDate fromDate = cliTaskSearchParams.fromDate();
        LocalDate toDate = cliTaskSearchParams.toDate();
        if (Objects.nonNull(fromDate) && Objects.nonNull(toDate)) {
            taskStream = taskStream.filter(task -> {
                LocalDate createdDate = task.createdAt().atZone(ZoneId.systemDefault()).toLocalDate();
                return createdDate.isAfter(fromDate) && createdDate.isBefore(toDate);
            });
        } else if (Objects.nonNull(fromDate)) {
            taskStream = taskStream.filter(task -> {
                LocalDate createdDate = task.createdAt().atZone(ZoneId.systemDefault()).toLocalDate();
                return createdDate.isEqual(fromDate);
            });
        }

        return taskMapper.toCliTaskList(taskStream.toList());
    }

    public List<CliTask> getTasksOfStatus(TaskStatus status) {
        return taskMapper.toCliTaskList(taskStore.findByStatus(status));
    }

    public long getTotalTasksOfStatus(TaskStatus status) {
        return taskStore.countByStatus(status);
    }

    public CliTask createTask(CliTaskCreate cliTaskCreate) {
        Task createdTask = taskStore.save(taskMapper.toTask(cliTaskCreate));
        return taskMapper.toCliTask(createdTask);
    }

    public CliTask updateTask(CliTaskUpdate cliTaskUpdate) {
        Task task = taskStore.findOne(cliTaskUpdate.id())
            .orElseThrow();

        Task updatedTask = taskStore.save(taskMapper.mergeToTask(cliTaskUpdate, task));
        return taskMapper.toCliTask(updatedTask);
    }

    public void deleteTask(Long id) {
        taskStore.findOne(id)
            .map(Task::id)
            .ifPresent(taskStore::delete);
    }

    public TaskStatus toggleTaskCompletion(Long id) {
        Task task = taskStore.findOne(id)
            .orElseThrow(() -> new TaskNotFoundException(id));

        Task toggledTask = task.withStatus(TaskStatus.COMPLETED.equals(task.status())
            ? TaskStatus.PENDING
            : TaskStatus.COMPLETED
        );

        return taskStore.save(toggledTask).status();
    }

    public long clearTasksOfStatus(TaskStatus status) {
        return taskStore.clearTasksOfStatus(status);
    }

    public long clearTasks() {
        return taskStore.clear();
    }
}
