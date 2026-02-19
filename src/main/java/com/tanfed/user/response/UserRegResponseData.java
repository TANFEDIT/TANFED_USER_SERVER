package com.tanfed.user.response;

import java.util.List;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserRegResponseData {

	private List<String> deptList;
	private List<String> designationList;
	private List<String> officeNameList;
	private List<String> roleList;
}
