package com.tanfed.user.response;

import java.util.List;

import com.tanfed.user.utils.UserRole;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AuthResponse {

	private String empId;
	private String name;
	private List<List<UserRole>> role;
	private String officeName;
	private List<String> additionalOfficeName;
	private String designation;
	private String token;
	private String imgName;
	private String imgType;
	private byte[] imgData;
}
