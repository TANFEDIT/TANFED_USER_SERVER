package com.tanfed.user.service;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;

import com.tanfed.user.dto.OfficeInfo;

@FeignClient(name = "BASICINFO-SERVICE", url = "${SHARED_API_URL}")
public interface MasterService {

	@GetMapping("/api/basic-info/fetchofficedata")
	public OfficeInfo getOfficeInfoByOfficeNameHandler(@RequestParam String officeName,
			@RequestHeader("Authorization") String jwt) throws Exception;

}
