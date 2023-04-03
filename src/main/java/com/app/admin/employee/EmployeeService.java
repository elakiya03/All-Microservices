package com.app.admin.employee;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.app.admin.Admin;
import com.app.admin.AdminRepository;
import com.app.admin.Employee;
import com.app.admin.Role;
import com.app.admin.department.Department;
import com.app.admin.department.DepartmentRepository;

@Service
public class EmployeeService {
	
	
	@Autowired(required=true)
	private EmployeeRepository employeeRepo;
	
	@Autowired
	private PasswordEncoder passwordEncoder;
	
	@Autowired
	private AdminRepository adminRepo;
	
	@Autowired
	private DepartmentRepository deptRepo;
	
	public List<Employee> getAllEmployees() {
        return employeeRepo.findAll();
    }
	
	public Employee getEmployee(String employeeEmail) {
        return employeeRepo.findByEmployeeEmail(employeeEmail)
            .orElse(null);
    }
	
	public List<Employee> getEmployeesByDeptid(Long departmentId) {
		return employeeRepo.findByDepartmentId(departmentId);
	}
	
	public String getdeptname(Long departmentId) {
		Optional<Department> department = deptRepo.findById(departmentId);
		Department dept = department.get();
		return dept.getDepartmentName();
	}
	
	public Employee addEmployee(Employee employee) {
	    var employeee = Employee.builder()
	    		.employeeFirstName(employee.getEmployeeFirstName())
	    		.employeeLastName(employee.getEmployeeLastName())
	    		.employeeEmail(employee.getEmployeeEmail())
	    		.employeePassword(employee.getEmployeePassword())
	    		.employeePhone(employee.getEmployeePhone())
	    		.employeePosition(employee.getEmployeePosition())
	    		.employeeBirthAddress(employee.getEmployeeBirthAddress())
	    		.employeeCurrAddress(employee.getEmployeeCurrAddress())
	    		.employeeGender(employee.getEmployeeGender())
	    		.employeeDOB(employee.getEmployeeDOB())
	    		.departmentId(employee.getDepartmentId())
	    		.departmentName(getdeptname(employee.getDepartmentId()))
	    		.role(Role.EMPLOYEE)
	    		.build();
	    
	    var admin = Admin.builder()
				.adminEmail(employee.getEmployeeEmail())
				.adminPassword(passwordEncoder.encode(employee.getEmployeePassword()))
				.role(Role.EMPLOYEE)
				.build();
				
		adminRepo.save(admin);
	    return employeeRepo.save(employeee);
	}
	
	public void deleteEmployee(String employeeEmail) {
		Optional<Employee> employee = employeeRepo.findByEmployeeEmail(employeeEmail);
		Employee employeee = employee.get();
		Long employeeId= employeee.getEmployeeId();
	    employeeRepo.deleteById(employeeId);
	    
	    Optional<Admin> admin = adminRepo.findByAdminEmail(employeeEmail);
	    Admin adminn = admin.get();
	    Long adminId= adminn.getAdminId();
	    adminRepo.deleteById(adminId);
	}
	
	
	public Employee updateEmployee(String employeeEmail, Employee employeeDetails) {
	    
		Employee employee = employeeRepo.findByEmployeeEmail(employeeEmail)
	        .orElseThrow(() -> new EmployeeNotFoundException(employeeEmail));
	    
		Admin admin = adminRepo.findByAdminEmail(employeeEmail)
				.orElseThrow(() -> new EmployeeNotFoundException(employeeEmail));
		
	    if (employeeDetails.getEmployeePassword() != null) {
	        employee.setEmployeePassword(employeeDetails.getEmployeePassword());
	        admin.setAdminPassword(employeeDetails.getEmployeePassword());
	    }
	    if (employeeDetails.getEmployeeFirstName() != null) {
	        employee.setEmployeeFirstName(employeeDetails.getEmployeeFirstName());
	    }
	   
		if(employeeDetails.getEmployeeLastName() != null) {
			employee.setEmployeeLastName(employeeDetails.getEmployeeLastName());
		}
		
		if(employeeDetails.getEmployeeBirthAddress() != null) {
			employee.setEmployeeBirthAddress(employeeDetails.getEmployeeBirthAddress());
		}
		
		if(employeeDetails.getEmployeeCurrAddress() != null) {
			employee.setEmployeeCurrAddress(employeeDetails.getEmployeeCurrAddress());
		}
		if(employeeDetails.getEmployeeDOB() != null) {
			employee.setEmployeeDOB(employeeDetails.getEmployeeDOB());
		}
		if(employeeDetails.getEmployeePhone() != null) {
			employee.setEmployeePhone(employeeDetails.getEmployeePhone());
		}

	    Employee updatedEmployee = employeeRepo.save(employee);
	    return updatedEmployee;
	}

	
	
}
