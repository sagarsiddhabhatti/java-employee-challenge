package com.example.rqchallenge.controller;

import com.example.rqchallenge.model.Employee;
import com.example.rqchallenge.service.EmployeeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.Pattern;
import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/employees")
@Slf4j
public class EmployeeController implements IEmployeeController {
    @Autowired
    private final EmployeeService employeeService;

    /**
     * all employees whose name contains or matches the string input provided
     *
     * @return list of employees
     */
    @GetMapping
    public ResponseEntity<List<Employee>> getAllEmployees() {
        log.debug("Request to fetch all employees received");
        List<Employee> employees = employeeService.getAllEmployees();
        log.info("Successfully retrieved {} employees", employees.size());
        return ResponseEntity.ok(employees);
    }

    /**
     * all employees whose name contains or matches the string input provided
     *
     * @param searchString
     * @return list of employees
     */
    @GetMapping("/search/{searchString}")
    public ResponseEntity<List<Employee>> getEmployeesByNameSearch(@PathVariable String searchString) {
        log.debug("Search request for employees with name containing: {}", searchString);
        List<Employee> employees = employeeService.getEmployeesByNameSearch(searchString);
        log.info("Found {} employees matching search criteria", employees.size());
        return ResponseEntity.ok(employees);
    }

    /**
     * return a single employee
     *
     * @param id
     * @return employee
     */
    @GetMapping("/{id}")
    public ResponseEntity<Employee> getEmployeeById(
            @PathVariable @Pattern(regexp = "\\d+", message = "Invalid employee ID format") String id) {
        log.debug("Request to fetch employee with ID: {}", id);
        Employee employee = employeeService.getEmployeeById(id);
        log.info("Successfully retrieved employee: {}", employee);
        return ResponseEntity.ok(employee);
    }

    /**
     * a single integer indicating the highest salary of all employees
     *
     * @return integer of the highest salary
     */
    @GetMapping("/highestSalary")
    public ResponseEntity<Integer> getHighestSalaryOfEmployees() {
        log.debug("Request to fetch highest salary among employees");
        int highestSalary = employeeService.getHighestSalaryOfEmployees();
        log.info("Successfully retrieved highest salary: {}", highestSalary);
        return ResponseEntity.ok(highestSalary);
    }

    /**
     * a list of the top 10 employees based off of their salaries
     *
     * @return list of employees
     */
    @GetMapping("/topTenHighestEarningEmployeeNames")
    public ResponseEntity<List<String>> getTopTenHighestEarningEmployeeNames() {
        log.debug("Request to fetch top ten highest earning employee names");
        List<String> topEmployees = employeeService.getTopTenHighestEarningEmployeeNames();
        log.info("Successfully retrieved top ten highest earning employees: {}", topEmployees);
        return ResponseEntity.ok(topEmployees);
    }

    /**
     * a status of success or failed based on if an employee was created
     *
     * @param employeeInput
     * @return string of the status (i.e. success)
     */
    @PostMapping
    public ResponseEntity<String> createEmployee(@RequestBody Map<String, Object> employeeInput) {
        log.debug("Request to create new employee with data: {}", employeeInput);
        Employee employee = new Employee();
        employee.setEmployeeName((String) employeeInput.get("name"));
        employee.setEmployeeSalary((String) employeeInput.get("salary"));
        employee.setEmployeeAge((String) employeeInput.get("age"));

        String response = employeeService.createEmployee(employee);
        log.info("Successfully created employee: {}", employee.getEmployeeName());
        return ResponseEntity.ok(response);
    }

    /**
     * deletes the employee with specified id given
     *
     * @param id
     * @return the name of the employee that was deleted
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteEmployeeById(@PathVariable String id) {
        log.debug("Request to delete employee with ID: {}", id);
        String employeeName = employeeService.deleteEmployeeById(id);
        log.info("Successfully deleted employee: {}", employeeName);
        return ResponseEntity.ok(employeeName);
    }
}
