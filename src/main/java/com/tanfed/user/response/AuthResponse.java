package com.tanfed.user.response;

import java.util.List;
import java.util.Set;

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
	private List<UserRole> role;
	private String tanNo;
	private String officeType;
	private String door;
	private String street;
	private String district;
	private Integer pincode;
	private String officeName;
	private Set<String> additionalOfficeName;
	private String designation;
	private String token;
	private String imgName;
	private String imgType;
	private byte[] imgData;
}
