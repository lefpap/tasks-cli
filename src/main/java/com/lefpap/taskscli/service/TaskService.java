package com.lefpap.taskscli.service;

import com.lefpap.taskscli.mapper.TaskMapper;
import com.lefpap.taskscli.model.Task;
import com.lefpap.taskscli.model.dto.CliTask;
import com.lefpap.taskscli.model.dto.CliTaskCreate;
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

    public CliTask createTask(CliTaskCreate cliTaskCreate) {
        Task createdTask = taskStore.save(taskMapper.toTask(cliTaskCreate));
        return taskMapper.toCliTask(createdTask);
    }
}
