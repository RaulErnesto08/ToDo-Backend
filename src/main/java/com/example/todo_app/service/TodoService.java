package com.example.todo_app.service;

import com.example.todo_app.model.Todo;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.*;
import java.time.LocalDateTime;
import java.util.stream.Collectors;

@Service
public class TodoService {

    private final Map<Long, Todo> todoList = new HashMap<>();
    private long count = 0;

    public List<Todo> getTodos(int page,
       int size,
       String sortBy,
       String sortOrder,
       String textFilter,
       String priorityFilter,
       Boolean doneFilter) {

        Comparator<Todo> comparator = switch (sortBy) {
            case "priority" -> Comparator.comparing(Todo::getPriority);
            case "dueDate" -> Comparator.comparing(Todo::getDueDate, Comparator.nullsLast(Comparator.naturalOrder()));
            default -> Comparator.comparing(Todo::getCreationDate);
        };

        if(sortOrder.equalsIgnoreCase("desc")) {
            comparator = comparator.reversed();
        }

        return todoList.values().stream()
                .filter(todo -> textFilter == null || todo.getText().contains(textFilter))
                .filter(todo -> priorityFilter == null || priorityFilter.isEmpty() || todo.getPriority().name().equalsIgnoreCase(priorityFilter))
                .filter(todo -> doneFilter == null || todo.isDone() == doneFilter)
                .sorted(comparator)
                .skip((long) page * size)
                .limit(size)
                .collect(Collectors.toList());
    }

    public long getTotalTodos(String textFilter, String priorityFilter, Boolean doneFilter) {
        return todoList.values().stream()
                .filter(todo -> textFilter == null || todo.getText().contains(textFilter))
                .filter(todo -> priorityFilter == null || priorityFilter.isEmpty() || todo.getPriority().name().equalsIgnoreCase(priorityFilter))
                .filter(todo -> doneFilter == null || todo.isDone() == doneFilter)
                .count();
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
          throw new RuntimeException("Todo not Found");
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

    public Map<String, Object> getMetrics() {
        List <Todo> completedTodos = todoList.values().stream()
                .filter(Todo::isDone)
                .toList();

        List<Long> completedTimes = completedTodos.stream()
                .map(todo -> getTimeDiff(todo.getCreationDate(), todo.getDoneDate()))
                .toList();

        double averageTimeForAllTasks = completedTimes.stream()
                .mapToLong(Long::longValue)
                .average()
                .orElse(0);

        Map<String, List<Long>> timesByPriority = new HashMap<>();
        for (Todo todo : completedTodos) {
            String priority = todo.getPriority().name();
            long timeDiff = getTimeDiff(todo.getCreationDate(), todo.getDoneDate());
            if(!timesByPriority.containsKey(priority)) {
                timesByPriority.put(priority, new ArrayList<>());
            }

            timesByPriority.get(priority).add(timeDiff);
        }

        Map<String, Double> averageTimeByPriority = new HashMap<>();
        timesByPriority.forEach((priority, times) -> {
            double average = 0;
            if (!times.isEmpty()) {
                long sum = 0;
                for(Long time : times) {
                    sum += time;
                }
                average = (double) sum / times.size();
            }
            averageTimeByPriority.put(priority, average);
        });

        Map<String, Object> metrics = new HashMap<>();
        metrics.put("averageTimeForAllTasks", averageTimeForAllTasks);
        metrics.put("averageTimeByPriority", averageTimeByPriority);

        return metrics;

    }

    private long getTimeDiff(LocalDateTime creationDate, LocalDateTime doneDate) {
        return Duration.between(creationDate, doneDate).getSeconds();
    }

}
