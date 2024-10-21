package com.example.rqchallenge.service;

import com.example.rqchallenge.constants.TestConstants;
import com.example.rqchallenge.model.Employee;
import com.example.rqchallenge.service.Impl.EmployeeServiceImpl;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.util.ResourceUtils;
import org.springframework.web.reactive.function.client.WebClient;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;


@SpringBootTest
@Slf4j
@TestPropertySource(properties = {
        "api.base-url=https://dummy.test.restapiexample.com"
})

class EmployeeServiceImplTest {

    private EmployeeServiceImpl employeeService;
    private MockWebServer mockWebServer;

    @BeforeEach
    void setUp() throws IOException {
        // Initialize MockWebServer
        mockWebServer = new MockWebServer();
        mockWebServer.start();
        String baseUrl = mockWebServer.url("test/").toString();
        WebClient webClient = WebClient.builder().baseUrl(baseUrl).build();
        employeeService = new EmployeeServiceImpl(webClient);

    }

    @AfterEach
    void tearDown() throws Exception {
        mockWebServer.shutdown();
    }

    @Test
    void testGetAllEmployees() throws Exception {
           String jsonResponse = getMockResonceData("employee_list.json");

        // Enqueue the mock response
        mockWebServer.enqueue(new MockResponse()
                .setResponseCode(200)
                .setHeader(TestConstants.CONTENT_TYPE, TestConstants.APPLICATION_JSON)
                .setBody(jsonResponse));

        // Call the method under test
        List<Employee> employees = employeeService.getAllEmployees();

        // Verify the results
        assertNotNull(employees);
        assertEquals(50, employees.size());
        assertEquals("Tiger Nixon", employees.get(0).getEmployeeName());
        assertEquals("Garrett Winters", employees.get(1).getEmployeeName());
    }

    @Test
    void testGetEmployeeById() throws Exception {


        String jsonResponse = getMockResonceData("employee_data_by_id_response.json");

        // Enqueue the mock response
        mockWebServer.enqueue(new MockResponse()
                .setResponseCode(200)
                .setHeader(TestConstants.CONTENT_TYPE, TestConstants.APPLICATION_JSON)
                .setBody(jsonResponse));

        // Call the method under test
        Employee result = employeeService.getEmployeeById("1");

        // Verify the results
        assertNotNull(result);
        assertEquals("Foo Bar", result.getEmployeeName());
    }

    @Test
    void testCreateEmployee() throws Exception {
        // Prepare mock response data
        String name = "test";
        String salary = "123";
        String age = "23";

        // Create JSON response for created employee
        String jsonResponse = getMockResonceData("create_employee.json");

        // Enqueue the mock response
        mockWebServer.enqueue(new MockResponse()
                .setResponseCode(200)
                .setHeader(TestConstants.CONTENT_TYPE, TestConstants.APPLICATION_JSON)
                .setBody(jsonResponse));

        // Call the method under test (assuming you have a create method in your service)

        Employee employee = new Employee();
        employee.setEmployeeName(name);
        employee.setEmployeeSalary(salary);
        employee.setEmployeeAge(age);
        String createdEmployee = employeeService.createEmployee(employee);

        // Verify the results
        assertNotNull(createdEmployee);
        assertEquals("success", createdEmployee);
    }

    @Test
    void testDeleteEmployee() throws Exception {
        // Prepare mock response data
        String id = "1";
        String deleteResponse = getMockResonceData("delete_response.json");
//"{\"status\":\"success\",\"message\":\"successfully! deleted Record\"}";
        String getEmployeeDataResponce = getMockResonceData("employee_data_by_id_response.json");;
        mockWebServer.enqueue(new MockResponse()
                .setResponseCode(200)
                .setHeader(TestConstants.CONTENT_TYPE, TestConstants.APPLICATION_JSON)
                .setBody(getEmployeeDataResponce));
        mockWebServer.enqueue(new MockResponse()
                .setResponseCode(200)
                .setHeader(TestConstants.CONTENT_TYPE, TestConstants.APPLICATION_JSON)
                .setBody(deleteResponse));

        // Call the method under test (assuming you have a delete method in your service)
        String deleteMessage = employeeService.deleteEmployeeById(id);

        // Verify the results
        assertNotNull(deleteMessage);
        assertEquals("Foo Bar", deleteMessage);
    }

    @NotNull
    private String getMockResonceData(String resourceLocation) throws IOException {
        return new String(Files.readAllBytes(Paths.get(ResourceUtils.getFile("classpath:"+resourceLocation).toURI())));
    }

    // Add more test cases for other methods...
}
