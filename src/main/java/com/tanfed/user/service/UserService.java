package com.tanfed.user.service;


import java.io.IOException;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import com.tanfed.user.dto.UserTransfer_PromotionModel;
import com.tanfed.user.entity.User;
import com.tanfed.user.request.PasswordData;
import com.tanfed.user.response.UserRegResponseData;

public interface UserService {
	
//	GET METHODS
	public User fetchUser(String jwt);
	public UserRegResponseData fetchDataForUserForm(String jwt) throws Exception;
	public List<User> fetchUsers(String officeName) throws Exception;
	public UserTransfer_PromotionModel fetchTransferAndPromotionData(String officeName, String empId, String jwt) throws Exception;
	
//	POST METHODS
	public ResponseEntity<String> saveUserImage(String empId, MultipartFile img) throws IOException;
	public ResponseEntity<String> saveUserSessionLog(String jwt) throws Exception;
	
//	PUT METHODS
	public ResponseEntity<String> updateUser(User user) throws Exception;
	public ResponseEntity<String> updatePassword(PasswordData obj) throws Exception;
}
