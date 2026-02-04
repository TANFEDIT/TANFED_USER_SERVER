package com.tanfed.user.repo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.tanfed.user.entity.UserTransferData;
@Repository
public interface UserTransferRepo extends JpaRepository<UserTransferData, Long> {

	public List<UserTransferData> findByEmpId(String empId);
}
