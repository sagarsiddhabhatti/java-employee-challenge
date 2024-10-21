package com.example.rqchallenge.service;

import com.example.rqchallenge.model.Employee;

import java.util.List;

public interface EmployeeService {

    List<Employee> getAllEmployees();

    Employee getEmployeeById(String id);

    List<Employee> getEmployeesByNameSearch(String name);

    int getHighestSalaryOfEmployees();

    List<String> getTopTenHighestEarningEmployeeNames();

    String createEmployee(Employee employee);

    String deleteEmployeeById(String id);

}
