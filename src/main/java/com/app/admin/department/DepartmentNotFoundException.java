package com.app.admin.department;

public class DepartmentNotFoundException extends RuntimeException{
	private static final long serialVersionUID = 1L;
	public DepartmentNotFoundException(Long id) {
		super("Could not found the user with id="+id);
	}

}
