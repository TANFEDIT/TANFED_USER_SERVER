package com.tanfed.user.service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
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

	@Scheduled(cron = "0 0 1 * * ?", zone = "Asia/Kolkata")
	public void dailyJob() {
		if (users.isEmpty()) {
			users.addAll(userRepository.findAll());
		}
		validateAndUpdateUserAccess();
	}

	public void validateAndUpdateUserAccess() {
		List<UserTransferData> userList = userTransferRepo.findAll();
		UpdatePromotionData(
				userList.stream().filter(i -> i.getTransferType().equals("Promotion")).collect(Collectors.toList()));
		updateTransferData(
				userList.stream().filter(i -> i.getTransferType().equals("Transfer")).collect(Collectors.toList()));
	}

	public void UpdatePromotionData(List<UserTransferData> userList) {
		for (var i : userList) {
			if (i.getTransferType().equals("Promotion") && i.getJoiningDate().isEqual(LocalDate.now())) {
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
		
	}

}
