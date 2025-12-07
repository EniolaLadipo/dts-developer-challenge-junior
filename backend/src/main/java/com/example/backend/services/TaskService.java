package com.example.backend.services;

import com.example.backend.exceptions.InvalidTaskException;
import com.example.backend.models.Task;
import com.example.backend.repositories.TaskRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import lombok.*;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class TaskService {

    @Autowired
    private final TaskRepository taskRepository;

    public Task createTask(Task task) {
        String title = task.getTitle();
        if (title == null || title.trim().isEmpty()) {
            throw new InvalidTaskException("Error: Task title is empty");
        }

        String status = task.getStatus();
        if (!"Complete".equals(status) && !"In Progress".equals(status) && !"To Do".equals(status)) {
            throw new InvalidTaskException("Error: Task status must either be 'Complete' or 'In Progress' or 'To Do'");
        }

        if (task.getDueDate() == null) {
            throw new InvalidTaskException("Error: Task due date is empty");
        }

        if (task.getDueDate().isBefore(LocalDate.now())) {
            throw new InvalidTaskException("Error: Task due date cannot be in the past");
        }

        return taskRepository.save(task);
    }

}
