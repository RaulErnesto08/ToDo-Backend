package com.example.todo_app.model;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class Todo {
    private Long id;
    private String text;
    private LocalDateTime dueDate;
    private boolean done;
    private LocalDateTime doneDate;
    private Priority priority;
    private LocalDateTime creationDate;

    public enum Priority {
        HIGH, MEDIUM, LOW
    }
}
