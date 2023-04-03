package com.app.admin;
import static org.junit.Assert.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import com.app.admin.employee.Employee;
import com.app.admin.employee.EmployeeController;
import com.app.admin.employee.EmployeeRepository;
import com.app.admin.employee.EmployeeService;
import com.app.task.Task;
import com.app.task.TaskStatus;

@ExtendWith(MockitoExtension.class)
public class EmployeeControllerTest {

    @Mock
    private JwtService jwtService;

    @Mock
    private EmployeeService employeeService;

    @InjectMocks
    private EmployeeController employeeController;
    
    @Mock
    private EmployeeRepository employeeRepo;
    
    @Mock
    private RestTemplate restTemplate;

    @Test
    public void testGetEmployeeById() {
        // Mock the dependencies
        String token = "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaG4gRG9lIiwiaWF0IjoxNTE2MjM5MDIyfQ.SflKxwRJSMeKKF2QT4fwpMeJf36POk6yJV_adQssw5c";
        String employeeEmail = "johndoe@example.com";
        Employee employee = new Employee();
        employee.setEmployeeId(1L);
        employee.setEmployeeFirstName("jimin");
        employee.setEmployeeEmail(employeeEmail);

        when(jwtService.extractUsername(token.substring(7))).thenReturn(employeeEmail);
        when(employeeService.getEmployee(employeeEmail)).thenReturn(employee);

        // Call the method and assert the result
        Employee result = employeeController.getEmployeeById(token);
        assertEquals(1L, result.getEmployeeId());
        assertEquals(employeeEmail, result.getEmployeeEmail());
        assertEquals("jimin", employee.getEmployeeFirstName());
    }
    
    
    @Test
    public void testAcceptTask() {

        // Create a mock employee
        Employee employee = new Employee();
        employee.setEmployeeId(1L);
        employee.setEmployeeEmail("employee@example.com");

        // Create a mock task
        Task task = new Task();
        task.setTaskId(1L);
        task.setStatus(TaskStatus.PENDING);
        task.setEmployeeId(1L);

        // Mock the JWT token and employee repository calls
        when(jwtService.extractUsername(any(String.class))).thenReturn("employee@example.com");
        when(employeeRepo.findByEmployeeEmail(any(String.class))).thenReturn(Optional.of(employee));

        // Mock the RestTemplate calls
        when(restTemplate.getForObject(any(String.class), eq(Task.class))).thenReturn(task);
        when(restTemplate.exchange(any(String.class), eq(HttpMethod.PUT), any(HttpEntity.class), eq(Task.class)))
                .thenReturn(new ResponseEntity<>(task, HttpStatus.OK));

        // Call the controller method
        ResponseEntity<Task> response = employeeController.acceptTask("Bearer token", 1L);

        // Check the response status code
        assertEquals(HttpStatus.OK, response.getStatusCode());

        // Check the response body
        Task updatedTask = response.getBody();
        assertNotNull(updatedTask);
        assertEquals(TaskStatus.ACCEPTED, updatedTask.getStatus());
    }
    
    
    

}
