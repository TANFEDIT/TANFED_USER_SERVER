package com.tanfed.user.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.tanfed.user.entity.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

	public User findByEmpId(String empId);
}
