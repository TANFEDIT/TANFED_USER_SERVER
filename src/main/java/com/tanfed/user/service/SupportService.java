package com.tanfed.user.service;

import java.util.List;

import org.springframework.http.ResponseEntity;

import com.tanfed.user.entity.IssueData;

public interface SupportService {

	public ResponseEntity<String> saveIssue(String issue, String jwt) throws Exception;
	
	public List<IssueData> fetchIssuesByEmpId(String jwt) throws Exception;

	public List<IssueData> fetchAllIssues() throws Exception;
	
	public ResponseEntity<String> updateIssue(String issueId, String status) throws Exception;
}
