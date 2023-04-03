package com.app.admin.department;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

@Data
@Entity
@Table(name="department")
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Department {
	
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE)
	private Long departmentId;
	
	@Column(unique = true)
	private String departmentName;
	
//	@Column(unique = true)
//	private Long managerId;
//	private String managerName;
}
