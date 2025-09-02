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
public class UserRegResponseData {

	private Set<String> deptLst;
	private Set<String> designationLst;
	private List<String> officeNameLst;
	private List<UserRole> roleLst;
}
