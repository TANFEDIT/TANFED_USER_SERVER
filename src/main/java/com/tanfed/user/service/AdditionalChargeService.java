package com.tanfed.user.service;

import java.time.LocalDate;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.tanfed.user.dto.OfficeHeader;
import com.tanfed.user.dto.OfficeInfo;
import com.tanfed.user.entity.User;
import com.tanfed.user.entity.UserTransferData;
import com.tanfed.user.repo.UserRepository;
import com.tanfed.user.repo.UserTransferRepo;

@Service
public class AdditionalChargeService {

	@Autowired
	private UserService userService;

	@Autowired
	private MasterService masterService;

	@Autowired
	private UserTransferRepo userTransferRepo;
	private static Logger logger = LoggerFactory.getLogger(AdditionalChargeService.class);

	public OfficeHeader getAddtionalChargeInfo(String officeName, String jwt) {
		User user = userService.fetchUser(jwt);
		String[] office = officeName.split(" - ");
		List<UserTransferData> userTransferData = userTransferRepo.findAll();
		UserTransferData collect = userTransferData.stream().filter(i -> {
			logger.info("{}", i.getNewRole());
			String role = i.getNewRole().stream().map(k -> k.name()).collect(Collectors.joining(", "));
			logger.info("{}", i.getEmpId());
			return (i.getPersonnelType().equals("additionalCharge") || i.getPersonnelType().equals("inCharge")
					|| (i.getPersonnelType().equals("dateExtension") && "additionalCharge".equals(i.getExtensionFor())))
					&& role.equals(office[1]) && i.getEmpId().equals(user.getEmpId())
					&& i.getNewOfficeName().equals(office[0]);
		}).reduce((first, second) -> second).orElse(null);

		try {

			OfficeInfo officeInfo = masterService.getOfficeInfoByOfficeNameHandler(office[0], jwt);
			return new OfficeHeader(user.getEmpId(), collect != null ? collect.getNewRole() : user.getRole(),
					user.getEmpName(), user.getDesignation(), officeInfo.getTanNo(), officeInfo.getOfficeName(),
					officeInfo.getOfficeType(), officeInfo.getDoor(), officeInfo.getStreet(), officeInfo.getDistrict(),
					officeInfo.getPincode());
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	@Autowired
	private UserRepository userRepository;

	public Set<String> getAdditionalOfficeNameList(String empId) {
		List<UserTransferData> userTransferData = userTransferRepo.findAll();
		LocalDate today = LocalDate.now();
		List<String> officeList = userTransferData.stream().filter(i -> {
			String type = i.getPersonnelType();

			boolean isValidType = ("additionalCharge".equals(type) && empId.equals(i.getEmpId()))
					|| ("dateExtension".equals(type) && "additionalCharge".equals(i.getExtensionFor())
							&& empId.equals(i.getEmpId()))
					|| ("inCharge".equals(type) && empId.equals(i.getEmpId()));
			if (!isValidType)
				return false;
			boolean isFromDateValid = !today.isBefore(i.getFromDate());
			boolean isToDateValid = "inCharge".equals(type) || !today.isAfter(i.getToDate());
			boolean isNotCancelled = i.getCancelDate() == null || today.isBefore(i.getCancelDate());
			return isFromDateValid && isToDateValid && isNotCancelled;
		}).map(i -> {
			String role = i.getNewRole().stream().map(k -> k.name()).collect(Collectors.joining(", "));
			return i.getNewOfficeName() + " - " + role;
		}).collect(Collectors.toList());
		User user = userRepository.findByEmpId(empId);
		String role = user.getRole().stream().map(k -> k.name()).collect(Collectors.joining(", "));
		officeList.add(user.getOfficeName() + " - " + role);
		Collections.reverse(officeList);
		Set<String> roles = new LinkedHashSet<>(officeList);
		return officeList.isEmpty() ? new HashSet<String>() : roles;
	}

}
