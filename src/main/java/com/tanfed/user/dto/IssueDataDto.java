package com.tanfed.user.dto;

import java.time.LocalDate;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class IssueDataDto {

	private Long id;
	private String officeName;
	private String empId;
	private String email;
	private String issue;
	private String status;
	private String response;
	private String issueId;
	private LocalDate date;
	private LocalDate solvedDate;
	private String fileName;
}
