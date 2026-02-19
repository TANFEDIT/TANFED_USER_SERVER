package com.tanfed.user.service;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.tanfed.user.config.JwtProvider;
import com.tanfed.user.controller.SupportHandler;
import com.tanfed.user.dto.IssueDataDto;
import com.tanfed.user.dto.SupportDataSuperadminDto;
import com.tanfed.user.entity.IssueData;
import com.tanfed.user.entity.User;
import com.tanfed.user.repo.IssueDataRepo;

@Service
public class SupportServiceImpl implements SupportService {

	@Autowired
	private UserService userService;

	@Autowired
	private IssueDataRepo issueDataRepo;
	private static Logger logger = LoggerFactory.getLogger(SupportHandler.class);

	@Override
	public ResponseEntity<String> saveIssue(String issue, String jwt, MultipartFile file) throws Exception {
		try {
			User user = userService.fetchUser(jwt);
			String filename = null;
			if (!file.isEmpty()) {
				String uploadDir = "C:/uploads";

				filename = UUID.randomUUID() + "_" + file.getOriginalFilename();

				Path path = Paths.get(uploadDir, filename);

				Files.createDirectories(path.getParent());
				Files.write(path, file.getBytes());
			}

			IssueData obj = new IssueData(null, user.getOfficeName(), user.getEmpId(), user.getEmailId(), issue,
					"Pending", null, UUID.randomUUID().toString(), LocalDate.now(), null, filename);
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
					.filter(item -> (item.getStatus().equals("Pending") || item.getStatus().equals("In Progress"))
							|| (item.getStatus().equals("Solved")
									&& item.getSolvedDate().isAfter(LocalDate.now().minusDays(2))))
					.collect(Collectors.toList());
		} catch (Exception e) {
			throw new Exception(e);
		}
	}

	@Override
	public SupportDataSuperadminDto fetchAllIssues(String officeName) throws Exception {
		try {
			SupportDataSuperadminDto res = new SupportDataSuperadminDto();
			List<IssueData> ticketList = issueDataRepo.findAll();
			res.setOfficeList(ticketList.stream()
					.filter(item -> (item.getStatus().equals("Pending") || item.getStatus().equals("In Progress")))
					.map(i -> i.getOfficeName()).collect(Collectors.toSet()));
			res.setTickets(ticketList.stream()
					.filter(item -> item.getOfficeName().equals(officeName)
							&& (item.getStatus().equals("Pending") || item.getStatus().equals("In Progress")))
					.map(i -> {
						try {
							return new IssueDataDto(i.getId(), i.getOfficeName(), i.getEmpId(), i.getEmail(),
									i.getIssue(), i.getStatus(), i.getResponse(), i.getIssueId(), i.getDate(),
									i.getSolvedDate(), i.getFileName());
						} catch (Exception e) {
							e.printStackTrace();
							return null;
						}
					}).collect(Collectors.toList()));
			return res;
		} catch (Exception e) {
			throw new Exception(e);
		}
	}

//	private Resource getFile(String fileName) throws Exception {
//		Path path = Paths.get("C:/uploads").resolve(fileName);

//		return new UrlResource(path.toUri());
//	}

	@Override
	public ResponseEntity<String> updateIssue(String issueId, String status) throws Exception {
		try {
			logger.info(status);
			logger.info(issueId);
			IssueData issueData = issueDataRepo.findByissueId(issueId);
			issueData.setStatus(status);
			issueData.setSolvedDate(status.equals("Solved") ? LocalDate.now() : null);
			issueDataRepo.save(issueData);
			return new ResponseEntity<String>("Status updated Successfully", HttpStatus.ACCEPTED);
		} catch (Exception e) {
			throw new Exception(e);
		}
	}

	@Override
	public ResponseEntity<String> updateIssueResponse(String issueId, String res) throws Exception {
		try {
			logger.info(issueId);
			IssueData issueData = issueDataRepo.findByissueId(issueId);
			issueData.setResponse(res);
			issueDataRepo.save(issueData);
			return new ResponseEntity<String>("Status updated Successfully", HttpStatus.ACCEPTED);
		} catch (Exception e) {
			throw new Exception(e);
		}
	}

}
