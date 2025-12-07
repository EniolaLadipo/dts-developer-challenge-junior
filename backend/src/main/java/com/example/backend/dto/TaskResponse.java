package com.example.backend.dto;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TaskResponse {

    private String message;
    private String status;
    private Object data;
}
