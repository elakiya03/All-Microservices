package com.app.admin.manager;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.app.admin.Admin;
import com.app.admin.AdminRepository;
import com.app.admin.Role;
import com.app.admin.department.Department;
import com.app.admin.department.DepartmentRepository;
import com.app.admin.employee.EmployeeNotFoundException;

@Service
public class ManagerService {
	
	
	@Autowired
	private ManagerRepo managerRepo;
	
	@Autowired
	private AdminRepository adminRepo;
	
	@Autowired
	private PasswordEncoder passwordEncoder;
	
	@Autowired
	private DepartmentRepository deptRepo;
	
	public List<Manager> getAllManagers(){
		return managerRepo.findAll();
	}
	
	public Manager getManagerById(String managerEmail) {
        return managerRepo.findByManagerEmail(managerEmail)
            .orElse(null);
    }
	
	public String getdeptname(Long departmentId) {
		Optional<Department> department = deptRepo.findById(departmentId);
		Department dept = department.get();
		return dept.getDepartmentName();
	}
	
	public Manager addManager(Manager manager) {
		var managerr = Manager.builder()
				.managerFirstName(manager.getManagerFirstName())
				.managerLastName(manager.getManagerLastName())
				.managerPhone(manager.getManagerPhone())
				.managerPosition(manager.getManagerPosition())
				.managerBirthAdrr(manager.getManagerBirthAdrr())
				.managerCurrAdrr(manager.getManagerCurrAdrr())
				.managerEmail(manager.getManagerEmail())
				.managerGender(manager.getManagerGender())
				.managerDOB(manager.getManagerDOB())
				.managerPassword(manager.getManagerPassword())
				.departmentId(manager.getDepartmentId())
				.departmentName(getdeptname(manager.getDepartmentId()))
				.role(Role.MANAGER)
				.build();
		
		var admin = Admin.builder()
				.adminEmail(manager.getManagerEmail())
				.adminPassword(passwordEncoder.encode(manager.getManagerPassword()))
				.role(Role.MANAGER)
				.build();
				
		adminRepo.save(admin);
		return managerRepo.save(managerr);
	}
	
	public void deleteManager(String managerEmail) {
		Optional<Manager> manager = managerRepo.findByManagerEmail(managerEmail);
		Manager managerr= manager.get();
		Long managerId= managerr.getManagerId();
	    managerRepo.deleteById(managerId);
	    
	    Optional<Admin> admin = adminRepo.findByAdminEmail(managerEmail);
	    Admin adminn = admin.get();
	    Long adminId= adminn.getAdminId();
	    adminRepo.deleteById(adminId);
	    
	}    
	
	public Manager updateManager(String managerEmail, Manager managerDetails) {
        Manager manager = managerRepo.findByManagerEmail(managerEmail)
            .orElseThrow(() -> new ManagerNotFoundException(managerEmail));
        
        Admin admin = adminRepo.findByAdminEmail(managerEmail)
				.orElseThrow(() -> new EmployeeNotFoundException(managerEmail));
		
        if(managerDetails.getManagerFirstName() != null) {
        manager.setManagerFirstName(managerDetails.getManagerFirstName());
        }
        
        if(managerDetails.getManagerLastName() != null) {
            manager.setManagerLastName(managerDetails.getManagerLastName());
            }
        if(managerDetails.getManagerPassword() != null) {
	        admin.setAdminPassword(managerDetails.getManagerPassword());
        	manager.setManagerPassword(managerDetails.getManagerPassword());
            }
        if(managerDetails.getManagerBirthAdrr() != null) {
            manager.setManagerBirthAdrr(managerDetails.getManagerBirthAdrr());
            }
        if(managerDetails.getManagerCurrAdrr() != null) {
            manager.setManagerCurrAdrr(managerDetails.getManagerCurrAdrr());
            }
        if(managerDetails.getManagerPhone() != null) {
            manager.setManagerPhone(managerDetails.getManagerPhone());
            }
        if(managerDetails.getManagerDOB() != null) {
            manager.setManagerDOB(managerDetails.getManagerDOB());
            }
        Manager updatedManager = managerRepo.save(manager);
        return updatedManager;
    }
}
