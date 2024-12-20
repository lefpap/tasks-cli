package com.lefpap.taskscli.service;

import com.lefpap.taskscli.mapper.TaskMapper;
import com.lefpap.taskscli.model.Task;
import com.lefpap.taskscli.model.TaskStatus;
import com.lefpap.taskscli.model.dto.CliTask;
import com.lefpap.taskscli.model.dto.CliTaskCreate;
import com.lefpap.taskscli.model.dto.CliTaskUpdate;
import com.lefpap.taskscli.store.TaskStore;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TaskService {

    private final TaskStore taskStore;
    private final TaskMapper taskMapper;

    public TaskService(TaskStore taskStore, TaskMapper taskMapper) {
        this.taskStore = taskStore;
        this.taskMapper = taskMapper;
    }

    public List<CliTask> getTasks() {
        return taskMapper.toCliTaskList(taskStore.findAll());
    }

    public long getTotalTasks() {
        return taskStore.countAll();
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

    public void clearTasksOfStatus(TaskStatus status) {
        taskStore.clearTasksOfStatus(status);
    }

    public void clearTasks() {
        taskStore.clear();
    }
}
