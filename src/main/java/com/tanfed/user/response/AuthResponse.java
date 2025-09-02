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

	private List<UserRole> role;
	private String officeName;
	private String officeType;
	private String designation;
	private String name;
	
	private int door;
	private String street;
	private String district;
	private int pincode;
	
	private String jwt;
	private String message;
	private Boolean status;
}
