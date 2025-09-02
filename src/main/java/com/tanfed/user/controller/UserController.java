package com.tanfed.user.controller;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.tanfed.user.entity.User;
import com.tanfed.user.repo.UserRepository;
import com.tanfed.user.request.PasswordData;
import com.tanfed.user.response.UserRegResponseData;
import com.tanfed.user.service.MailService;
import com.tanfed.user.service.UserService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.PathVariable;

@RestController
@RequestMapping("/api/user")
public class UserController {

	@Autowired
	private UserService userService;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private PasswordEncoder passwordEncoder;

	@Autowired
	private MailService mailService;

	@GetMapping("/fetchuser")
	public ResponseEntity<User> fetchUserHandler(@RequestHeader("Authorization") String jwt) {
		User fetchedUser = userService.fetchUser(jwt);
		return new ResponseEntity<User>(fetchedUser, HttpStatus.OK);
	}

	@GetMapping("/fetchdataforuserform")
	@PreAuthorize("hasAnyRole('ROLE_SUPERADMIN','ROLE_ESTUSER', 'ROLE_ESTADMIN', 'ROLE_ROADMIN')")
	public UserRegResponseData fetchDataForUserFormHandler(@RequestHeader("Authorization") String jwt)
			throws Exception {
		return userService.fetchDataForUserForm(jwt);
	}

	private Logger logger = LoggerFactory.getLogger(UserController.class);

	@PostMapping("/adduser")
	@PreAuthorize("hasAnyRole('ROLE_SUPERADMIN','ROLE_ESTUSER', 'ROLE_ESTADMIN', 'ROLE_ROADMIN')")
	public ResponseEntity<String> createUserHandler(@RequestBody User user) throws Exception {
		try {
			User isUserExist = userRepository.findByEmpId(user.getEmpId());
			if (isUserExist != null) {
				throw new Exception("EmpId already Exists!");
			}
			String rawPassword = user.getPassword();
			user.setPassword(passwordEncoder.encode(rawPassword));
			userRepository.save(user);

			mailService.sendmailPassword(user.getEmailId(), rawPassword, user.getEmpId());
			logger.info("email{}", user.getEmailId());
			return new ResponseEntity<String>("User Registered Successfully \nPassword sent to registered email",
					HttpStatus.CREATED);
		} catch (Exception e) {
			throw new Exception("User Registration failed!" + e);
		}
	}

	@PutMapping("/saveuserimage/{empId}")
	public ResponseEntity<String> saveUserImgHandler(@PathVariable String empId, @RequestBody MultipartFile img)
			throws IOException {
		return userService.saveUserImage(empId, img);
	}

	@PostMapping("/signout")
	public ResponseEntity<String> saveUserSessionLogHandler(@RequestHeader("Authorization") String jwt)
			throws Exception {
		return userService.saveUserSessionLog(jwt);
	}

	@PutMapping("/updateuser")
	public ResponseEntity<String> updateUserHandler(@RequestBody User user) throws Exception {
		return userService.updateUser(user);
	}

	@PutMapping("/changepasswordafterauth")
	public ResponseEntity<String> updatePasswordHandler(@RequestBody PasswordData obj) throws Exception {
		return userService.updatePassword(obj);
	}

}
