package com.tanfed.user.dto;

import java.util.List;
import java.util.Set;

import com.tanfed.user.entity.User;
import com.tanfed.user.entity.UserTransferData;
import com.tanfed.user.utils.UserRole;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserTransfer_PromotionModel {

	private List<String> officeList;
	private List<String> empIdList;
	private User user;
	private UserTransferData userTransferData;
	private Set<String> deptLst;
	private List<UserRole> roleLst;
}
