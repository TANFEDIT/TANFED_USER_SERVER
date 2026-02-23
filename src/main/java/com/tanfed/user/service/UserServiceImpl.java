package com.tanfed.user.service;

import java.io.IOException;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.tanfed.user.config.JwtProvider;
import com.tanfed.user.config.JwtTokenValidator;
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
	private UserTransferRepo userTransferRepo;

	@Autowired
	private SessionManagerRepo sessionManagerRepo;

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
			String natureOfEmployment) throws Exception {
		UserTransfer_PromotionModel res = new UserTransfer_PromotionModel();
		List<Office> office = officeRepo.findAll();
//		User fetchedUser = fetchUser(jwt);
//		res.setRoleList(getRoleList(fetchedUser.getRole(), officeName));
		res.setDeptList(Arrays.asList(DesignationAndDept.department));
		res.setOfficeList(office.stream().map(i -> i.getOfficeName()).collect(Collectors.toList()));
		if (officeName != null && !officeName.isEmpty()) {
			res.setEmpIdList(fetchUsers(officeName).stream().map(i -> i.getEmpId()).collect(Collectors.toList()));
			if (empId != null && !empId.isEmpty()) {
				res.setUser(userRepository.findByEmpId(empId));
				res.setUserTransferData(userTransferRepo.findByEmpId(empId).stream()
						.filter(i -> i.getPersonnelType().equals("transfer")).reduce((first, second) -> second)
						.orElse(null));
			}
		}
		return res;
	}

	@Override
	public ResponseEntity<String> saveUserTransferData(UserTransferData obj) throws Exception {
		try {
			if (obj.getRelievedDate() != null) {
				User user = userRepository.findByEmpId(obj.getEmpId());
				obj.setCurrentDepartment(user.getDepartment());
				obj.setCurrentDesignation(user.getDesignation());
				obj.setCurrentOfficeName(user.getOfficeName());
				obj.setCurrentRole(user.getRole());
			}
			userTransferRepo.save(obj);
			return new ResponseEntity<String>("User updated Successfully", HttpStatus.ACCEPTED);
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
