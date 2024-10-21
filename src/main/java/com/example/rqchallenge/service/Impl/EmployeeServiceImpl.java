package com.example.rqchallenge.service.Impl;

import com.example.rqchallenge.constants.AppConstants;
import com.example.rqchallenge.exceptions.EmployeeNotFoundException;
import com.example.rqchallenge.exceptions.EmployeeServiceException;
import com.example.rqchallenge.model.Employee;
import com.example.rqchallenge.model.EmployeeApiResponse;
import com.example.rqchallenge.model.EmployeeListResponse;
import com.example.rqchallenge.service.EmployeeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientException;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Mono;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class EmployeeServiceImpl implements EmployeeService {

    @Autowired
    private final WebClient webClient;

    // Common method for retrieving and checking response status
    private <T> T processApiResponse(Mono<T> responseMono, String errorMessage) {
        try {
            return responseMono.block();
        } catch (WebClientResponseException e) {
            log.error("API response error: {} - {}", e.getStatusCode(), e.getResponseBodyAsString());
            throw new EmployeeServiceException(errorMessage, e);
        } catch (WebClientException e) {
            log.error("WebClient error: {}", e.getMessage(), e);
            throw new EmployeeServiceException("Service is unavailable", e);
        } catch (Exception e) {
            log.error("Unexpected error: {}", e.getMessage(), e);
            throw new EmployeeServiceException(errorMessage, e);
        }
    }

    @Override
    public List<Employee> getAllEmployees() {
        log.debug("Fetching all employees");
        String url = UriComponentsBuilder.fromUriString(AppConstants.GET_ALL_EMPLOYEES).toUriString();

        Mono<EmployeeListResponse> responseMono = webClient.get()
                .uri(url)
                .retrieve()
                .bodyToMono(EmployeeListResponse.class)
                .flatMap(response -> {
                    if ("success".equals(response.getStatus()) && response.getData() != null) {
                        log.info("Successfully retrieved {} employees.", response.getData().size());
                        return Mono.just(response);
                    } else {
                        log.warn("No employees found or invalid status.");
                        return Mono.just(new EmployeeListResponse("failure", Collections.emptyList()));
                    }
                })
                .onErrorResume(throwable -> {
                    log.error("Error retrieving employees: {}", throwable.getMessage());
                    return Mono.just(new EmployeeListResponse("failure", Collections.emptyList()));
                });

        EmployeeListResponse response = processApiResponse(responseMono, "Failed to fetch employees.");

        return response.getData();
    }

    @Override
    public Employee getEmployeeById(String id) {
        log.debug("Fetching employee with ID: {}", id);
        String url = UriComponentsBuilder.fromUriString(AppConstants.GET_EMPLOYEE_BY_ID)
                .buildAndExpand(id)
                .toUriString();

        try {
            // Call the external API and parse the response
            Mono<EmployeeApiResponse> responseMono = webClient.get()
                    .uri(url)
                    .retrieve()
                    .onStatus(
                            HttpStatus::is4xxClientError,
                            clientResponse -> {
                                log.error("Client error while fetching employee with id: {}", id);
                                return Mono.error(new EmployeeNotFoundException("Employee with id " + id + " not found."));
                            }
                    )
                    .onStatus(
                            HttpStatus::is5xxServerError,
                            clientResponse -> {
                                log.error("Server error while fetching employee with id: {}", id);
                                return Mono.error(new EmployeeServiceException("Server error occurred while fetching employee details."));
                            }
                    )
                    .bodyToMono(EmployeeApiResponse.class)
                    .flatMap(response -> {
                        if ("success".equals(response.getStatus()) && response.getData() != null) {
                            log.info("Successfully retrieved employee: {}", response.getData().getEmployeeName());
                            return Mono.just(response);  // Return the full response here
                        } else {
                            log.error("Failed to retrieve employee: Status: {}, Data: null", response.getStatus());
                            return Mono.error(new EmployeeNotFoundException("Failed to retrieve employee with id " + id));
                        }
                    });

            // Handle the API response and extract the employee data
            EmployeeApiResponse response = processApiResponse(responseMono, "Failed to fetch employee with ID: " + id);

            // Return the actual employee object from the response
            return response.getData();

        } catch (WebClientResponseException e) {
            log.error("Error while calling employee service: {}: {}", e.getStatusCode(), e.getResponseBodyAsString(), e);
            throw new EmployeeServiceException("Error calling employee service for id: " + id, e);
        } catch (Exception e) {
            log.error("Unexpected error while fetching employee with id: {}", id, e);
            throw new EmployeeServiceException("Unexpected error while fetching employee with id: " + id, e);
        }
    }

    @Override
    public String createEmployee(Employee employeeInput) {
        try {
            // Use ParameterizedTypeReference to correctly specify the response type as Map<String, Object>
            Map<String, Object> response = webClient.post()
                    .uri(AppConstants.CREATE_EMPLOYEE)
                    .bodyValue(employeeInput)
                    .retrieve()
                    .bodyToMono(new ParameterizedTypeReference<Map<String, Object>>() {
                    })  // Specify the correct Map type
                    .block();

            // Extract the "status" from the response map
            String status = (String) response.get("status");

            if (status == null || status.isEmpty()) {
                throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Invalid response from server");
            }

            return status;

        } catch (WebClientResponseException e) {
            // This handles 4xx and 5xx HTTP status codes
            log.error("Error response from API: {} - {}", e.getStatusCode(), e.getResponseBodyAsString(), e);
            throw new ResponseStatusException(e.getStatusCode(), "Error while creating employee: " + e.getMessage(), e);

        } catch (WebClientException e) {
            // This handles lower-level network or client errors
            log.error("WebClient error: {}", e.getMessage(), e);
            throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE, "Unable to connect to the employee service", e);

        } catch (Exception e) {
            // Catch any other exceptions (like JSON parsing or null pointer issues)
            log.error("Unexpected error occurred while creating employee: {}", e.getMessage(), e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "An unexpected error occurred", e);
        }
    }

    @Override
    public String deleteEmployeeById(String id) {
        log.debug("Deleting employee with ID: {}", id);
        Employee employee = getEmployeeById(id);

        String url = UriComponentsBuilder.fromUriString(AppConstants.DELETE_EMPLOYEE)
                .buildAndExpand(id)
                .toUriString();

        Mono<Void> responseMono = webClient.delete()
                .uri(url)
                .retrieve()
                .bodyToMono(Void.class);

        processApiResponse(responseMono, "Failed to delete employee with id: " + id);
        log.info("Successfully deleted employee with id: {}", id);
        return employee.getEmployeeName();
    }

    @Override
    public List<Employee> getEmployeesByNameSearch(String searchString) {
        log.debug("Searching employees with name containing: {}", searchString);
        List<Employee> allEmployees = getAllEmployees();
        return allEmployees.stream()
                .filter(employee -> Optional.ofNullable(employee.getEmployeeName())
                        .orElse("")
                        .toLowerCase()
                        .contains(searchString.toLowerCase()))
                .collect(Collectors.toList());
    }

    @Override
    public int getHighestSalaryOfEmployees() {
        log.debug("Fetching highest salary among employees");
        List<Employee> allEmployees = getAllEmployees();
        return allEmployees.stream()
                .mapToInt(emp -> Integer.parseInt(emp.getEmployeeSalary()))
                .max()
                .orElse(0);
    }

    @Override
    public List<String> getTopTenHighestEarningEmployeeNames() {
        log.debug("Fetching top 10 highest-earning employees");
        List<Employee> allEmployees = getAllEmployees();
        return allEmployees.stream()
                .sorted((e1, e2) -> Integer.compare(
                        Integer.parseInt(e2.getEmployeeSalary()),
                        Integer.parseInt(e1.getEmployeeSalary()))
                )
                .limit(10)
                .map(Employee::getEmployeeName)
                .collect(Collectors.toList());
    }
}
