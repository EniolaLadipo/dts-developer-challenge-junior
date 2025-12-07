package com.example.backend.repositories;

import com.example.backend.models.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {
    // JpaRepository provides all basic CRUD operations automatically
    // save(), findAll(), findById(), deleteById(), etc.
}