package com.app.admin.manager;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface ManagerRepo extends JpaRepository<Manager, Long>{
	Optional<Manager> findByManagerEmail(String managerEmail);
	List<Manager> findByDepartmentId(Long departmentId);
	
//	@Query("Select l FROM Manager l WHERE l.departmentId = :departmentId")
//	Manager findByDepartmentIdd(Long departmentId);
	
}
