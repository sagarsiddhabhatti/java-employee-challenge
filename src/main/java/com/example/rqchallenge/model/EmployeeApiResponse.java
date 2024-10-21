package com.example.rqchallenge.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EmployeeApiResponse {

    @JsonProperty("status")
    private String status;

    @JsonProperty("data")
    private Employee data;  // The "data" field contains the employee details
}
