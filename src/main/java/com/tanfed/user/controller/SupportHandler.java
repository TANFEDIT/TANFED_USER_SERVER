package com.tanfed.user.controller;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.tanfed.user.dto.SupportDataSuperadminDto;
import com.tanfed.user.entity.IssueData;
import com.tanfed.user.service.SupportService;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@RestController
@RequestMapping("/api/user/support")
public class SupportHandler {

	@Autowired
	private SupportService supportService;
	private static Logger logger = LoggerFactory.getLogger(SupportHandler.class);

	@PostMapping("/saveissue/{issue}")
	public ResponseEntity<String> saveIssueHandler(@PathVariable String issue,
			@RequestHeader("Authorization") String jwt) throws Exception {
		logger.info(issue);
		return supportService.saveIssue(issue, jwt);
	}

	@GetMapping("/fetchissuesbyempid")
	public List<IssueData> fetchIssuesByEmpIdHandler(@RequestHeader("Authorization") String jwt) throws Exception {
		return supportService.fetchIssuesByEmpId(jwt);
	}

	@GetMapping("/fetchallissues")
	public SupportDataSuperadminDto fetchAllIssuesHandler(@RequestParam String officeName) throws Exception {
		return supportService.fetchAllIssues(officeName);
	}

	@PutMapping("/updateissuestatus/{issueId}/{status}")
	public ResponseEntity<String> updateIssueHandler(@PathVariable String issueId, @PathVariable String status)
			throws Exception {
		return supportService.updateIssue(issueId, status);
	}

	@PutMapping("/updateissueresponse/{issueId}/{res}")
	public ResponseEntity<String> updateIssueResponseHandler(@PathVariable String issueId, @PathVariable String res)
			throws Exception {
		return supportService.updateIssueResponse(issueId, res);
	}

}
