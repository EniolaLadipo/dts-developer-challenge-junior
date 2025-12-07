package com.example.backend.controllers;

import com.example.backend.models.Task;
import com.example.backend.services.TaskService;
import com.example.backend.dto.TaskResponse;
import com.example.backend.exceptions.InvalidTaskException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import lombok.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/task")
public class TaskController {

    @Autowired
    private final TaskService taskService;

    @PostMapping("/create")
    public ResponseEntity<TaskResponse> createTask(@RequestBody Task task) {

        try {
            // Status code 201: Successful task creation
            Task createdTask = taskService.createTask(task);
            TaskResponse taskResponse = new TaskResponse(
                    "Task created successfully",
                    "success",
                    createdTask
            );
            return new ResponseEntity<>(taskResponse, HttpStatus.CREATED );

        } catch (InvalidTaskException e) {
            // Error code 400: Invalid task entry
            TaskResponse errorResponse = new TaskResponse(
                    e.getMessage(),
                    "client-error",
                    null
            );
            return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);

        } catch (Exception e) {
            // Error code 500: Internal server error
            TaskResponse errorResponse = new TaskResponse(
                    e.getMessage(),
                    "server-error",
                    null
            );
            return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
