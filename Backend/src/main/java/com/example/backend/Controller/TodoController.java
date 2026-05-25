package com.example.backend.Controller;

import com.example.backend.toDo.TodoItem;
import com.example.backend.toDo.TodoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/todos")
public class TodoController {

    private final TodoRepository todoRepository;

    @Autowired
    public TodoController(TodoRepository todoRepository) {
        this.todoRepository = todoRepository;
    }

    @GetMapping
    public List<TodoItem> getAll() {
        return todoRepository.findAllByOrderByCreatedAtDesc();
    }

    @PostMapping
    public ResponseEntity<TodoItem> create(@RequestBody TodoRequest req) {
        return ResponseEntity.ok(todoRepository.save(new TodoItem(req.title)));
    }

    @PutMapping("/{id}/toggle")
    public ResponseEntity<TodoItem> toggle(@PathVariable Long id) {
        return todoRepository.findById(id).map(item -> {
            item.setCompleted(!item.isCompleted());
            return ResponseEntity.ok(todoRepository.save(item));
        }).orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        if (!todoRepository.existsById(id)) return ResponseEntity.notFound().build();
        todoRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    static class TodoRequest {
        public String title;
    }
}
