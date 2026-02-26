package com.tanfed.user.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.tanfed.user.dto.OfficeHeader;
import com.tanfed.user.service.AdditionalChargeService;

@RestController
@RequestMapping("/api/user/ac")
public class AdditionalChargeHandler {

	@Autowired
	private AdditionalChargeService additionalChargeService;

	@GetMapping("/getAddtionalchargeinfo")
	public OfficeHeader getAddtionalChargeInfoHandler(@RequestParam String officeName,
			@RequestHeader("Authorization") String jwt) {
		return additionalChargeService.getAddtionalChargeInfo(officeName, jwt);
	}

}
