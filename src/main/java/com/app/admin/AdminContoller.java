package com.app.admin;

import java.util.List;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.app.admin.employee.EmployeeRepository;
import com.app.admin.employee.EmployeeService;
import com.app.admin.manager.Manager;
import com.app.admin.manager.ManagerService;

@RestController
@RequestMapping("/admin")
@CrossOrigin(origins = "http://localhost:3000")
public class AdminContoller {
	
	@Autowired
	private JwtService jwtService;
	
	@Autowired
	private AdminRepository adminRepository;
	
	@Autowired
	private EmployeeService employeeService;
	
	@Autowired
	private EmployeeRepository employeeRepo;
	
	@GetMapping("/all")
	public Optional<Admin> getadmin(@RequestHeader("Authorization") String token) {
		String adminEmail = jwtService.extractUsername(token.substring(7));
		
		Optional<Admin> admin = adminRepository.findByAdminEmail(adminEmail);
		return admin;
	}
	
	@Autowired
	private ManagerService managerService;
	
	@GetMapping("/allmanager")
	public List<Manager> getAllManagers(){
		return managerService.getAllManagers();
	}
	
	@DeleteMapping("/deletemanager/{managerEmail}")
	public void deleteManager(@PathVariable String managerEmail) {
		managerService.deleteManager(managerEmail);
	}
	
	//admin
	@PostMapping("/manager")
	public ResponseEntity<Manager> addManager(@RequestBody Manager manager) {
		Manager savedManager = managerService.addManager(manager);
		return new ResponseEntity<>(savedManager, HttpStatus.CREATED);
	}
	

	@GetMapping("/allemployee")
    public List<Employee> getAllEmployees() {
        return employeeService.getAllEmployees();
    }
	
	@GetMapping("/department/{departmentId}")
	public List<Employee> getEmployeesByDeptid(@PathVariable Long departmentId){
		return employeeRepo.findByDepartmentId(departmentId);
	}
	
	@DeleteMapping("/deleteemployee/{employeeEmail}")
	public void deleteEmployee(@PathVariable String employeeEmail) {
		employeeService.deleteEmployee(employeeEmail);
	}
	
	@PostMapping("/addemployee")
	public ResponseEntity<Employee> addEmployee(@RequestBody Employee employee) {
		Employee savedEmployee = employeeService.addEmployee(employee);
		return new ResponseEntity<>(savedEmployee, HttpStatus.CREATED);
	}
}
