package com.lefpap.taskscli.model;

public record Task(
    Long id,
    String title,
    TaskStatus status
) { }
