package com.example.rqchallenge.exceptions;

public class EmployeeServiceException extends RuntimeException {
    public EmployeeServiceException(String message) {
        super(message);
    }

    public EmployeeServiceException(String message, Throwable cause) {
        super(message, cause);
    }
}
