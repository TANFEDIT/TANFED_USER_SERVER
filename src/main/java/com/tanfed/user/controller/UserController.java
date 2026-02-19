package com.tanfed.user.controller;

import java.io.IOException;
import java.util.List;

// import org.slf4j.Logger;
// import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.tanfed.user.dto.UserTransfer_PromotionModel;
import com.tanfed.user.entity.User;
import com.tanfed.user.entity.UserTransferData;
import com.tanfed.user.repo.UserRepository;
import com.tanfed.user.request.PasswordData;
import com.tanfed.user.response.UserRegResponseData;
//import com.tanfed.user.service.MailService;
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

//	@Autowired
//	private MailService mailService;

	@GetMapping("/fetchuser")
	public ResponseEntity<User> fetchUserHandler(@RequestHeader("Authorization") String jwt) {
		User fetchedUser = userService.fetchUser(jwt);
		return new ResponseEntity<User>(fetchedUser, HttpStatus.OK);
	}

	@GetMapping("/fetchdataforuserform")
	public UserRegResponseData fetchDataForUserFormHandler(@RequestHeader("Authorization") String jwt,
			@RequestParam String officeName, @RequestParam String department) throws Exception {
		return userService.fetchDataForUserForm(jwt, officeName, department);
	}

	// private Logger logger = LoggerFactory.getLogger(UserController.class);

	@PostMapping("/adduser")
	@PreAuthorize("hasAnyRole('ROLE_SUPERADMIN','ROLE_ESTUSER', 'ROLE_ESTADMIN', 'ROLE_ROADMIN')")
	public ResponseEntity<String> createUserHandler(@RequestBody User user) throws Exception {
		try {

			// String rawPassword = user.getPassword();
			String rawPassword = "Pass@123";
			user.setPassword(passwordEncoder.encode(rawPassword));
			user.setIsBlocked(false);
			userRepository.save(user);

			// mailService.sendmailPassword(user.getEmailId(), rawPassword,
			// user.getEmpId());
			// logger.info("email{}", user.getEmailId());
			return new ResponseEntity<String>("User Registered Successfully", HttpStatus.CREATED);
		} catch (Exception e) {
			throw new Exception("User Registration failed!" + e);
		}
	}

	@PutMapping("/saveuserimage/{empId}")
	public ResponseEntity<String> saveUserImgHandler(@PathVariable String empId, @RequestBody MultipartFile img)
			throws IOException {
		return userService.saveUserImage(empId, img);
	}

	@PutMapping("/removeuserimage")
	public ResponseEntity<String> removeUserImgHandler(@RequestHeader("Authorization") String jwt) throws IOException {
		return userService.removeUserImage(jwt);
	}

	@PostMapping("/signout")
	public ResponseEntity<String> saveUserSessionLogHandler(@RequestHeader("Authorization") String jwt)
			throws Exception {
		return userService.saveUserSessionLog(jwt);
	}

	@PutMapping("/updateuser")
	public ResponseEntity<String> updateUserHandler(@RequestBody User user, @RequestHeader("Authorization") String jwt)
			throws Exception {
		return userService.updateUser(user, jwt);
	}

	@PutMapping("/changepasswordafterauth")
	public ResponseEntity<String> updatePasswordHandler(@RequestBody PasswordData obj) throws Exception {
		return userService.updatePassword(obj);
	}

	@GetMapping("/getusersbyoffice")
	public List<User> fetchUsers(@RequestParam String officeName) throws Exception {
		return userService.fetchUsers(officeName);
	}

	@GetMapping("/validateempid/{empId}")
	public String validateEmpID(@PathVariable String empId) {
		User isUserExist = userRepository.findByEmpId(empId);
		if (isUserExist != null) {
			return new String("EmpId already Exists!");
		}
		return new String("");
	}

	@GetMapping("/getdatafortransfer")
	public UserTransfer_PromotionModel fetchTransferAndPromotionDataHandler(@RequestParam String officeName,
			@RequestParam String natureOfEmployment, @RequestParam String empId,
			@RequestHeader("Authorization") String jwt) throws Exception {
		return userService.fetchTransferAndPromotionData(officeName, empId, jwt, natureOfEmployment);
	}

	@PostMapping("/savetransferdata")
	public ResponseEntity<String> postMethodName(@RequestBody UserTransferData obj) throws Exception {
		return userService.saveUserTransferData(obj);
	}

}
