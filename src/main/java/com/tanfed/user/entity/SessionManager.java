package com.tanfed.user.entity;


import java.time.LocalDateTime;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table
public class SessionManager {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@Column
	private String empId;
	
	@Column
	private String sessionId;
	
	@Column(length = 1000)
	private String jwt;
	
	@Column
	private LocalDateTime expiryDate;
}
