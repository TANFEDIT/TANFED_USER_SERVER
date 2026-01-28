package com.tanfed.user.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.tanfed.user.entity.IssueData;
import com.tanfed.user.service.SupportService;

import io.swagger.v3.oas.annotations.parameters.RequestBody;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

@RestController
@RequestMapping("/api/user/support")
public class SupportHandler {

	@Autowired
	private SupportService supportService;

	@PostMapping("/saveissue")
	public ResponseEntity<String> saveIssueHandler(@RequestBody String issue, String jwt) throws Exception {
		return supportService.saveIssue(issue, jwt);
	}

	@GetMapping("fetchissuesbyempid")
	public List<IssueData> fetchIssuesByEmpIdHandler(@RequestParam String jwt) throws Exception {
		return supportService.fetchIssuesByEmpId(jwt);
	}

	@GetMapping("/fetchallissues")
	public List<IssueData> fetchAllIssuesHandler() throws Exception {
		return supportService.fetchAllIssues();
	}

	@PutMapping("/updateissuestatus/{issueId}/{status}")
	public ResponseEntity<String> updateIssueHandler(@PathVariable String issueId, String status) throws Exception {
		return supportService.updateIssue(issueId, status);
	}

}
