package com.lefpap.taskscli.model;

import java.time.Instant;

public record Task(
    Long id,
    String title,
    TaskStatus status,
    Instant createdAt,
    Instant updatedAt
) {

    public static Task empty() {
        return new Task(
            null,
            null,
            null,
            null,
            null
        );
    }

    public Task withId(Long id) {
        return new Task(
            id,
            this.title(),
            this.status(),
            this.createdAt(),
            this.updatedAt()
        );
    }

    public Task withTitle(String title) {
        return new Task(
            this.id(),
            title,
            this.status(),
            this.createdAt(),
            this.updatedAt()
        );
    }

    public Task withStatus(TaskStatus status) {
        return new Task(
            this.id(),
            this.title(),
            status,
            this.createdAt(),
            this.updatedAt()
        );
    }

    public Task withCreatedAt(Instant createdAt) {
        return new Task(
            this.id(),
            this.title(),
            this.status(),
            createdAt,
            this.updatedAt()
        );
    }

    public Task withUpdatedAt(Instant updatedAt) {
        return new Task(
            this.id(),
            this.title(),
            this.status(),
            this.createdAt(),
            updatedAt
        );
    }
}
