package com.example.backend.controllers;

import com.example.backend.exceptions.InvalidTaskException;
import com.example.backend.models.Task;
import com.example.backend.services.TaskService;
import tools.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@WebMvcTest(TaskController.class)
public class TaskControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private TaskService taskService;

    @Autowired
    private ObjectMapper objectMapper;

    private Task testTask;

    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-M-dd");

    @BeforeEach
    void setUp() {
        testTask = new Task();
        testTask.setId(1L);
        testTask.setTitle("Create a task");
        testTask.setDescription("Create a task using this REST API");
        testTask.setStatus("To Do");
        testTask.setDueDate(LocalDate.now());
    }

    @Test
    void createTask_ValidTask_ReturnsSuccessResponse_201() throws Exception {
        when(taskService.createTask(any(Task.class))).thenReturn(testTask);

        mockMvc.perform(post("/api/task/create")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(testTask)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.message", is("Task created successfully")))
                .andExpect(jsonPath("$.status", is("success")))
                .andExpect(jsonPath("$.data.id", is(1)))
                .andExpect(jsonPath("$.data.title", is("Create a task")))
                .andExpect(jsonPath("$.data.description", is("Create a task using this REST API")))
                .andExpect(jsonPath("$.data.status", is("To Do")))
                .andExpect(jsonPath("$.data.dueDate", is(LocalDate.now().format(formatter))));

        verify(taskService, times(1)).createTask(any(Task.class));
    }

    @Test
    void createTask_ValidTask_NoDescription_ReturnsSuccessResponse_201() throws Exception {
        testTask.setDescription(null);

        when(taskService.createTask(any(Task.class))).thenReturn(testTask);

        mockMvc.perform(post("/api/task/create")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(testTask)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.message", is("Task created successfully")))
                .andExpect(jsonPath("$.status", is("success")))
                .andExpect(jsonPath("$.data.id", is(1)))
                .andExpect(jsonPath("$.data.title", is("Create a task")))
                .andExpect(jsonPath("$.data.description", is(nullValue())))
                .andExpect(jsonPath("$.data.status", is("To Do")))
                .andExpect(jsonPath("$.data.dueDate", is(LocalDate.now().format(formatter))));

        verify(taskService, times(1)).createTask(any(Task.class));
    }

    @Test
    void createTask_NoTitle_ReturnsErrorResponse_400() throws Exception {
        testTask.setTitle(null);

        when(taskService.createTask(any(Task.class)))
            .thenThrow(new InvalidTaskException("Error: Task title is empty"));

        mockMvc.perform(post("/api/task/create")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(testTask)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message", is("Error: Task title is empty")))
                .andExpect(jsonPath("$.status", is("client-error")))
                .andExpect(jsonPath("$.data", is(nullValue())));

        verify(taskService, times(1)).createTask(any(Task.class));
    }

    @Test
    void createTask_InvalidStatus_ReturnsErrorResponse_400() throws Exception {
        testTask.setStatus("Finished");

        when(taskService.createTask(any(Task.class)))
            .thenThrow(new InvalidTaskException("Error: Task status must either be 'Complete' or 'In Progress' or 'To Do'"));

        mockMvc.perform(post("/api/task/create")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(testTask)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message", is("Error: Task status must either be 'Complete' or 'In Progress' or 'To Do'")))
                .andExpect(jsonPath("$.status", is("client-error")))
                .andExpect(jsonPath("$.data", is(nullValue())));

        verify(taskService, times(1)).createTask(any(Task.class));
    }

    @Test
    void createTask_InvalidDueDate_ReturnsErrorResponse_400() throws Exception {

        LocalDate yesterday = LocalDate.now().minusDays(1);

        testTask.setDueDate(yesterday);

        when(taskService.createTask(any(Task.class)))
            .thenThrow(new InvalidTaskException("Error: Task due date cannot be in the past"));

        mockMvc.perform(post("/api/task/create")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(testTask)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message", is("Error: Task due date cannot be in the past")))
                .andExpect(jsonPath("$.status", is("client-error")))
                .andExpect(jsonPath("$.data", is(nullValue())));

        verify(taskService, times(1)).createTask(any(Task.class));
    }

    @Test
    void createTask_NoDueDate_ReturnsErrorResponse_400() throws Exception {

        testTask.setDueDate(null);

        when(taskService.createTask(any(Task.class)))
                .thenThrow(new InvalidTaskException("Error: Task due date is empty"));

        mockMvc.perform(post("/api/task/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testTask)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message", is("Error: Task due date is empty")))
                .andExpect(jsonPath("$.status", is("client-error")))
                .andExpect(jsonPath("$.data", is(nullValue())));

        verify(taskService, times(1)).createTask(any(Task.class));
    }

    @Test
    void createTask_GenericException_ReturnsInternalServerError_500() throws Exception {
        when(taskService.createTask(any(Task.class)))
            .thenThrow(new RuntimeException("Something went wrong"));

        mockMvc.perform(post("/api/task/create")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(testTask)))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.message", is("Something went wrong")))
                .andExpect(jsonPath("$.status", is("server-error")))
                .andExpect(jsonPath("$.data", is(nullValue())));

    }
}
