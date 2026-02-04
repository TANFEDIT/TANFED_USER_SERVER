package com.tanfed.user.entity;

import java.time.LocalDate;
import java.util.List;

import com.tanfed.user.utils.UserRole;
import com.tanfed.user.utils.UserRoleConverter;

import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table
public class UserTransferData {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private String transferType;

	private String empId;

	private String currentDepartment;

	private String currentDesignation;

	private String currentOfficeName;

	@Convert(converter = UserRoleConverter.class)
	private List<UserRole> currentRole;

	private String newDepartment;

	private String newDesignation;

	private String newOfficeName;

	@Convert(converter = UserRoleConverter.class)
	private List<UserRole> newRole;

	private String reason;

	private LocalDate rcDate;

	private LocalDate fromDate;

	private LocalDate toDate;

	private LocalDate relievedDate;

	private LocalDate joiningDate;

	private String rcNo;

}
