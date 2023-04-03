package com.app.admin.employee;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import com.app.admin.Employee;
import com.app.admin.JwtService;
import com.app.admin.manager.Manager;
import com.app.admin.manager.ManagerRepo;
import com.app.leave.Leave;
import com.app.leave.LeaveRequest;
import com.app.leave.LeaveStatus;
import com.app.task.Task;
import com.app.task.TaskStatus;


@RestController
@RequestMapping("/employees")
@CrossOrigin("http://localhost:3000")
public class EmployeeController {
	
	@Autowired
	private RestTemplate restTemplate;
	
	@Autowired
	private EmployeeService employeeService;
	
	@Autowired
	private EmployeeRepository employeeRepo;
	
	@Autowired
	private JwtService jwtService;
	
	@Autowired 
	private ManagerRepo managerRepo;
	
	@Value("${leave.service}")
	private String leaveServiceurl;
	
	@Value("${task.service}")
	private String taskServiceurl;	
	
	/*
	 *For Employee
	 * 
	 */
	
	
	@GetMapping("/managerforemployee")
		public Manager getManager(@RequestHeader("Authorization") String token) {
			
			String employeeEmail = jwtService.extractUsername(token.substring(7));
			
			Optional<Employee> employee = employeeRepo.findByEmployeeEmail(employeeEmail);
			Employee employeee = employee.get();
			Long departmentId = employeee.getDepartmentId();
			List<Manager> manager = managerRepo.findByDepartmentId(departmentId);
			Manager m = manager.get(0);
			return m;
	}

	@GetMapping("/view")
    public Employee getEmployeeById(@RequestHeader("Authorization") String token) {
		String employeeEmail = jwtService.extractUsername(token.substring(7));
		return employeeService.getEmployee(employeeEmail);
    }
	

	@PatchMapping("/update")
	public Employee updateEmployee(@RequestHeader("Authorization") String token,
	                                                @RequestBody Employee employeeDetails) {
	    String employeeEmail = jwtService.extractUsername(token.substring(7));
	    return employeeService.updateEmployee(employeeEmail, employeeDetails);
	    
	}
	
	@PostMapping("/leaverequest")
	public ResponseEntity<LeaveRequest> applyLeave(@RequestHeader("Authorization") String token, @RequestBody LeaveRequest leaveRequest){
		
		String employeeEmail = jwtService.extractUsername(token.substring(7));
		Optional<Employee> employeee = employeeRepo.findByEmployeeEmail(employeeEmail);
		Employee employee = employeee.get();
		Long departmentId = employee.getDepartmentId();
		String employeeFirstName = employee.getEmployeeFirstName();
		String employeeLastName = employee.getEmployeeLastName();
		
		List<Manager> managers = managerRepo.findByDepartmentId(departmentId);
		Manager manager = managers.get(0);
		String reason = leaveRequest.getReason();
		
		Leave leave = new Leave();
		leave.setStartDate(leaveRequest.getStartDate());
		leave.setEndDate(leaveRequest.getEndDate());
		leave.setStatus(LeaveStatus.PENDING);
		leave.setEmployeeId(employee.getEmployeeId());
		leave.setDepartmentId(departmentId);
		leave.setManagerId(manager.getDepartmentId());
		leave.setLeaveReason(reason);
		leave.setEmployeeName(employeeFirstName+" "+employeeLastName);
		
		restTemplate.postForObject(leaveServiceurl+"/leaves",leave, Leave.class);
		    
		leaveRequest.setEmployeeId(employee.getEmployeeId());
//		leaveRequestRepo.save(leaveRequest);
		LeaveRequest savedLeaveRequest = restTemplate.postForObject(leaveServiceurl+ "/leaveRequests", leaveRequest, LeaveRequest.class);
		
		return ResponseEntity.ok(savedLeaveRequest);   
					    
	}
	
	@GetMapping("/leaveforemployee")
	public ResponseEntity<List<Leave>> getLeavesByEmployeeId(@RequestHeader("Authorization") String token) {
		
		String employeeEmail = jwtService.extractUsername(token.substring(7));
		HttpHeaders headers = new HttpHeaders();
	    headers.setContentType(MediaType.APPLICATION_JSON);

	    HttpEntity<String> entity = new HttpEntity<>(headers);
	    
	    Optional<Employee> employeee = employeeRepo.findByEmployeeEmail(employeeEmail);
		Employee employee = employeee.get();
		Long employeeId = employee.getEmployeeId();
	    ResponseEntity<List<Leave>> response = restTemplate.exchange(
	            leaveServiceurl + "/leaves/employee/leave/" + employeeId,
	            HttpMethod.GET,
	            entity,
	            new ParameterizedTypeReference<List<Leave>>() {});

	    return ResponseEntity.ok(response.getBody());
	}
	
	@GetMapping("/taskforemployee")
	public ResponseEntity<List<Task>> getTaskForEmployee(@RequestHeader("Authorization") String token){
		String employeeEmail = jwtService.extractUsername(token.substring(7));
		HttpHeaders headers = new HttpHeaders();
	    headers.setContentType(MediaType.APPLICATION_JSON);

	    HttpEntity<String> entity = new HttpEntity<>(headers);
	    
	    Optional<Employee> employeee = employeeRepo.findByEmployeeEmail(employeeEmail);
		Employee employee = employeee.get();
		Long employeeId = employee.getEmployeeId();
	    ResponseEntity<List<Task>> response = restTemplate.exchange(
	            taskServiceurl + "/tasks/employee/" + employeeId,
	            HttpMethod.GET,
	            entity,
	            new ParameterizedTypeReference<List<Task>>() {});

	    return ResponseEntity.ok(response.getBody());
	}

	@PutMapping("/accepttask/{taskId}")
	public ResponseEntity<Task> acceptTask(@RequestHeader("Authorization") String token,@PathVariable Long taskId){
		
		String employeeEmail = jwtService.extractUsername(token.substring(7));
		Optional<Employee> employeee = employeeRepo.findByEmployeeEmail(employeeEmail);
		Employee employee = employeee.get();
		Long employeeId = employee.getEmployeeId();
		Task task = restTemplate.getForObject(taskServiceurl+"/tasks/"+taskId, Task.class);
	    if(task.getEmployeeId().equals(employeeId) && task.getStatus().equals(TaskStatus.PENDING)) {
			task.setStatus(TaskStatus.ACCEPTED);	    
			restTemplate.exchange(
                taskServiceurl+"/tasks/"+taskId, HttpMethod.PUT, new HttpEntity<>(task), Task.class);
	    }
	    
	    return ResponseEntity.ok(task);
	}
	
	@PutMapping("/starttask/{taskId}")
	public ResponseEntity<Task> startTask(@RequestHeader("Authorization") String token,@PathVariable Long taskId){
		
		String employeeEmail = jwtService.extractUsername(token.substring(7));
		Optional<Employee> employeee = employeeRepo.findByEmployeeEmail(employeeEmail);
		Employee employee = employeee.get();
		Long employeeId = employee.getEmployeeId();
		Task task = restTemplate.getForObject(taskServiceurl+"/tasks/"+taskId, Task.class);
	    if(task.getEmployeeId().equals(employeeId) && task.getStatus().equals(TaskStatus.ACCEPTED)) {
			task.setStatus(TaskStatus.STARTED);	    
			restTemplate.exchange(
                taskServiceurl+"/tasks/"+taskId, HttpMethod.PUT, new HttpEntity<>(task), Task.class);
	    }
	    
	    return ResponseEntity.ok(task);
	}
	
	@PutMapping("/completetask/{taskId}")
	public ResponseEntity<Task> completeTask(@RequestHeader("Authorization") String token,@PathVariable Long taskId){
		
		String employeeEmail = jwtService.extractUsername(token.substring(7));
		Optional<Employee> employeee = employeeRepo.findByEmployeeEmail(employeeEmail);
		Employee employee = employeee.get();
		Long employeeId = employee.getEmployeeId();
		Task task = restTemplate.getForObject(taskServiceurl+"/tasks/"+taskId, Task.class);
	    if(task.getEmployeeId().equals(employeeId) && task.getStatus().equals(TaskStatus.STARTED)) {
			task.setStatus(TaskStatus.COMPLETED);	    
			restTemplate.exchange(
                taskServiceurl+"/tasks/"+taskId, HttpMethod.PUT, new HttpEntity<>(task), Task.class);
	    }
	    
	    return ResponseEntity.ok(task);
	}
	
	@DeleteMapping("/deleteleave/{leaveId}")
	public void deleteLeave(@RequestHeader("Authorization") String token,@PathVariable Long leaveId) {
	    Leave leave = restTemplate.getForObject(leaveServiceurl+"/leaves/"+leaveId, Leave.class);
	    if(leave.getStatus().equals(LeaveStatus.PENDING)) {
	    restTemplate.delete(leaveServiceurl + "/leaves/" + leaveId);
	    }
	}
}
