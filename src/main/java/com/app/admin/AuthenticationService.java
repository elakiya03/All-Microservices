package com.app.admin;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.app.admin.employee.Employee;
import com.app.admin.employee.EmployeeAuthRequest;
import com.app.admin.employee.EmployeeRegRequest;
import com.app.admin.employee.EmployeeRepository;
import com.app.admin.manager.Manager;
import com.app.admin.manager.ManagerAuthRequest;
import com.app.admin.manager.ManagerRegRequest;
import com.app.admin.manager.ManagerRepo;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthenticationService {
	
	@Autowired
	private AdminRepository adminRepo;
	
	@Autowired
	private EmployeeRepository employeeRepo;
	
	@Autowired
	private ManagerRepo managerRepo;
	
	@Autowired
	private PasswordEncoder passwordEncoder;
	
	@Autowired
	private JwtService jwtService;
	
	@Autowired
	private AuthenticationManager authenticationManager;
	
	public AuthenticationResponse register(RegisterRequest request) {
		var admin = Admin.builder()
				.adminEmail(request.getAdminEmail())
				.adminPassword(passwordEncoder.encode(request.getAdminPassword()))
				.role(Role.ADMIN)
				.build();
				
		adminRepo.save(admin);
		var jwtToken = jwtService.generateToken(admin);
		return AuthenticationResponse.builder()
				.token(jwtToken)
				.build();
	}
	
	public AuthenticationResponse authenticate(AuthenticateRequest request) {
		authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(request.getAdminEmail(), request.getAdminPassword()));
		var admin = adminRepo.findByAdminEmail(request.getAdminEmail()).orElseThrow();
		var jwtToken = jwtService.generateToken(admin);		
		return AuthenticationResponse.builder().token(jwtToken).build();
	}

	public AuthenticationResponse employeeAuthenticate(EmployeeAuthRequest request) {
		//authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(request.getEmployeeEmail(), request.getEmployeePassword()));
		var employee = employeeRepo.findByEmployeeEmail(request.getEmployeeEmail()).orElseThrow();
		var jwtToken = jwtService.generateToken(employee);
		return AuthenticationResponse.builder().token(jwtToken).build();
	}

	public AuthenticationResponse employeeRegister(EmployeeRegRequest request) {
		var employee = Employee.builder()
				.employeeEmail(request.getEmployeeEmail())
				.employeePassword(request.getEmployeePassword())
				.role(Role.EMPLOYEE)
				.build();
		var admin = Admin.builder()
				.adminEmail(request.getEmployeeEmail())
				.adminPassword(passwordEncoder.encode(request.getEmployeePassword()))
				.role(Role.EMPLOYEE)
				.build();
				
		adminRepo.save(admin);
		employeeRepo.save(employee);
		var jwtToken = jwtService.generateToken(employee);
		return AuthenticationResponse.builder()
				.token(jwtToken)
				.build();
	}
	
	public AuthenticationResponse managerAuthenticate(ManagerAuthRequest request) {
		//authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(request.getEmployeeEmail(), request.getEmployeePassword()));
		var employee = managerRepo.findByManagerEmail(request.getManagerEmail()).orElseThrow();
		var jwtToken = jwtService.generateToken(employee);
		return AuthenticationResponse.builder().token(jwtToken).build();
	}

	public AuthenticationResponse managerRegister(ManagerRegRequest request) {
		var manager = Manager.builder()
				.managerEmail(request.getManagerEmail())
				.managerPassword(request.getManagerPassword())
				.role(Role.MANAGER)
				.build();
		var admin = Admin.builder()
				.adminEmail(request.getManagerEmail())
				.adminPassword(passwordEncoder.encode(request.getManagerPassword()))
				.role(Role.MANAGER)
				.build();
				
		adminRepo.save(admin);
		managerRepo.save(manager);
		var jwtToken = jwtService.generateToken(manager);
		return AuthenticationResponse.builder()
				.token(jwtToken)
				.build();
	}
}
