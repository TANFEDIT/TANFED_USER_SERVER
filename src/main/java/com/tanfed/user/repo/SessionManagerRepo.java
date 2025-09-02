package com.tanfed.user.repo;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.tanfed.user.entity.SessionManager;
@Repository
public interface SessionManagerRepo extends JpaRepository<SessionManager, Long> {

	public Optional<SessionManager> findByEmpId(String empId);

	public void deleteByEmpId(String empId);
	
	
}
