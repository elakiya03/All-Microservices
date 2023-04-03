package com.app.admin.manager;

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
import com.app.admin.employee.EmployeeRepository;
import com.app.leave.Leave;
import com.app.leave.LeaveStatus;
import com.app.task.Task;
import com.app.task.TaskAssign;
import com.app.task.TaskDetail;
import com.app.task.TaskStatus;

@RestController
@RequestMapping("/managers")
@CrossOrigin("http://localhost:3000")
public class ManagerController {
	
	@Autowired
	private RestTemplate restTemplate;
	
	@Autowired
	private ManagerService managerService;
	
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
	 * manager
	 */
	
	@GetMapping("/employeesformanager")
	public List<Employee> getEmployees(@RequestHeader("Authorization") String token){
		String managerEmail = jwtService.extractUsername(token.substring(7));
		Optional<Manager> manager = managerRepo.findByManagerEmail(managerEmail);
		Manager managerr = manager.get();
		Long departmentId = managerr.getDepartmentId();
		List<Employee> employee = employeeRepo.findByDepartmentId(departmentId);
		return employee;
	}
	
	@GetMapping("/view")
    public Manager getManagerById(@RequestHeader("Authorization") String token) {
        String managerEmail = jwtService.extractUsername(token.substring(7));
		return managerService.getManagerById(managerEmail);
    }
	
	@PatchMapping("/update")
	public Manager updateManager(@RequestHeader("Authorization") String token,
			@RequestBody Manager managerDetails) {
		        String managerEmail = jwtService.extractUsername(token.substring(7));

		return managerService.updateManager(managerEmail, managerDetails);
	}
	
	@PutMapping("/leaveapproval/{leaveId}")
	public ResponseEntity<Leave> leaveApproval(@RequestHeader("Authorization") String token, @PathVariable Long leaveId) {
        String managerEmail = jwtService.extractUsername(token.substring(7));

	    Leave leave = restTemplate.getForObject(leaveServiceurl+"/leaves/"+leaveId, Leave.class);
	    Optional<Manager> manager = managerRepo.findByManagerEmail(managerEmail);
	    Manager managerr = manager.get();

	    Long managerId= managerr.getManagerId();
	    if (leave.getManagerId() != managerId) {
	        return ResponseEntity.badRequest().build();
	    }

	    if(leave.getStatus().equals(LeaveStatus.PENDING)) {
	        leave.setStatus(LeaveStatus.APPROVED); 
	        restTemplate.exchange(
	                leaveServiceurl+"/leaves/"+leaveId, HttpMethod.PUT, new HttpEntity<>(leave), Leave.class);
	    }
	    Leave updatedLeave = restTemplate.getForObject(leaveServiceurl+"/leaves/"+leaveId, Leave.class);
	    return ResponseEntity.ok(updatedLeave);
	}
	
	@PutMapping("/leavedenial/{leaveId}")
	public ResponseEntity<Leave> leaveDenial(@RequestHeader("Authorization") String token, @PathVariable Long leaveId) {
        String managerEmail = jwtService.extractUsername(token.substring(7));

	    Leave leave = restTemplate.getForObject(leaveServiceurl+"/leaves/"+leaveId, Leave.class);
	    Optional<Manager> manager = managerRepo.findByManagerEmail(managerEmail);
	    Manager managerr = manager.get();

	    Long managerId= managerr.getManagerId();
	    if (leave.getManagerId() != managerId) {
	        return ResponseEntity.badRequest().build();
	    }

	    if(leave.getStatus().equals(LeaveStatus.PENDING)) {
	        leave.setStatus(LeaveStatus.DENIED); 
	        restTemplate.exchange(
	                leaveServiceurl+"/leaves/"+leaveId, HttpMethod.PUT, new HttpEntity<>(leave), Leave.class);
	    }
	    Leave updatedLeave = restTemplate.getForObject(leaveServiceurl+"/leaves/"+leaveId, Leave.class);
	    return ResponseEntity.ok(updatedLeave);
	}
	
	@GetMapping("/leaveformanager")
	public ResponseEntity<List<Leave>> getLeavesByEmployeeId(@RequestHeader("Authorization") String token) {
	    HttpHeaders headers = new HttpHeaders();
	    headers.setContentType(MediaType.APPLICATION_JSON);

	    HttpEntity<String> entity = new HttpEntity<>(headers);
        String managerEmail = jwtService.extractUsername(token.substring(7));

	    Optional<Manager> manager = managerRepo.findByManagerEmail(managerEmail);
	    Manager managerr = manager.get();

	    Long managerId= managerr.getManagerId();
	    
	    ResponseEntity<List<Leave>> response = restTemplate.exchange(
	            leaveServiceurl + "/leaves/manager/employeeleaves/" + managerId,
	            HttpMethod.GET,
	            entity,
	            new ParameterizedTypeReference<List<Leave>>() {});

	    return ResponseEntity.ok(response.getBody());
	}
	
	@PostMapping("/createtask")
	public ResponseEntity<TaskDetail> createTask(@RequestHeader("Authorization") String token, @RequestBody TaskDetail taskDetail){
        String managerEmail = jwtService.extractUsername(token.substring(7));
        Optional<Manager> managerr= managerRepo.findByManagerEmail(managerEmail);
		Manager manager = managerr.get();
		Long managerId = manager.getManagerId();
		
		Task task = new Task();
		task.setTaskId(taskDetail.getTaskId());
		task.setTaskTitle(taskDetail.getTaskTitle());
		task.setTaskDescription(taskDetail.getTaskDescription());
		task.setStartDate(taskDetail.getStartDate());
		task.setEndDate(taskDetail.getEndDate());
		task.setStatus(TaskStatus.UNASSIGNED);
		task.setManagerId(managerId);
		
		restTemplate.postForObject(taskServiceurl+"/tasks", task, Task.class);
		
		//taskDetails.setTaskId(task.getTaskId());
		taskDetail.setManagerId(managerId);
		TaskDetail savedTaskDetails = restTemplate.postForObject(taskServiceurl+"/taskDetails", taskDetail, TaskDetail.class);
		
		return ResponseEntity.ok(savedTaskDetails);
		
	}
	
	@PutMapping("/assigntask/{taskId}") 
	public ResponseEntity<TaskAssign> assignTask(@RequestHeader("Authorization") String token, @PathVariable Long taskId, @RequestBody TaskAssign taskAssign){
		
		Task task = restTemplate.getForObject(taskServiceurl+"/tasks/"+taskId, Task.class);
		
		if (task == null) {
	        return ResponseEntity.notFound().build();
	    }
		
		String managerEmail = jwtService.extractUsername(token.substring(7));
        Optional<Manager> managerr= managerRepo.findByManagerEmail(managerEmail);
		Manager manager = managerr.get();
		Long managerId = manager.getManagerId();
		
		if(task.getManagerId() != managerId) {
			 return ResponseEntity.notFound().build();
		}		
		
		Long employeeId= taskAssign.getEmployeeId();
		Optional<Employee> employee = employeeRepo.findById(employeeId);
		Employee emp = employee.get();
		String employeeFirstName = emp.getEmployeeFirstName();
		String employeeLastName= emp.getEmployeeLastName();
		
		
		TaskAssign savedTaskAssign = new TaskAssign();
		savedTaskAssign.setEmployeeId(employeeId);
		savedTaskAssign.setManagerId(managerId);
		savedTaskAssign.setTask(task);
		
		restTemplate.postForObject(taskServiceurl+"/taskAssigns", savedTaskAssign, TaskAssign.class);
		
		if(task.getStatus().equals(TaskStatus.UNASSIGNED)) {
			task.setStatus(TaskStatus.PENDING);
			task.setEmployeeId(employeeId);
			task.setEmployeeName(employeeFirstName+" "+employeeLastName);

		}
		restTemplate.exchange(
                taskServiceurl+"/tasks/"+taskId, HttpMethod.PUT, new HttpEntity<>(task), Task.class);
		return ResponseEntity.ok(savedTaskAssign);
	}
	
	@GetMapping("/tasklist")
	public ResponseEntity<List<Task>> listTask(@RequestHeader("Authorization") String token){
		HttpHeaders headers = new HttpHeaders();
	    headers.setContentType(MediaType.APPLICATION_JSON);

	    HttpEntity<String> entity = new HttpEntity<>(headers);
		
		String managerEmail = jwtService.extractUsername(token.substring(7));
        Optional<Manager> managerr= managerRepo.findByManagerEmail(managerEmail);
		Manager manager = managerr.get();
		Long managerId = manager.getManagerId();
		
		ResponseEntity<List<Task>> response = restTemplate.exchange(
	            taskServiceurl + "/tasks/manager/" + managerId,
	            HttpMethod.GET,
	            entity,
	            new ParameterizedTypeReference<List<Task>>() {});
		return ResponseEntity.ok(response.getBody());
	}
	
	@DeleteMapping("/deletetask/{taskId}")
	public void deletetask(@RequestHeader("Authorization") String token, @PathVariable Long taskId) {
	    Task task = restTemplate.getForObject(taskServiceurl+"/tasks/"+taskId, Task.class);
	   
	    if(task.getStatus().equals(TaskStatus.UNASSIGNED)) {
	    	restTemplate.delete(taskServiceurl + "/tasks/" + taskId);
	    }
	    
	}

}
