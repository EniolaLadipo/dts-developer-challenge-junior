package com.example.backend.services;

import com.example.backend.models.Task;
import com.example.backend.repositories.TaskRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Optional;

import com.example.backend.exceptions.InvalidTaskException;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TaskServiceTest {

    @Mock
    private TaskRepository taskRepository;

    @InjectMocks
    private TaskService taskService;

    private Task testTask;

    @BeforeEach
    void setUp() {

        testTask = new Task();
        testTask.setId(1L);
        testTask.setTitle("Create API");
        testTask.setDescription("Create a task using this REST API");
        testTask.setStatus("In Progress");
        testTask.setDueDate(LocalDate.now());
    }

    @Test
    void createTask_ReturnsValidResponse() {
        when(taskRepository.save(any(Task.class))).thenReturn(testTask);

        Task result = taskService.createTask(testTask);

        assertNotNull(result);
        assertEquals(testTask.getId(), result.getId());
        assertEquals(testTask.getTitle(), result.getTitle());
        assertEquals(testTask.getDescription(), result.getDescription());
        assertEquals(testTask.getStatus(), result.getStatus());
        assertEquals(testTask.getDueDate(), result.getDueDate());

        verify(taskRepository, times(1)).save(testTask);
    }

    @Test
    void createTask_NullDescription_ReturnsValidResponse() {

        testTask.setDescription(null);

        when(taskRepository.save(any(Task.class))).thenReturn(testTask);

        Task result = taskService.createTask(testTask);

        assertNotNull(result);
        assertNull(result.getDescription());
        verify(taskRepository, times(1)).save(testTask);
    }

    @Test
    void createTask_NullTitle_ThrowsInvalidTaskException() {

        testTask.setTitle(null);

        InvalidTaskException exception = assertThrows(
                InvalidTaskException.class,
                () -> taskService.createTask(testTask)
        );

        assertEquals("Error: Task title is empty", exception.getMessage());
        verify(taskRepository, never()).save(any());
    }

    @Test
    void createTask_InvalidStatus_ThrowsInvalidTaskException() {

        testTask.setStatus("Finished");

        InvalidTaskException exception = assertThrows(
                InvalidTaskException.class,
                () -> taskService.createTask(testTask)
        );
        assertEquals("Error: Task status must either be 'Complete' or 'In Progress' or 'To Do'", exception.getMessage());
        verify(taskRepository, never()).save(any());
    }

    @Test
    void createTask_NullDueDate_ThrowsInvalidTaskException() {

        testTask.setDueDate(null);

        InvalidTaskException exception = assertThrows(
                InvalidTaskException.class,
                () -> taskService.createTask(testTask)
        );
        assertEquals("Error: Task due date is empty", exception.getMessage());
        verify(taskRepository, never()).save(any());
    }

    @Test
    void createTask_InvalidDueDate_ThrowsInvalidTaskException() {

        LocalDate yesterday = LocalDate.now().minusDays(1);

        testTask.setDueDate(yesterday);

        InvalidTaskException exception = assertThrows(
                InvalidTaskException.class,
                () -> taskService.createTask(testTask)
        );

        assertEquals("Error: Task due date cannot be in the past", exception.getMessage());
        verify(taskRepository, never()).save(any());
    }
}
