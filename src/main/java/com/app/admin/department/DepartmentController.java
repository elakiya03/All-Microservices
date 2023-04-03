package com.app.admin.department;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;


@RestController
@RequestMapping("/departments")
@CrossOrigin("http://localhost:3000")
public class DepartmentController {

	@Autowired
    private DepartmentService departmentService;
	
	//admin
	@GetMapping("/")
	public List<Department> getAllDepartments(){
		return departmentService.getAllDepartment();
	}
    
	//admin
	@GetMapping("/department/{departmentId}")
	public Department getDepartmentById(@PathVariable Long departmentId) {
		return departmentService.getDepartmentById(departmentId);
	}
	
	//admin
	@DeleteMapping("/department/{departmentId}")
	public void deleteEmployee(@PathVariable Long departmentId) {
		departmentService.deleteDepartment(departmentId);
	}
	
	//admin
	@PostMapping("/department")
	public ResponseEntity<Department> addDepartment(@RequestBody Department department){
		Department savedDepartment = departmentService.addDepartment(department);
		return new ResponseEntity<>(savedDepartment, HttpStatus.CREATED);
	}
	
	//admin
	@PutMapping("/department/{departmentId}")
	public Department updateDepartment(@PathVariable Long departmentId,@RequestBody Department departmentDetails) {
		return departmentService.updateDepartment(departmentId, departmentDetails);
	}
}

