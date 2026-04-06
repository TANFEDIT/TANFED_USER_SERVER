package com.tanfed.user.service;

import java.io.IOException;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tanfed.user.config.JwtProvider;
import com.tanfed.user.config.JwtTokenValidator;
import com.tanfed.user.dto.UserPersonnelView;
import com.tanfed.user.dto.UserTransfer_PromotionModel;
import com.tanfed.user.entity.*;
import com.tanfed.user.repo.*;
import com.tanfed.user.request.PasswordData;
import com.tanfed.user.response.UserRegResponseData;
import com.tanfed.user.utils.DesignationAndDept;
import com.tanfed.user.utils.UserRole;

import jakarta.transaction.Transactional;

@Service
public class UserServiceImpl implements UserService {

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private PasswordEncoder passwordEncoder;

	@Autowired
	private UserLogRepo userLogRepo;

	@Autowired
	private OfficeRepo officeRepo;

	@Autowired
	private ObjectMapper mapper;

	@Autowired
	private UserTransferRepo userTransferRepo;

	@Autowired
	private SessionManagerRepo sessionManagerRepo;
	private static Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);

	@Override
	public User fetchUser(String jwt) {
		String empId = JwtProvider.getEmailFromJwtToken(jwt);
		return userRepository.findByEmpId(empId);
	}

	@Override
	public UserRegResponseData fetchDataForUserForm(String jwt, String officeName, String department) throws Exception {
		try {
			UserRegResponseData data = new UserRegResponseData();

			User fetchedUser = fetchUser(jwt);
			if (department != null && !department.isEmpty()) {
				data.setRoleList(getRoleList(fetchedUser.getRole(), department));
			}
			List<String> deptFiltered = Arrays.asList(DesignationAndDept.department).stream().filter(
					i -> officeName.equals("Head Office") ? !i.equals("Regional Office") : i.equals("Regional Office"))
					.collect(Collectors.toList());
			data.setDeptList(deptFiltered);
			data.setDesignationList(Arrays.asList(DesignationAndDept.designation));
			data.setOfficeNameList(
					officeRepo.findAll().stream().map(Office::getOfficeName).collect(Collectors.toList()));

			return data;
		} catch (Exception e) {
			throw new Exception(e);
		}

	}

	private List<String> getRoleList(List<UserRole> roles, String department) {
		if (roles.contains(UserRole.SUPERADMIN)) {
			return Arrays.asList(DesignationAndDept.role.get(department));
		} else if (roles.contains(UserRole.ROADMIN)) {
			return Arrays.asList(DesignationAndDept.role.get(department)).stream().filter(item -> {
				return item.equals("ROUSER");
			}).collect(Collectors.toList());
		} else if (roles.contains(UserRole.ESTADMIN) || roles.contains(UserRole.ESTUSER)) {
			return Arrays.asList(DesignationAndDept.role.get(department)).stream().filter(item -> {
				return !item.equals("SUPERADMIN");
			}).collect(Collectors.toList());
		} else {
			return null;
		}
	}

	@Override
	public ResponseEntity<String> saveUserImage(String empId, MultipartFile img) throws IOException {
		try {
			User userData = userRepository.findByEmpId(empId);
			if (userData == null) {
				throw new UsernameNotFoundException("User not found with empId :" + empId);
			} else {
				if (img == null) {
					throw new NullPointerException("Image data is null");
				} else {
					userData.setImgName(img.getOriginalFilename());
					userData.setImgType(img.getContentType());
					userData.setImgData(img.getBytes());

					userRepository.save(userData);
				}

				return new ResponseEntity<String>("Profile Ing uploaded", HttpStatus.ACCEPTED);
			}
		} catch (IOException e) {
			throw new IOException(e);
		}
	}

	@Autowired
	private JwtTokenValidator jwtTokenValidator;

	@Transactional
	@Override
	public ResponseEntity<String> saveUserSessionLog(String jwt) throws Exception {
		try {
			String empId = JwtProvider.getEmailFromJwtToken(jwt);

			List<UserLog> byEmpId = userLogRepo.findByEmpId(empId);
			UserLog last = byEmpId.get(byEmpId.size() - 1);

			if (last.getLogoutTime() == null) {
				last.setLogoutTime(ZonedDateTime.now(ZoneId.of("Asia/Kolkata")).toString());
			}
			userLogRepo.save(last);

			jwtTokenValidator.blockListJwt(jwt);

			sessionManagerRepo.deleteByEmpId(empId);

			SecurityContextHolder.clearContext();
			return new ResponseEntity<String>("Logged out successfully.", HttpStatus.OK);
		} catch (Exception e) {
			throw new Exception(e);
		}
	}

	@Override
	public ResponseEntity<String> updateUser(User user, String jwt) throws Exception {
		try {
			String empId = JwtProvider.getEmailFromJwtToken(jwt);
			User userData = userRepository.findByEmpId(empId);
			if (userData == null) {
				throw new UsernameNotFoundException("User not found with empId :" + empId);
			} else {
				userData.setMobileNo2(user.getMobileNo2());
				userData.setCurrentAddress(user.getCurrentAddress());
				userData.setPermanentAddress(user.getPermanentAddress());
				userRepository.save(userData);
				return new ResponseEntity<String>("User updated Successfully", HttpStatus.ACCEPTED);
			}
		} catch (Exception e) {
			throw new Exception("User updation failed!" + e);
		}
	}

	@Override
	public ResponseEntity<String> updatePassword(PasswordData obj) throws Exception {
		try {
			User user = userRepository.findByEmpId(obj.getEmpId());
			if (user == null) {
				throw new UsernameNotFoundException("User not found with empId :" + obj.getEmpId());
			} else {
				if (passwordEncoder.matches(obj.getOldPassword(), user.getPassword())) {
					user.setPassword(passwordEncoder.encode(obj.getNewPassword()));
					userRepository.save(user);
					return new ResponseEntity<String>("password updated Successfully", HttpStatus.ACCEPTED);
				} else {
					return new ResponseEntity<String>("Wrong Password", HttpStatus.BAD_REQUEST);
				}
			}
		} catch (Exception e) {
			throw new Exception("password updation failed!" + e);
		}
	}

	@Override
	public List<User> fetchUsers(String officeName) throws Exception {
		try {
			return userRepository.findByOfficeName(officeName).stream()
					.filter(i -> !i.getDepartment().equals("IT Wing")).collect(Collectors.toList());
		} catch (Exception e) {
			throw new Exception(e);
		}
	}

	@Override
	public UserTransfer_PromotionModel fetchTransferAndPromotionData(String officeName, String empId, String jwt,
			String natureOfEmployment, String personnelType, String department, String extensionFor) throws Exception {
		UserTransfer_PromotionModel res = new UserTransfer_PromotionModel();
		List<Office> office = officeRepo.findAll();
		User fetchedUser = fetchUser(jwt);
		res.setOfficeList(office.stream().map(i -> i.getOfficeName()).collect(Collectors.toList()));

		if (personnelType != null && !personnelType.isEmpty() && personnelType.equals("view")) {
			List<UserTransferData> userTransferData = userTransferRepo.findAll();
			res.setUserViewData(userTransferData.stream().filter(i -> (i.getPersonnelType().equals("inCharge")
					&& i.getCancelDate() == null)
					|| (i.getPersonnelType().equals("additionalCharge") && i.getCancelDate() == null
							&& (LocalDate.now()
									.isAfter(i.getFromDate())
									&& (LocalDate.now().isBefore(i.getToDate())
											|| LocalDate.now().equals(i.getToDate()))))
					|| (i.getPersonnelType().equals("dateExtension") && i.getCancelDate() == null
							&& (i.getExtensionFor().equals("additionalCharge") || i.getExtensionFor().equals("FAC"))
							&& (LocalDate.now().isAfter(i.getFromDate()) && (LocalDate.now().isBefore(i.getToDate())
									|| LocalDate.now().equals(i.getToDate())))))
					.map(i -> new UserPersonnelView(i.getId(), i.getCreatedAt(), i.getEmpId(), i.getEmpName(),
							i.getCurrentDesignation(), i.getCurrentDepartment(), i.getCurrentOfficeName(),
							i.getCurrentRole(), i.getNewOfficeName(), i.getNewRole(), i.getNewDepartment(),
							i.getPersonnelType(), i.getExtensionFor() != null ? i.getExtensionFor() : null))
					.collect(Collectors.toList()));
			res.getUserViewData().addAll(userRepository.findAll().stream()
					.filter(i -> i.getNatureOfEmployment().equals("Gov. Employee") && i.getDeputedAs().equals("FAC")
							&& i.getIsBlocked().equals(false))
					.map(i -> new UserPersonnelView(i.getId(), i.getDeputationFromDate(), i.getEmpId(), i.getEmpName(),
							i.getDesignation(), i.getDepartment(), i.getOfficeName(), i.getRole(), i.getOfficeName(),
							i.getRole(), i.getDepartment(), "FAC", null))
					.collect(Collectors.toList()));
		}
		if (officeName != null && !officeName.isEmpty()) {
			if ("dateExtension".equals(personnelType)) {
				if ("FAC".equals(extensionFor)) {
					res.setEmpIdList(fetchUsers(officeName).stream()
							.filter(i -> i.getNatureOfEmployment().equals("Gov. Employee")
									&& i.getDeputedAs().equals("FAC")
									&& !LocalDate.now().isAfter(i.getDeputationToDate()))
							.map(i -> i.getEmpId()).collect(Collectors.toSet()));
				} else {
					res.setEmpIdList(userTransferRepo.findAll().stream().filter(i -> (i.getPersonnelType()
							.equals(extensionFor)
							|| (i.getPersonnelType().equals(personnelType) && i.getExtensionFor().equals(extensionFor)))
							&& ((i.getCancelDate() != null && LocalDate.now().isBefore(i.getCancelDate()))
									|| (i.getToDate() != null && !LocalDate.now().isAfter(i.getToDate())
											&& i.getCancelDate() == null)))
							.map(i -> i.getEmpId()).collect(Collectors.toSet()));
				}
			} else {
				res.setEmpIdList(fetchUsers(officeName).stream()
						.filter(i -> i.getNatureOfEmployment().equals(natureOfEmployment)).map(i -> i.getEmpId())
						.collect(Collectors.toSet()));
			}
			res.setDesignationList(Arrays.asList(DesignationAndDept.designation));
			res.setDeptList(Arrays.asList(DesignationAndDept.department));
			if (empId != null && !empId.isEmpty()) {
				if ("dateExtension".equals(personnelType)) {
					res.setUserTransferData(userTransferRepo.findByEmpId(empId).stream().filter(i -> i
							.getPersonnelType().equals(extensionFor)
							|| (i.getPersonnelType().equals(personnelType) && i.getExtensionFor().equals(extensionFor)))
							.reduce((first, second) -> second).orElse(null));
				}
				res.setUser(userRepository.findByEmpId(empId));
				if ("transfer".equals(personnelType)) {
					res.setUserTransferData(userTransferRepo.findByEmpId(empId).stream()
							.filter(i -> i.getPersonnelType().equals("transfer") && i.getJoiningDate() == null)
							.reduce((first, second) -> second).orElse(null));
				}
				if (department != null && !department.isEmpty()) {
					res.setRoleList(getRoleList(fetchedUser.getRole(), department));
				}
			}
		}
		return res;
	}

	@Override
	public ResponseEntity<String> saveUserTransferData(String obj, MultipartFile file) throws Exception {
		try {
			logger.info(obj);
			UserTransferData userData = mapper.readValue(obj, UserTransferData.class);

			if (userData.getRelievedDate() != null) {
				User user = userRepository.findByEmpId(userData.getEmpId());
				userData.setCurrentDepartment(user.getDepartment());
				userData.setCurrentDesignation(user.getDesignation());
				userData.setCurrentOfficeName(user.getOfficeName());
				userData.setCurrentRole(user.getRole());
				if (userData.getRelievedDate().equals(LocalDate.now())
						|| userData.getRelievedDate().isBefore(LocalDate.now())) {
					user.setIsBlocked(true);
					userRepository.save(user);
				}

			}
			if (userData.getPersonnelType().equals("transfer") && userData.getJoiningDate() != null) {
				UserTransferData userTransferData = userTransferRepo.findById(userData.getId()).get();
				userTransferData.setJoiningDate(userData.getJoiningDate());
				userTransferRepo.save(userTransferData);
				userData.setId(null);
				if (userData.getJoiningDate().equals(LocalDate.now())
						|| userData.getJoiningDate().isBefore(LocalDate.now())) {
					User user = userRepository.findByEmpId(userData.getEmpId());
					user.setDepartment(userData.getNewDepartment());
					user.setDesignation(userData.getNewDesignation());
					user.setOfficeName(userData.getNewOfficeName());
					user.setRole(userData.getNewRole());
					user.setIsBlocked(false);
					userRepository.save(user);
				}
			}
			if (file != null) {
				userData.setFileData(file.getBytes());
				userData.setFileName(file.getOriginalFilename());
				userData.setFileType(file.getContentType());
			}
			if (userData.getCancelDate() != null) {
				if (userData.getPersonnelType().equals("FAC") && userData.getCancelDate().equals(LocalDate.now())
						|| userData.getCancelDate().isBefore(LocalDate.now())) {
					User user = userRepository.findByEmpId(userData.getEmpId());
					user.setIsBlocked(true);
					userRepository.save(user);
					userData.setId(null);
				} else {
					UserTransferData userTransferData = userTransferRepo.findById(userData.getId()).get();
					userTransferData.setCancelDate(userData.getCancelDate());
					userTransferRepo.save(userTransferData);
					userData.setId(null);
				}
			}
			if (userData.getPersonnelType().equals("promotion") && userData.getJoiningDate() != null) {
				if (userData.getJoiningDate().equals(LocalDate.now())
						|| userData.getJoiningDate().isBefore(LocalDate.now())) {
					User user = userRepository.findByEmpId(userData.getEmpId());
					user.setDepartment(userData.getNewDepartment());
					user.setDesignation(userData.getNewDesignation());
					user.setOfficeName(userData.getNewOfficeName());
					user.setRole(userData.getNewRole());
					userRepository.save(user);
				}
			}

			userTransferRepo.save(userData);
			return new ResponseEntity<String>("User updated Successfully", HttpStatus.CREATED);
		} catch (Exception e) {
			throw new Exception(e);
		}
	}

	@Override
	public ResponseEntity<String> removeUserImage(String jwt) {
		User user = fetchUser(jwt);
		user.setImgName(null);
		user.setImgType(null);
		user.setImgData(null);

		userRepository.save(user);
		return new ResponseEntity<String>("User updated Successfully", HttpStatus.ACCEPTED);
	}

}
