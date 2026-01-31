package com.tanfed.user.entity;

import java.time.LocalDate;
import java.util.List;

import com.tanfed.user.utils.UserRole;
import com.tanfed.user.utils.UserRoleConverter;

import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name="users")

public class User {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@Column(unique = true)
	private String empId;
	
	private String empName;
	private String natureOfEmployment;
	private String designation;
	private String gender;
	private LocalDate dob;
	private LocalDate doj;
	private LocalDate deputationjoinDate;
	private LocalDate joiningDate;
	private LocalDate dor;
	private String officeName;
	private String department;
	private String rcNo;
	private LocalDate rcDate;
	private String goNo;
	private LocalDate goDate;
	private Long mobileNo1; 
	private Long mobileNo2;
	private String emailId;
	
	@Column(name="aadhar_no", unique = true)
	private Long aadharNo;
	
	@Column(unique = true)
	private String panNo;
	
	private String password;
	
	@Convert(converter = UserRoleConverter.class)
	private List<UserRole> role;
	
	private String currentAddress;
	
	private String permanentAddress;
	
	private String state;

	private Boolean sameAsChecked;
	
	private String imgName;
	
	private String imgType;
	
	@Lob
	@Column(length = 1000000)
	private byte[] imgData;
	
	
}
