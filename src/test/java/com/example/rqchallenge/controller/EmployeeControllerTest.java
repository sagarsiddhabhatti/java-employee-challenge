package com.example.rqchallenge.controller;

import com.example.rqchallenge.model.Employee;
import com.example.rqchallenge.service.EmployeeService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;
import utils.MockEmployeeDataGenerator;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class EmployeeControllerTest {

    @Mock
    private EmployeeService employeeService;

    @InjectMocks
    private EmployeeController employeeController;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testGetAllEmployees_Success() {
        // Arrange
        List<Employee> employees = MockEmployeeDataGenerator.generateEmployees(50);

        when(employeeService.getAllEmployees()).thenReturn(employees);

        // Act
        ResponseEntity<List<Employee>> response = employeeController.getAllEmployees();

        // Assert
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(employees, response.getBody());
        verify(employeeService, times(1)).getAllEmployees();
    }

    @Test

    public void testGetAllEmployees_Error() {
        // Arrange
        when(employeeService.getAllEmployees()).thenThrow(new RuntimeException("Service unavailable"));

        assertThrows(RuntimeException.class, () -> {
            employeeController.getAllEmployees();
        });
    }

    @Test
    public void testSearchEmployees_Success() {
        // Arrange
        List<Employee> employees = Arrays.asList(new Employee("1", "John Doe", "50000", "30",""));
        String searchString = "John";

        when(employeeService.getEmployeesByNameSearch(searchString)).thenReturn(employees);

        // Act
        ResponseEntity<List<Employee>> response = employeeController.getEmployeesByNameSearch(searchString);

        // Assert
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(employees, response.getBody());
        assertEquals(employees.get(0).getEmployeeName(), response.getBody().get(0).getEmployeeName());
        verify(employeeService, times(1)).getEmployeesByNameSearch(searchString);
    }

    @Test
    public void testGetEmployeeById_Success() {
        // Arrange
        Employee employee = new Employee("1", "John Doe", "50000", "30","");
        String id = "1";

        when(employeeService.getEmployeeById(id)).thenReturn(employee);

        // Act
        ResponseEntity<Employee> response = employeeController.getEmployeeById(id);

        // Assert
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(employee, response.getBody());
        verify(employeeService, times(1)).getEmployeeById(id);
    }

    @Test
    public void testGetEmployeeById_Error() {
        // Arrange
        String id = "999";
        when(employeeService.getEmployeeById(id)).thenThrow(new RuntimeException("Employee not found"));
        assertThrows(RuntimeException.class, () -> {
            employeeController.getEmployeeById(id);
        });

    }

    @Test
    public void testCreateEmployee_Success() {
        // Arrange
        Map<String, Object> employeeInput = new HashMap<>();
        employeeInput.put("name", "John Doe");
        employeeInput.put("salary", "50000");
        employeeInput.put("age", "30");
        String responseMessage = "Employee created successfully";

        when(employeeService.createEmployee(any(Employee.class))).thenReturn(responseMessage);

        // Act
        ResponseEntity<String> response = employeeController.createEmployee(employeeInput);

        // Assert
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(responseMessage, response.getBody());
        ArgumentCaptor<Employee> employeeCaptor = ArgumentCaptor.forClass(Employee.class);
        verify(employeeService, times(1)).createEmployee(employeeCaptor.capture());

        Employee capturedEmployee = employeeCaptor.getValue();
        assertEquals("John Doe", capturedEmployee.getEmployeeName());
        assertEquals("50000", capturedEmployee.getEmployeeSalary());
        assertEquals("30", capturedEmployee.getEmployeeAge());
    }

    @Test
    public void testCreateEmployee_Error() {

        Map<String, Object> employeeInput = new HashMap<>();
        employeeInput.put("name", "John Doe");
        employeeInput.put("salary", "50000");
        employeeInput.put("age", "30");
        when(employeeService.createEmployee(any(Employee.class))).thenThrow(new RuntimeException("Failed to create employee"));

        assertThrows(RuntimeException.class, () -> {
            employeeController.createEmployee(employeeInput);
        });
    }

    @Test
    public void testDeleteEmployee_Success() {
        // Arrange
        String id = "1";
        String employeeName = "John Doe";

        when(employeeService.deleteEmployeeById(id)).thenReturn(employeeName);

        // Act
        ResponseEntity<String> response = employeeController.deleteEmployeeById(id);

        // Assert
        assertEquals(200, response.getStatusCodeValue());
        assertEquals( employeeName, response.getBody());
        verify(employeeService, times(1)).deleteEmployeeById(id);
    }

    @Test
    public void testDeleteEmployee_Error() {
        String id = "999";
        when(employeeService.deleteEmployeeById(id)).thenThrow(new RuntimeException("Employee not found"));

        assertThrows(RuntimeException.class, () -> {
            employeeController.deleteEmployeeById(id);
        });

    }
}
