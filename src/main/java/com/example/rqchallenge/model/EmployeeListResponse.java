package com.example.rqchallenge.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
@Data
@AllArgsConstructor
@NoArgsConstructor
public class EmployeeListResponse {
    @JsonProperty("status")
    private String status;

    @JsonProperty("data")
    private List<Employee> data;  // The "data" field contains the employee details
}
