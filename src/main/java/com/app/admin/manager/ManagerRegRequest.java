package com.app.admin.manager;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ManagerRegRequest {
	
	private String managerEmail;
	private String managerPassword;
	private String role;
	
}
