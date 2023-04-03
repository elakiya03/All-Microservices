package com.app.admin.department;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DepartmentService {
    
	
	@Autowired
    private DepartmentRepository departmentRepo;
	
	public List<Department> getAllDepartment(){
		return departmentRepo.findAll();
	}
	
	public Department getDepartmentById(Long departmentId) {
		return departmentRepo.findById(departmentId)
				.orElse(null);
	}
	
	public Department addDepartment(Department department) {
		var dept = Department.builder()
				.departmentName(department.getDepartmentName())
				.build();
		
		return departmentRepo.save(dept);
	}
	public void deleteDepartment(Long departmentId) {
		departmentRepo.deleteById(departmentId);
	}
	
	public Department updateDepartment(Long departmentId, Department departmentDetails) {
		Department department= departmentRepo.findById(departmentId)
				.orElseThrow(() -> new DepartmentNotFoundException(departmentId));
		
		department.setDepartmentName(departmentDetails.getDepartmentName());
		
		Department updatedDepartment= departmentRepo.save(department);
		return updatedDepartment;
	}
    

}

