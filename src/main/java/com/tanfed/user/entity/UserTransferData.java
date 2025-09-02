package com.tanfed.user.entity;

import java.time.LocalDate;

import com.tanfed.user.utils.UserRole;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
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
	
	@Column
	private String empId;
	
	@Column
	private String empName;
	
	@Column
	private String officeName;
	
	@Enumerated(EnumType.STRING)
	private UserRole role;
	
	@Column
	private LocalDate date;
	
	@Column
	private String rcNo;
	
}
