package com.lefpap.taskscli.model.dto;

import com.lefpap.taskscli.model.TaskStatus;

public record CliTaskListOptions(
    boolean all,
    TaskStatus status
) { }
