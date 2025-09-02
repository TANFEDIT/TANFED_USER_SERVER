package com.tanfed.user.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PasswordData {

	private String empId;
	private String oldPassword;
	private String newPassword;
}
