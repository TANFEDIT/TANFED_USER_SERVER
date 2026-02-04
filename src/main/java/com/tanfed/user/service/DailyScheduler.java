package com.tanfed.user.service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.tanfed.user.entity.User;
import com.tanfed.user.entity.UserTransferData;
import com.tanfed.user.repo.UserRepository;
import com.tanfed.user.repo.UserTransferRepo;

@Service
public class DailyScheduler {

	private static List<User> users = new ArrayList<User>();

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private UserTransferRepo userTransferRepo;

	@Scheduled(cron = "0 0 23 * * ?", zone = "Asia/Kolkata")
	public void dailyJob() {
		if (users.isEmpty()) {
			users.addAll(userRepository.findAll());
		}
		validateAndUpdateUserAccess();
	}

	public void validateAndUpdateUserAccess() {
		List<UserTransferData> userList = userTransferRepo.findAll();
		UpdatePromotionData(
				userList.stream().filter(i -> i.getTransferType().equals("promotion")).collect(Collectors.toList()));
		updateTransferData(
				userList.stream().filter(i -> i.getTransferType().equals("transfer")).collect(Collectors.toList()));
		updateInchargeAndAdditionalChargeData(userList.stream()
				.filter(i -> (i.getTransferType().equals("inCharge") || i.getTransferType().equals("additionalCharge")))
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
			if (i.getRelievedDate() != null && i.getJoiningDate() == null
					&& i.getRelievedDate().isEqual(LocalDate.now())) {
				User user = users.stream().filter(u -> u.getEmpId().equals(i.getEmpId())).collect(Collectors.toList())
						.get(0);
				user.setIsBlocked(true);
				userRepository.save(user);
			}
			if (i.getRelievedDate() == null && i.getJoiningDate() != null
					&& i.getJoiningDate().isEqual(LocalDate.now())) {
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

	public void updateInchargeAndAdditionalChargeData(List<UserTransferData> userList) {
		for (var i : userList) {
			if (i.getFromDate() != null && i.getFromDate().isEqual(LocalDate.now())) {
				User user = users.stream().filter(u -> u.getEmpId().equals(i.getEmpId())).collect(Collectors.toList())
						.get(0);
				user.setDepartment(user.getDepartment() + ", " + i.getNewDepartment());
				user.setOfficeName(i.getTransferType().equals("additionalCharge")
						? user.getOfficeName() + ", " + i.getNewOfficeName()
						: user.getOfficeName());
				user.getRole().addAll(i.getNewRole());
				userRepository.save(user);
			}
			if (i.getToDate() != null && i.getToDate().isEqual(LocalDate.now())) {
				User user = users.stream().filter(u -> u.getEmpId().equals(i.getEmpId())).collect(Collectors.toList())
						.get(0);
				user.setDepartment(i.getCurrentDepartment());
				user.setOfficeName(i.getTransferType().equals("additionalCharge") ? i.getCurrentOfficeName()
						: user.getOfficeName());
				user.setRole(i.getCurrentRole());
				userRepository.save(user);
			}
		}
	}

}
