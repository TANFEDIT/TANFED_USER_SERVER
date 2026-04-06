package com.tanfed.user.dto;

import java.util.List;

import com.tanfed.user.utils.UserRole;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OfficeHeader {

	private String empId;
	private List<UserRole> role;
	private String name;
	private String designation;
	
	
	private String tanNo;
	private String officeName;
	private String officeType;
	private String door;
	private String street;
	private String district;
	private Integer pincode;
	
}
