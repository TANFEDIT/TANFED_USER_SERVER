package com.tanfed.user.service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.tanfed.user.entity.SessionManager;
import com.tanfed.user.entity.User;
import com.tanfed.user.entity.UserLog;
import com.tanfed.user.entity.UserTransferData;
import com.tanfed.user.repo.SessionManagerRepo;
import com.tanfed.user.repo.UserLogRepo;
import com.tanfed.user.repo.UserRepository;
import com.tanfed.user.repo.UserTransferRepo;

@Service
public class DailyScheduler {

	private static List<User> users = new ArrayList<User>();

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private UserTransferRepo userTransferRepo;

	@Autowired
	private UserLogRepo userLogRepo;

	@Autowired
	private SessionManagerRepo sessionManagerRepo;

	@Scheduled(cron = "0 0 1 * * ?", zone = "Asia/Kolkata")
	public void forceLogout() {
		List<SessionManager> activeUsers = sessionManagerRepo.findAll();
		for (var empId : activeUsers) {
			UserLog userLog = userLogRepo.findByEmpId(empId.getEmpId()).stream().reduce((first, second) -> second)
					.get();
			if (userLog.getLogoutTime() == null) {
				userLog.setLogoutTime(LocalDate.now().toString());
			}
			userLogRepo.save(userLog);
		}
		sessionManagerRepo.deleteAll();
	}

	@Scheduled(cron = "0 0 1 * * ?", zone = "Asia/Kolkata")
	public void dailyJob() {
		if (users.isEmpty()) {
			users.addAll(userRepository.findAll());
		}
		validateAndUpdateUserAccess();
	}

	public void validateAndUpdateUserAccess() {
		if (users.isEmpty()) {
			users.addAll(userRepository.findAll());
		}
		validateEmployeeAccess();
		List<UserTransferData> userList = userTransferRepo.findAll();
		UpdatePromotionData(
				userList.stream().filter(i -> i.getPersonnelType().equals("promotion")).collect(Collectors.toList()));
		updateTransferData(
				userList.stream().filter(i -> i.getPersonnelType().equals("transfer")).collect(Collectors.toList()));
//		updateInchargeData(
//				userList.stream().filter(i -> i.getPersonnelType().equals("inCharge")).collect(Collectors.toList()));
		checkAndUpdateFacExtension(userList.stream()
				.filter(i -> i.getPersonnelType().equals("dateExtension") && i.getExtensionFor().equals("FAC"))
				.collect(Collectors.toList()));
	}

	public void UpdatePromotionData(List<UserTransferData> userList) {
		for (var i : userList) {
			if (i.getJoiningDate().isEqual(LocalDate.now())) {
				User user = users.stream().filter(u -> u.getEmpId().equals(i.getEmpId())).collect(Collectors.toList())
						.get(0);
				user.setDepartment(i.getNewDepartment());
				user.setDesignation(i.getNewDesignation());
				user.setOfficeName(i.getNewOfficeName());
				user.setRole(i.getNewRole());
				userRepository.save(user);
			}
		}
	}

	public void updateTransferData(List<UserTransferData> userList) {
		Map<String, UserTransferData> userMap = userList.stream()
				.collect(Collectors.toMap(UserTransferData::getEmpId, u -> u, (oldVal, newVal) -> newVal));

		List<UserTransferData> userListFinal = new ArrayList<>(userMap.values());

		for (var i : userListFinal) {
			User user = users.stream().filter(u -> u.getEmpId().equals(i.getEmpId())).collect(Collectors.toList())
					.get(0);
			if (i.getRelievedDate() != null && i.getJoiningDate() == null
					&& i.getRelievedDate().isEqual(LocalDate.now())) {
				user.setIsBlocked(true);
			}
			if (i.getJoiningDate() != null && i.getJoiningDate().isEqual(LocalDate.now())) {
				user.setDepartment(i.getNewDepartment());
				user.setDesignation(i.getNewDesignation());
				user.setOfficeName(i.getNewOfficeName());
				user.setRole(i.getNewRole());
				user.setIsBlocked(false);
			}
			userRepository.save(user);
		}
	}

//	public void updateInchargeData(List<UserTransferData> userList) {
//		for (var i : userList) {
//			User user = users.stream().filter(u -> u.getEmpId().equals(i.getEmpId())).collect(Collectors.toList())
//					.get(0);
//			if (i.getFromDate() != null && i.getFromDate().isEqual(LocalDate.now())) {
//				user.getRole().addAll(i.getNewRole());
//			}
//			if (i.getCancelDate() != null && i.getCancelDate().isEqual(LocalDate.now())) {
//				user.setRole(i.getCurrentRole());
//			}
//			userRepository.save(user);
//		}
//	}

	public void checkAndUpdateFacExtension(List<UserTransferData> userList) {
		for (var i : userList) {
			User user = users.stream().filter(u -> u.getEmpId().equals(i.getEmpId())).collect(Collectors.toList())
					.get(0);
			if (i.getFromDate() != null && i.getFromDate().isEqual(LocalDate.now())) {
				user.setIsBlocked(false);
			}
			if (i.getToDate() != null
					&& (i.getToDate().isEqual(LocalDate.now()) || i.getToDate().isBefore(LocalDate.now()))) {
				user.setIsBlocked(true);
			}
			userRepository.save(user);
		}
	}

	public void validateEmployeeAccess() {
		List<User> govEmps = users.stream().filter(i -> i.getNatureOfEmployment().equals("Gov. Employee"))
				.collect(Collectors.toList());
		for (var gov : govEmps) {
			if ((gov.getDeputedAs().equals("Transfer") && gov.getDeputationJoinDate().equals(LocalDate.now()))
					|| (gov.getDeputedAs().equals("FAC") && gov.getDeputationFromDate().equals(LocalDate.now()))) {
				gov.setIsBlocked(false);
			}
			if (gov.getDeputedAs().equals("FAC") && gov.getDeputationToDate().equals(LocalDate.now())) {
				gov.setIsBlocked(true);
			}
		}
		userRepository.saveAll(govEmps);
	}

}
