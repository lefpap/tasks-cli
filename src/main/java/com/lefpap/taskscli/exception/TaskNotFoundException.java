package com.lefpap.taskscli.exception;

public class TaskNotFoundException extends RuntimeException {

    public TaskNotFoundException(String message) {
        super(message);
    }

    public TaskNotFoundException(Long id) {
        super("Task with id: %s not found".formatted(id));
    }
}
