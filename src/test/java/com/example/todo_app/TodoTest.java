package com.example.todo_app;

import com.example.todo_app.model.Todo;
import com.example.todo_app.model.Todo.Priority;
import com.example.todo_app.service.TodoService;

import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

public class TodoTest {

    @InjectMocks
    private TodoService todoService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testCreateTodo() {
        Todo testTodo = new Todo();
        testTodo.setText("Test Todo");
        testTodo.setPriority(Priority.HIGH);

        Todo todo = todoService.createTodo(testTodo);

        assertNotNull(todo);
        assertEquals("Test Todo", todo.getText());
        assertEquals(Priority.HIGH, todo.getPriority());
        assertNotNull(todo.getCreationDate());
        assertFalse(todo.isDone());
    }

    @Test
    public void testUpdateTodo() {
        Todo todo = new Todo();
        todo.setId(1L);
        todo.setText("Test Todo");
        todo.setPriority(Priority.LOW);
        todo.setCreationDate(LocalDateTime.now());

        todoService.createTodo(todo);

        Todo updatedTodo = new Todo();
        updatedTodo.setText("Updated Todo");
        updatedTodo.setPriority(Priority.HIGH);
        updatedTodo.setDueDate(LocalDate.now().plusDays(5));

        Todo finalTodo = todoService.updateTodo(todo.getId(), updatedTodo);
        assertEquals("Updated Todo", finalTodo.getText());
        assertEquals(Priority.HIGH, finalTodo.getPriority());
        assertNotNull(finalTodo.getDueDate());
        assertEquals(todo.getId(), finalTodo.getId());
    }

    @Test
    public void testMarkAsDone() {
        Todo todo = new Todo();
        todo.setId(1L);
        todo.setText("Testing Todo");
        todo.setPriority(Priority.MEDIUM);
        todo.setCreationDate(LocalDateTime.now());

        todoService.createTodo(todo);
        todoService.markAsDone(todo.getId());

        Todo updatedTodo = todoService.markAsDone(todo.getId());

        assertTrue(updatedTodo.isDone());
        assertNotNull(updatedTodo.getDoneDate());
    }

    @Test
    public void testMarkAsUndone() {
        Todo todo = new Todo();
        todo.setId(1L);
        todo.setText("Existing Todo");
        todo.setPriority(Priority.MEDIUM);
        todo.setCreationDate(LocalDateTime.now());
        todo.setDone(true);
        todo.setDoneDate(LocalDateTime.now());

        todoService.createTodo(todo);
        todoService.markAsUndone(todo.getId());

        Todo updatedTodo = todoService.markAsUndone(todo.getId());

        assertFalse(updatedTodo.isDone());
        assertNull(updatedTodo.getDoneDate());
    }

    @Test
    public void testDeleteTodo() {
        Todo todo = new Todo();
        todo.setId(1L);
        todo.setText("Existing Todo");
        todo.setPriority(Priority.MEDIUM);
        todo.setCreationDate(LocalDateTime.now());

        todoService.createTodo(todo);
        todoService.deleteTodo(todo.getId());

        assertThrows(RuntimeException.class, () -> todoService.updateTodo(todo.getId(), new Todo()));
    }
}
