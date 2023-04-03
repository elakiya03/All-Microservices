package com.app.admin.manager;

public class ManagerNotFoundException extends RuntimeException {
	
	private static final long serialVersionUID = 1L;

	public ManagerNotFoundException(String managerEmail) {
		super("Could not found the user with id="+managerEmail);
	}

}
