package com.app.admin.manager;


import java.time.LocalDate;
import java.util.Collection;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.app.admin.Role;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name="manager")
public class Manager implements UserDetails {
	
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE)
	private Long managerId;
	
	private String managerFirstName;
	private String managerLastName;
	private Long managerPhone;
	private String managerBirthAdrr;
	private String managerCurrAdrr;
	private LocalDate managerDOB;
	
	@Column(unique=true)
	private Long departmentId;
	private String departmentName;
	private String managerPosition;
	private String managerGender;
	
	@Column(unique=true)
	private String managerEmail;
	private String managerPassword;
	
	@Override
	public String getPassword() {
		return managerPassword;
	}
	@Override
	public String getUsername() {
		return managerEmail;
	}
	
	@Override
	public boolean isAccountNonExpired() {
		return true;
	}
	@Override
	public boolean isAccountNonLocked() {
		return true;
	}
	@Override
	public boolean isCredentialsNonExpired() {
		return true;
	}
	@Override
	public boolean isEnabled() {
		return true;
	
}
	@Enumerated(EnumType.STRING)
	private Role role;
	
	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return List.of(new SimpleGrantedAuthority(role.name()));
	}
}	