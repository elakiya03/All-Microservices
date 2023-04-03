package com.app.admin.employee;

public class EmployeeNotFoundException extends RuntimeException{
	private static final long serialVersionUID = 1L;
	public EmployeeNotFoundException(String employeeEmail) {
		super("Could not found the user with id="+employeeEmail);
	}

}
