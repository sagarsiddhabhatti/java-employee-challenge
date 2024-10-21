package com.example.rqchallenge.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Employee {

    @JsonProperty("id")
    private String id;

    @JsonProperty("employee_name")
    private String employeeName;

    @JsonProperty("employee_salary")
    private String employeeSalary;

    @JsonProperty("employee_age")
    private String employeeAge;

    @JsonProperty("profile_image")
    private String profileImage;
}
