package com.tanfed.user.service;

import java.util.Arrays;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.tanfed.user.dto.OfficeHeader;
import com.tanfed.user.dto.OfficeInfo;
import com.tanfed.user.entity.User;

@Service
public class AdditionalChargeService {

	@Autowired
	private UserService userService;
	
	@Autowired
	private MasterService masterService;
	
	public OfficeHeader getAddtionalChargeInfo(String officeName, String jwt) {
		User user = userService.fetchUser(jwt);
		OfficeInfo officeInfo;
		try {
			officeInfo = masterService.getOfficeInfoByOfficeNameHandler(officeName, jwt);
			return new OfficeHeader(user.getEmpId(), Arrays.asList("ROADMIN"), user.getEmpName(), user.getDesignation(),
					officeInfo.getTanNo(), officeInfo.getOfficeName(), officeInfo.getOfficeType(), officeInfo.getDoor(),
					officeInfo.getStreet(), officeInfo.getDistrict(), officeInfo.getPincode());
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

}
