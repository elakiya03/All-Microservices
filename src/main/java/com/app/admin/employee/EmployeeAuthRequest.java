package com.app.admin.employee;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class EmployeeAuthRequest{
	
	private String employeeEmail;
	private String employeePassword;
}
