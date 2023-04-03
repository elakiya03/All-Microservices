package com.app.admin;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.app.admin.employee.EmployeeAuthRequest;
import com.app.admin.employee.EmployeeRegRequest;
import com.app.admin.manager.ManagerAuthRequest;
import com.app.admin.manager.ManagerRegRequest;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/admincontroller")
@CrossOrigin("http://localhost:3000")
@RequiredArgsConstructor
public class AuthenticationController {
	
	@Autowired
	private AuthenticationService authenticationService;
	
	@PostMapping("/register")
	public ResponseEntity<AuthenticationResponse> register(@RequestBody RegisterRequest request){
		return ResponseEntity.ok(authenticationService.register(request));
	}
	
	@PostMapping("/authenticate")
	public ResponseEntity<AuthenticationResponse> authenticate(@RequestBody AuthenticateRequest request){
		return ResponseEntity.ok(authenticationService.authenticate(request));

	}
	
	@PostMapping("/employeereg")
	public ResponseEntity<AuthenticationResponse> employeeRegister(@RequestBody EmployeeRegRequest request){
		return ResponseEntity.ok(authenticationService.employeeRegister(request));
	}
	
	@PostMapping("/employeeauth")
	public ResponseEntity<AuthenticationResponse> employeeAuthenticate(@RequestBody EmployeeAuthRequest request){
		return ResponseEntity.ok(authenticationService.employeeAuthenticate(request));

	}
	
	@PostMapping("/managerreg")
	public ResponseEntity<AuthenticationResponse> managerRegister(@RequestBody ManagerRegRequest request){
		return ResponseEntity.ok(authenticationService.managerRegister(request));
	}
	
	@PostMapping("/managerauth")
	public ResponseEntity<AuthenticationResponse> managerAuthenticate(@RequestBody ManagerAuthRequest request){
		return ResponseEntity.ok(authenticationService.managerAuthenticate(request));

	}
}
