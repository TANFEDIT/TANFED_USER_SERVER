package com.tanfed.user.dto;

import java.util.List;
import java.util.Set;

import com.tanfed.user.entity.User;
import com.tanfed.user.entity.UserTransferData;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserTransfer_PromotionModel {

	private List<String> officeList;
	private Set<String> empIdList;
	private User user;
	private UserTransferData userTransferData;
	private List<UserPersonnelView> userViewData;
	private List<String> deptList;
	private List<String> roleList;
	private List<String> designationList;
}
