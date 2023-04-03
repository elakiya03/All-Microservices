package com.app.admin;
import static org.junit.Assert.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.List;
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

import com.app.admin.employee.EmployeeController;
import com.app.admin.employee.EmployeeRepository;
import com.app.admin.manager.Manager;
import com.app.admin.manager.ManagerController;
import com.app.admin.manager.ManagerRepo;
import com.app.leave.Leave;
import com.app.leave.LeaveRequest;
import com.app.leave.LeaveStatus;
import com.app.task.Task;
import com.app.task.TaskAssign;
import com.app.task.TaskDetail;
import com.app.task.TaskStatus;



@ExtendWith(MockitoExtension.class)
public class ApplyLeaveTest {
    
    @Mock
    private EmployeeRepository employeeRepo;
    
    @Mock
    private ManagerRepo managerRepo;
    
    @Mock
    private JwtService jwtService;
    
    @Mock
    private RestTemplate restTemplate;
    
    @InjectMocks
    private EmployeeController employeeeController;
    
    @InjectMocks
    private ManagerController managerController;
    
    
//    
//    @BeforeEach
//    public void initMocks() throws Exception {
//        MockitoAnnotations.openMocks(this);
//    }
    
    @Test
    public void testApplyLeave()  {
        
        // Create a mock employee
        Employee employee = new Employee();
        employee.setEmployeeEmail("john@example.com");
        employee.setDepartmentId(1L);
        employee.setEmployeeFirstName("John");
        employee.setEmployeeLastName("Doe");
        employee.setEmployeeId(1L);
        
        // Create a mock manager
        Manager manager = new Manager();
        manager.setManagerEmail("manager@example.com");
        manager.setDepartmentId(1L);
        manager.setManagerId(1L);
        
        // Create a mock leave request
        LeaveRequest leaveRequest = new LeaveRequest();
        leaveRequest.setStartDate(LocalDate.parse("2023-03-26"));
        leaveRequest.setEndDate(LocalDate.parse("2023-03-30"));
        leaveRequest.setReason("Vacation");
        
        // Mock the repository calls
        when(jwtService.extractUsername(any(String.class))).thenReturn("john@example.com");
        when(employeeRepo.findByEmployeeEmail(any(String.class))).thenReturn(Optional.of(employee));
        when(managerRepo.findByDepartmentId(any(Long.class))).thenReturn(List.of(manager));
        when(restTemplate.postForObject(any(String.class), any(Leave.class), eq(Leave.class))).thenReturn(new Leave());
        when(restTemplate.postForObject(any(String.class), any(LeaveRequest.class), eq(LeaveRequest.class))).thenReturn(leaveRequest);

        // Call the controller method
        ResponseEntity<LeaveRequest> response = employeeeController.applyLeave("Bearer token", leaveRequest);
        
        // Check the response status code
        assertEquals(HttpStatus.OK, response.getStatusCode());
        
        // Check the response body
        LeaveRequest savedLeaveRequest = response.getBody();
        assertNotNull(savedLeaveRequest);
        assertEquals(leaveRequest.getStartDate(), savedLeaveRequest.getStartDate());
        assertEquals(leaveRequest.getEndDate(), savedLeaveRequest.getEndDate());
        assertEquals(leaveRequest.getReason(), savedLeaveRequest.getReason());
    }
    
    
    @Test
    public void testLeaveDenial() {

        // Create a mock manager
        Manager manager = new Manager();
        manager.setManagerEmail("manager@example.com");
        manager.setDepartmentId(1L);
        manager.setManagerId(1L);

        // Create a mock leave
        Leave leave = new Leave();
        leave.setLeaveId(1L);
        leave.setManagerId(1L);
        leave.setStatus(LeaveStatus.PENDING);

        // Mock the JWT token and manager repository calls
        when(jwtService.extractUsername(any(String.class))).thenReturn("manager@example.com");
        when(managerRepo.findByManagerEmail(any(String.class))).thenReturn(Optional.of(manager));

        // Mock the RestTemplate calls
        //when(restTemplate.getForObject(any(String.class), eq(Leave.class))).thenReturn(leave);
        when(restTemplate.exchange(any(String.class), eq(HttpMethod.PUT), any(HttpEntity.class), eq(Leave.class)))
                .thenReturn(new ResponseEntity<>(leave, HttpStatus.OK));
        when(restTemplate.getForObject(any(String.class), eq(Leave.class))).thenReturn(leave);

        // Call the controller method
        ResponseEntity<Leave> response = managerController.leaveDenial("Bearer token", 1L);

        // Check the response status code
        assertEquals(HttpStatus.OK, response.getStatusCode());

        // Check the response body
        Leave updatedLeave = response.getBody();
        assertNotNull(updatedLeave);
        assertEquals(LeaveStatus.DENIED, updatedLeave.getStatus());
    }
    
    @Test
    public void testCreateTask() {
        // Create a mock task detail
        TaskDetail taskDetail = new TaskDetail();
        taskDetail.setTaskId(1L);
        taskDetail.setTaskTitle("Task 1");
        taskDetail.setTaskDescription("Task 1 Description");
        taskDetail.setStartDate(LocalDate.of(2023, 3, 25));
        taskDetail.setEndDate(LocalDate.of(2023, 4, 1));

        // Create a mock manager
        Manager manager = new Manager();
        manager.setManagerEmail("manager@example.com");
        manager.setDepartmentId(1L);
        manager.setManagerId(1L);

        // Mock the JWT token and manager repository calls
        when(jwtService.extractUsername(any(String.class))).thenReturn("manager@example.com");
        when(managerRepo.findByManagerEmail(any(String.class))).thenReturn(Optional.of(manager));

        // Mock the RestTemplate calls
        when(restTemplate.postForObject(any(String.class), any(Task.class), eq(Task.class)))
                .thenReturn(new Task());
        when(restTemplate.postForObject(any(String.class), any(TaskDetail.class), eq(TaskDetail.class)))
                .thenReturn(taskDetail);

        // Call the controller method
        ResponseEntity<TaskDetail> response = managerController.createTask("Bearer token", taskDetail);

        // Check the response status code
        assertEquals(HttpStatus.OK, response.getStatusCode());

        // Check the response body
        TaskDetail savedTaskDetails = response.getBody();
        assertNotNull(savedTaskDetails);
        assertEquals(manager.getManagerId(), savedTaskDetails.getManagerId());
    }
    
    
    @Test
    public void testAssignTask() {

        // Create a mock manager
        Manager manager = new Manager();
        manager.setManagerEmail("manager@example.com");
        manager.setDepartmentId(1L);
        manager.setManagerId(1L);

        // Create a mock task
        Task task = new Task();
        task.setTaskId(1L);
        task.setManagerId(1L);
        task.setTaskTitle("Mock task");
        task.setTaskDescription("This is a mock task for testing purposes.");
        task.setStartDate(LocalDate.now());
        task.setEndDate(LocalDate.now().plusDays(1));
        task.setStatus(TaskStatus.UNASSIGNED);

        // Create a mock employee
        Employee employee = new Employee();
        employee.setEmployeeId(1L);
        employee.setEmployeeFirstName("John");
        employee.setEmployeeLastName("Doe");

        // Create a mock task assign
        TaskAssign taskAssign = new TaskAssign();
        taskAssign.setEmployeeId(1L);

        // Mock the JWT token and manager repository calls
        when(jwtService.extractUsername(any(String.class))).thenReturn("manager@example.com");
        when(managerRepo.findByManagerEmail(any(String.class))).thenReturn(Optional.of(manager));

        // Mock the RestTemplate calls
        when(restTemplate.getForObject(any(String.class), eq(Task.class))).thenReturn(task);
        when(restTemplate.postForObject(any(String.class), any(TaskAssign.class), eq(TaskAssign.class))).thenReturn(taskAssign);
        when(employeeRepo.findById(any(Long.class))).thenReturn(Optional.of(employee));
        when(restTemplate.exchange(any(String.class), eq(HttpMethod.PUT), any(HttpEntity.class), eq(Task.class)))
                .thenReturn(new ResponseEntity<>(task, HttpStatus.OK));

        // Call the controller method
        ResponseEntity<TaskAssign> response = managerController.assignTask("Bearer token", 1L, taskAssign);

        // Check the response status code
        assertEquals(HttpStatus.OK, response.getStatusCode());

        // Check the response body
        TaskAssign savedTaskAssign = response.getBody();
        assertNotNull(savedTaskAssign);
        assertEquals(taskAssign.getEmployeeId(), savedTaskAssign.getEmployeeId());
        assertEquals(task.getTaskId(), savedTaskAssign.getTask().getTaskId());
        assertEquals(manager.getManagerId(), savedTaskAssign.getManagerId());
        assertEquals(task.getEmployeeId(), savedTaskAssign.getTask().getEmployeeId());
        assertEquals(task.getEmployeeName(), employee.getEmployeeFirstName() + " " + employee.getEmployeeLastName());
        assertEquals(TaskStatus.PENDING, task.getStatus());
    }


}
