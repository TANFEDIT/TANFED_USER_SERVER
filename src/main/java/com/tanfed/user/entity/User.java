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
	
	@Column
	private String empName;
	
	@Column
	private String designation;
	
	@Column
	private LocalDate dob;
	
	@Column
	private LocalDate doj;
	
	@Column
	private LocalDate joiningDate;
	
	@Column
	private LocalDate dor;
	
	@Column
	private String officeName;
	
	@Column
	private String department;
	
	@Column
	private String rcNo;
	
	@Column
	private LocalDate date;
	
	@Column
	private Long mobileNo1; 
	
	@Column
	private Long mobileNo2;
	
	@Column
	private String emailId;
	
	@Column(name="aadhar_no", unique = true)
	private Long aadharNo;
	
	@Column(unique = true)
	private String panNo;
	
	@Column
	private String password;
	
	@Convert(converter = UserRoleConverter.class)
	private List<UserRole> role;
	
	@Column
	private String currentAddress;
	
	@Column
	private String permanentAddress;
	
	@Column
	private String state;

	@Column
	private Boolean sameAsChecked;
	
	@Column
	private String imgName;
	
	@Column
	private String imgType;
	
	@Lob
	@Column(length = 1000000)
	private byte[] imgData;
	
	
}
