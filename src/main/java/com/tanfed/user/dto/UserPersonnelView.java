package com.tanfed.user.dto;

import java.time.LocalDate;
import java.util.List;

import com.tanfed.user.utils.UserRole;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserPersonnelView {

	private Long id;
	private LocalDate createdDate;
	private String empId;
	private String empName;
	private String designation;
	private String currentDepartment;
	private String currentOfficeName;
	private List<UserRole> currentRole;
	private String newOfficeName;
	private List<UserRole> newRole;
	private String newDepartment;
	private String personnelType;
	private String extensionFor;
}
