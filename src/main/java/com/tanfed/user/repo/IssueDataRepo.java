package com.tanfed.user.repo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.tanfed.user.entity.IssueData;
@Repository
public interface IssueDataRepo extends JpaRepository<IssueData, Long> {

	public List<IssueData> findByEmpId(String empId);
	
	public IssueData findByissueId(String issueId);
}
