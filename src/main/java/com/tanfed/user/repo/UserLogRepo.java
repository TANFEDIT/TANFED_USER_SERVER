package com.tanfed.user.repo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.tanfed.user.entity.UserLog;
@Repository
public interface UserLogRepo extends JpaRepository<UserLog, Long> {

	public List<UserLog> findByEmpId(String empId);
}
