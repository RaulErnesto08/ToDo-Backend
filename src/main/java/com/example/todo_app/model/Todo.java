package com.example.todo_app.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;


import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class Todo {
    private Long id;

    @NotBlank
    @Size(max = 120)
    private String text;

    private LocalDate dueDate;
    private boolean done;
    private LocalDateTime doneDate;

    @NotNull
    private Priority priority;

    private LocalDateTime creationDate;

    public enum Priority {
        HIGH, MEDIUM, LOW
    }
}
