package com.example.rqchallenge.exceptions;

import lombok.Data;

@Data
public class ErrorResponse {
    private final String message;
    private final String errorCode;
}
