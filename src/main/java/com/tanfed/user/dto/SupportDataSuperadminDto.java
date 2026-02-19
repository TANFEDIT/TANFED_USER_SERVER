package com.tanfed.user.dto;

import java.util.List;
import java.util.Set;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor

public class SupportDataSuperadminDto {

	private Set<String> officeList;
	private List<IssueDataDto> tickets;
}
