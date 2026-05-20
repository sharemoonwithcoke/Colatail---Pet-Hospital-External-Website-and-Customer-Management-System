package com.example.backend.toDo;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "todo_items")
public class TodoItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    private boolean completed = false;

    private LocalDate createdAt;

    public TodoItem() {}

    public TodoItem(String title) {
        this.title = title;
        this.completed = false;
        this.createdAt = LocalDate.now();
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public boolean isCompleted() { return completed; }
    public void setCompleted(boolean completed) { this.completed = completed; }
    public LocalDate getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDate createdAt) { this.createdAt = createdAt; }
}
