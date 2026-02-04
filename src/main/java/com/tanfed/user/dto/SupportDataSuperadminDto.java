package com.tanfed.user.dto;

import java.util.List;

import com.tanfed.user.entity.IssueData;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor

public class SupportDataSuperadminDto {

	private List<String> officeList;
	private List<IssueData> tickets;
}
