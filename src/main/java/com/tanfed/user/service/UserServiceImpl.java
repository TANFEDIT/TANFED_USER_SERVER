package com.tanfed.user.service;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
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
import com.tanfed.user.entity.*;
import com.tanfed.user.repo.*;
import com.tanfed.user.request.PasswordData;
import com.tanfed.user.response.UserRegResponseData;
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
	private DesignationRepo designationRepo;

	@Autowired
	private OfficeRepo officeRepo;

	@Autowired
	private SessionManagerRepo sessionManagerRepo;

	@Override
	public User fetchUser(String jwt) {
		String empId = JwtProvider.getEmailFromJwtToken(jwt);
		return userRepository.findByEmpId(empId);
	}

	@Override
	public UserRegResponseData fetchDataForUserForm(String jwt) throws Exception {
		try {
			UserRegResponseData data = new UserRegResponseData();

			List<Designation> list = designationRepo.findAll();

			Set<String> designationLst = new HashSet<String>();
			Set<String> deptLst = new HashSet<String>();

			list.forEach(temp -> {
				designationLst.add(temp.getDesignation());
				if (!temp.getDepartment().equals("") && !temp.getDepartment().equals("none")) {
					deptLst.add(temp.getDepartment());
				}
			});

			User fetchedUser = fetchUser(jwt);

			data.setRoleLst(getRoleList(fetchedUser.getRole()));
			data.setDeptLst(deptLst);
			data.setDesignationLst(designationLst);
			data.setOfficeNameLst(
					officeRepo.findAll().stream().map(Office::getOfficeName).collect(Collectors.toList()));

			return data;
		} catch (Exception e) {
			throw new Exception(e);
		}

	}

	private List<UserRole> getRoleList(List<UserRole> roles) {
		List<UserRole> data = new ArrayList<>(List.of(UserRole.values()));
		if (roles.size() == 1) {
			if (roles.get(0).equals(UserRole.SUPERADMIN)) {
				return data;
			} else if (roles.get(0).equals(UserRole.ROADMIN)) {
				return data.stream().filter(item -> {
					return item.equals(UserRole.ROUSER);
				}).collect(Collectors.toList());
			} else if (roles.get(0).equals(UserRole.ESTADMIN)) {
				return data.stream().filter(item -> {
					return !item.equals(UserRole.SUPERADMIN);
				}).collect(Collectors.toList());
			} else {
				return null;
			}
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
				userData.setImgName(img.getOriginalFilename());
				userData.setImgType(img.getContentType());
				userData.setImgData(img.getBytes());

				userRepository.save(userData);

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
				last.setLogoutTime(LocalDateTime.now().toString());
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
	public ResponseEntity<String> updateUser(User user) throws Exception {
		try {
			User userData = userRepository.findByEmpId(user.getEmpId());
			if (userData == null) {
				throw new UsernameNotFoundException("User not found with empId :" + user.getEmpId());
			} else {
				userData.setMobileNo1(user.getMobileNo1());
				userData.setMobileNo2(user.getMobileNo2());
				userData.setEmailId(user.getEmailId());
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
			return userRepository.findByOfficeName(officeName);
		} catch (Exception e) {
			throw new Exception(e);
		}
	}

}
