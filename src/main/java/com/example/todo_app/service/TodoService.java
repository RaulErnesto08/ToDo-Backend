package com.example.todo_app.service;

import com.example.todo_app.model.Todo;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;

@Service
public class TodoService {

    private final Map<Long, Todo> todoList = new HashMap<>();
    private long count = 0;

    public List<Todo> getTodos() {
        return new ArrayList<>(todoList.values());
    }

    public Todo createTodo(Todo todo) {
        todo.setId(++count);
        todo.setCreationDate(LocalDateTime.now());
        todoList.put(todo.getId(), todo);

        return todo;
    }

    public Todo updateTodo(Long id, Todo updatedTodo) {
        Todo todo = todoList.get(id);

        if (todo == null) {
          throw new RuntimeException("Todo not Found"); // Return 404
        }

        todo.setText(updatedTodo.getText());
        todo.setDueDate(updatedTodo.getDueDate());
        todo.setPriority(updatedTodo.getPriority());

        return todo;
    }

    public void deleteTodo(Long id) {
        todoList.remove(id);
    }

    public Todo markAsDone(Long id) {
        Todo todo = todoList.get(id);

        if (todo != null && !todo.isDone()) {
            todo.setDone(true);
            todo.setDoneDate(LocalDateTime.now());
            return todo;
        }

        return todo;
    }

    public Todo markAsUndone(Long id) {
        Todo todo = todoList.get(id);

        if (todo != null && todo.isDone()) {
            todo.setDone(false);
            todo.setDoneDate(null);
            return todo;
        }

        return todo;
    }

}
