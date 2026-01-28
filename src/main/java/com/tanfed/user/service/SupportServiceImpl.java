package com.tanfed.user.service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.tanfed.user.config.JwtProvider;
import com.tanfed.user.entity.IssueData;
import com.tanfed.user.entity.User;
import com.tanfed.user.repo.IssueDataRepo;

@Service
public class SupportServiceImpl implements SupportService {

	@Autowired
	private UserService userService;

	@Autowired
	private IssueDataRepo issueDataRepo;

	@Override
	public ResponseEntity<String> saveIssue(String issue, String jwt) throws Exception {
		try {
			User user = userService.fetchUser(jwt);
			IssueData obj = new IssueData(0L, user.getOfficeName(), user.getEmpId(), user.getEmailId(), issue,
					"Pending", null, null);
			issueDataRepo.save(obj);
			return new ResponseEntity<String>("Issue Saved", HttpStatus.CREATED);

		} catch (Exception e) {
			throw new Exception(e);
		}
	}

	@Override
	public List<IssueData> fetchIssuesByEmpId(String jwt) throws Exception {
		try {
			String empId = JwtProvider.getEmailFromJwtToken(jwt);
			return issueDataRepo.findByEmpId(empId).stream()
					.filter(item -> (item.getStatus().equals("Pending") || item.getStatus().equals("Inprogress"))
							|| (item.getStatus().equals("Closed")
									&& item.getDate().isAfter(LocalDate.now().minusDays(2))))
					.collect(Collectors.toList());
		} catch (Exception e) {
			throw new Exception(e);
		}
	}

	@Override
	public List<IssueData> fetchAllIssues() throws Exception {
		try {
			return issueDataRepo.findAll().stream()
					.filter(item -> (item.getStatus().equals("Pending") || item.getStatus().equals("Inprogress")))
					.collect(Collectors.toList());
		} catch (Exception e) {
			throw new Exception(e);
		}
	}

	@Override
	public ResponseEntity<String> updateIssue(String issueId, String status) throws Exception {
		try {
			IssueData issueData = issueDataRepo.findByissueId(issueId);
			issueData.setStatus(status);
			issueDataRepo.save(issueData);
			return new ResponseEntity<String>("Status updated Successfully", HttpStatus.ACCEPTED);
		} catch (Exception e) {
			throw new Exception(e);
		}
	}

}
