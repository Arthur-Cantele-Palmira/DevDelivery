package com.devdelivery.restaurant_service.exception;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class StandardErrorResponse {
    private LocalDateTime timestamp;
    private Integer status;
    private String error;
    private String message;
}
