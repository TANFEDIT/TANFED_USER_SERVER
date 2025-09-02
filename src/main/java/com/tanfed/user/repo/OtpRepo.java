package com.tanfed.user.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.tanfed.user.entity.OtpEntity;
@Repository
public interface OtpRepo extends JpaRepository<OtpEntity, Long> {

	public OtpEntity findByEmpIdAndOtp(String empId, Integer otp);
}
