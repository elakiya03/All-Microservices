package com.app.admin.employee;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

public interface EmployeeRepository extends JpaRepository<Employee, Long>{
	List<Employee> findByDepartmentId(Long departmentId); 
	
	Optional<Employee> findByEmployeeEmail(String employeeEmail);
	
	
}
