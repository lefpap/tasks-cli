package com.lefpap.taskscli.model.dto;

import com.lefpap.taskscli.model.TaskStatus;

public record CliTask(
    Long id,
    String title,
    TaskStatus status
) { }
