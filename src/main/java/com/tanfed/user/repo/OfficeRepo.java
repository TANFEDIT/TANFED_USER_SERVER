package com.tanfed.user.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.tanfed.user.entity.Office;
@Repository
public interface OfficeRepo extends JpaRepository<Office, Long> {

	public Office findByOfficeName(String officeName);
}
