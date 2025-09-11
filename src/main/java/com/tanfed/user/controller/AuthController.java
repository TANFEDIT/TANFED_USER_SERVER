package com.tanfed.user.controller;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

import javax.security.auth.login.LoginException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.RestController;

import com.tanfed.user.config.JwtProvider;
import com.tanfed.user.config.JwtTokenValidator;
import com.tanfed.user.entity.*;
import com.tanfed.user.repo.*;
import com.tanfed.user.request.LoginRequest;
import com.tanfed.user.response.AuthResponse;
import com.tanfed.user.service.CustomUserServiceImplementation;
import com.tanfed.user.service.MailService;
import com.tanfed.user.service.UserService;
import com.tanfed.user.utils.MailBody;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.GetMapping;

@RestController
@RequestMapping("/auth")
public class AuthController {

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private PasswordEncoder passwordEncoder;

	@Autowired
	private MailService mailService;

	@Autowired
	private OtpRepo otpRepo;

	@Autowired
	private OfficeRepo officeRepo;

	@Autowired
	private UserLogRepo userLogRepo;

	@Autowired
	private UserService userService;

	@Autowired
	private SessionManagerRepo sessionManagerRepo;

	@Autowired
	private CustomUserServiceImplementation customUserServiceImplementation;

	private static Logger logger = LoggerFactory.getLogger(AuthController.class);

//	@PostMapping("/signin")
//	public Mono<Void> signinHandler(ServerHttpResponse res, @RequestBody LoginRequest request) {
//	    return Mono.fromCallable(() -> authenticate(request.getEmpId(), request.getPassword()))
//	        .subscribeOn(Schedulers.boundedElastic())
//	        .flatMap(authentication ->
//	            Mono.fromCallable(() -> sessionManagerRepo.findByEmpId(request.getEmpId()))
//	                .subscribeOn(Schedulers.boundedElastic())
//	                .flatMap(optionalSession -> {
//	                    if (optionalSession.isPresent()) {
//	                        return Mono.error(new LoginException("User already logged in!"));
//	                    }
//
//	                    String jwtToken = JwtProvider.generateToken(authentication);
//	                    LocalDateTime expiry = JwtProvider.getExpiryFromJwt(jwtToken);
//
//	                    Mono<SessionManager> saveSession = Mono.fromCallable(() ->
//	                        sessionManagerRepo.save(new SessionManager(null, request.getEmpId(), jwtToken, expiry))
//	                    ).subscribeOn(Schedulers.boundedElastic());
//
//	                    Mono<UserLog> saveLog = Mono.fromCallable(() ->
//	                        userLogRepo.save(new UserLog(null, LocalDate.now(), request.getEmpId(), LocalDateTime.now().toString(), null))
//	                    ).subscribeOn(Schedulers.boundedElastic());
//
//	                    ResponseCookie cookie = ResponseCookie.from("authToken", jwtToken)
//	                        .httpOnly(true)
//	                        .secure(false)
//	                        .path("/")
//	                        .maxAge(86400)
//	                        .build();
//	                    res.addCookie(cookie);
//
//	                    return Mono.when(saveSession, saveLog).then(); // Mono<Void>
//	                })
//	        )
//	        .onErrorMap(e -> new LoginException("signin failed: " + e.getMessage()));
//	}

	@Autowired
	private SimpMessagingTemplate messagingTemplate;

	@PostMapping("/signin")
	public ResponseEntity<AuthResponse> signinHandler(@RequestBody LoginRequest request) throws Exception {
		try {
			Authentication authentication = authenticate(request.getEmpId(), request.getPassword());
			SecurityContextHolder.getContext().setAuthentication(authentication);

			String jwtToken = JwtProvider.generateToken(authentication);
			LocalDateTime expiryFromJwt = JwtProvider.getExpiryFromJwt(jwtToken);
			String sessionId = UUID.randomUUID().toString();

			if (sessionManagerRepo.findByEmpId(request.getEmpId()).isPresent()) {
				SessionManager sessionManager = sessionManagerRepo.findByEmpId(request.getEmpId()).get();
				if (sessionManager.getSessionId() != null && !sessionManager.getSessionId().equals(sessionId)) {
					messagingTemplate.convertAndSendToUser(request.getEmpId(), "/queue/force-logout", "logout");
					System.out.println("✅ Message sent to : " + request.getEmpId());
					signoutWithoutJwt(request.getEmpId());
				}
			}
			sessionManagerRepo.save(new SessionManager(null, request.getEmpId(), sessionId, jwtToken, expiryFromJwt));

			userLogRepo
					.save(new UserLog(null, LocalDate.now(), request.getEmpId(), LocalDateTime.now().toString(), null));

			User user = userRepository.findByEmpId(request.getEmpId());
			AuthResponse res = new AuthResponse(user.getEmpId(), user.getEmpName(), user.getRole(), user.getOfficeName(),
					user.getDesignation(), jwtToken, user.getImgName(), user.getImgType(), user.getImgData());
			return new ResponseEntity<AuthResponse>(res, HttpStatus.OK);
		} catch (Exception e) {
			throw new LoginException("signin failed " + e);
		}
	}

	@PostMapping("/sendotp/{empId}")
	public ResponseEntity<String> sendOtpHandler(@PathVariable String empId) throws Exception {
		try {
			User user = userRepository.findByEmpId(empId);
			if (user == null) {
				throw new UsernameNotFoundException("User not found with empId :" + empId);
			}
			int otp = ThreadLocalRandom.current().nextInt(100000, 1000000);

			OtpEntity otpEntity = OtpEntity.builder().otp(otp).empId(empId)
					.expiryDate(LocalDateTime.now().plusSeconds(60 * 5)).build();

			otpRepo.save(otpEntity);

			MailBody body = MailBody.builder().to(user.getEmailId()).subject("OTP Verification")
					.text(user.getEmpName() + "Your OTP is" + otp).build();
			ResponseEntity<String> sendMail = mailService.sendMail(body);
			if (sendMail.getStatusCode().equals(HttpStatus.OK)) {
				return new ResponseEntity<String>("mail sent Successfully", HttpStatus.OK);
			} else {
				return new ResponseEntity<String>("Mail did not sent", HttpStatus.EXPECTATION_FAILED);
			}
		} catch (Exception e) {
			throw new Exception("Mail did not sent" + e);
		}
	}

	@PostMapping("/verifyotp/{empId}/{otp}")
	public ResponseEntity<String> verifyOtpHandler(@PathVariable String empId, @PathVariable Integer otp)
			throws Exception {
		try {
			OtpEntity userOtp = otpRepo.findByEmpIdAndOtp(empId, otp);
			if (userOtp == null) {
				return new ResponseEntity<String>("Invalid OTP", HttpStatus.EXPECTATION_FAILED);
			}
			if (userOtp.getExpiryDate().isBefore(LocalDateTime.now())) {
				return new ResponseEntity<String>("OTP expired! Try again", HttpStatus.EXPECTATION_FAILED);
			}
			if (userOtp.getOtp().equals(otp)) {
				otpRepo.deleteById(userOtp.getId());
				return new ResponseEntity<String>("OTP verified Successfully", HttpStatus.OK);
			} else {
				return new ResponseEntity<String>("OTP verification failed!", HttpStatus.EXPECTATION_FAILED);
			}

		} catch (Exception e) {
			throw new Exception(e);
		}
	}

	@PutMapping("/changepassword")
	public ResponseEntity<String> changePasswordHandler(@RequestBody LoginRequest obj) throws Exception {
		try {
			User user = userRepository.findByEmpId(obj.getEmpId());
			if (user == null) {
				throw new Exception("User not found");
			}
			user.setPassword(passwordEncoder.encode(obj.getPassword()));
			userRepository.save(user);
			return new ResponseEntity<String>("Password changed", HttpStatus.ACCEPTED);
		} catch (Exception e) {
			throw new Exception("Password not changed" + e);
		}
	}

	@PostMapping("/signoutwithoutjwt/{empId}")
	public ResponseEntity<String> signoutWithoutJwt(@PathVariable String empId) throws Exception {
		try {
			SessionManager sessionData = sessionManagerRepo.findByEmpId(empId).get();
			logger.info("signoutwithoutjwt{}", sessionData.getJwt());
			if (sessionData.getExpiryDate().isBefore(LocalDateTime.now())) {
				sessionManagerRepo.deleteById(sessionData.getId());
				List<UserLog> byEmpId = userLogRepo.findByEmpId(empId);
				UserLog last = byEmpId.get(byEmpId.size() - 1);

				if (last.getLogoutTime() == null) {
					last.setLogoutTime(LocalDateTime.now().toString());
				}
				userLogRepo.save(last);
				SecurityContextHolder.clearContext();
				return new ResponseEntity<String>("Logged Out!", HttpStatus.OK);
			} else {
				return userService.saveUserSessionLog("Bearer " + sessionData.getJwt());
			}

		} catch (Exception e) {
			throw new Exception(e);
		}
	}

	private Authentication authenticate(String empId, String password) {
		UserDetails userDetails = customUserServiceImplementation.loadUserByUsername(empId);

		if (userDetails == null) {
			throw new BadCredentialsException("No user found");
		}
		if (!passwordEncoder.matches(password, userDetails.getPassword())) {
			throw new BadCredentialsException("Invalid username or password");
		}
		return new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
	}

	@GetMapping("/getofficelist")
	public List<Office> getOfficeList() {
		return officeRepo.findAll();
	}

	@Autowired
	private JwtTokenValidator jwtTokenValidator;

	@GetMapping("/blocklist")
	public List<String> getBLockedJwtList() {
//		logger.info("blocklist {}", jwtTokenValidator.blockList());
		return jwtTokenValidator.blockList();
	}

	@Autowired
	private DesignationRepo designationRepo;

	@GetMapping("/fetchuserdesignation/{empId}")
	public String getNewDesignation(@PathVariable String empId) {
//		fetch user data by empId to get designation
		User byEmpId = userRepository.findByEmpId(empId);
		String designation = byEmpId.getDesignation();

//		initialize hashmap
		HashMap<String, String> designationValue = new HashMap<String, String>();

//		fetch designation list from repo and add to hashmap
		List<Designation> list = designationRepo.findAll();
		for (Designation temp : list) {
			String role = temp.getDesignation();
			String roleCode = temp.getAbbreviation();
			designationValue.put(role, roleCode);
		}

//		compare user designation to hashmap and set new value 
		String newValue = null;
		for (String temp : designationValue.keySet()) {
			if (designation.equals(temp)) {
				return newValue = designationValue.get(temp);
			}
		}
//		return new designation
		return newValue;
	}

	@GetMapping("/test")
	public String getMethodName() {
		return "User Service Depolyed Successfully!";
	}
	
	
}
