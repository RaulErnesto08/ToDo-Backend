package com.example.todo_app.controller;

import com.example.todo_app.model.Todo;
import com.example.todo_app.service.TodoService;

import java.util.List;
import java.util.Map;

import jakarta.validation.Valid;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@CrossOrigin(origins = "http://localhost:3000", exposedHeaders = "x-total-count")
@RestController
@RequestMapping("/todos")
public class TodoController {

    private final TodoService todoService;

    public TodoController(TodoService todoService) {
        this.todoService = todoService;
    }

    @GetMapping
    public ResponseEntity<List<Todo>> getTodos(@RequestParam(defaultValue = "0") int page,
       @RequestParam(defaultValue = "10") int size,
       @RequestParam(defaultValue = "creationDate") String sortBy,
       @RequestParam(defaultValue = "asc") String sortOrder,
       @RequestParam(required = false) String textFilter,
       @RequestParam(required = false) String priorityFilter,
       @RequestParam(required = false) Boolean doneFilter) {

        List<Todo> todos = todoService.getTodos(page, size, sortBy, sortOrder, textFilter, priorityFilter, doneFilter);
        long totalTodos = todoService.getTotalTodos(textFilter, priorityFilter, doneFilter);

        HttpHeaders headers = new HttpHeaders();
        headers.add("x-total-count", String.valueOf(totalTodos));

        return ResponseEntity.ok().headers(headers).body(todos);
    }

    @PostMapping
    public Todo createTodo(@Valid @RequestBody Todo todo) {
        return todoService.createTodo(todo);
    }

    @PutMapping("/{id}")
    public Todo updateTodo(@PathVariable Long id, @RequestBody Todo updatedTodo) {
        return todoService.updateTodo(id, updatedTodo);
    }

    @DeleteMapping("/{id}")
    public void deleteTodo(@PathVariable Long id) {
        todoService.deleteTodo(id);
    }

    @PostMapping("/{id}/done")
    public Todo markAsDone(@PathVariable Long id) {
        return todoService.markAsDone(id);
    }

    @PutMapping("/{id}/undone")
    public Todo markAsUndone(@PathVariable Long id) {
        return todoService.markAsUndone(id);
    }

    @GetMapping("/metrics")
    public ResponseEntity<Map<String, Object>> getMetrics() {
        Map<String, Object> metrics = todoService.getMetrics();
        return ResponseEntity.ok(metrics);
    }
}
