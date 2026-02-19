package com.tanfed.user.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import com.tanfed.user.utils.MailBody;

@Service
public class MailService {

	@Autowired
	private JavaMailSender mailSender;

	private Logger logger = LoggerFactory.getLogger(MailService.class);

	public ResponseEntity<String> sendmailPassword(String to, String password, String empId) throws Exception {
		try {
			logger.info("sendmailPassword{}", to);
			MailBody body = MailBody.builder().to(to).subject("Tanfed User Credentials!")
					.text("Your EmployeeID is : " + empId + "\n Your Password is : " + password
							+ "\nNote: Change Password after login. DO NOT SHARE PASSWORD!")
					.build();

			sendMail(body);
			return new ResponseEntity<String>("mail sent Successfully", HttpStatus.OK);
		} catch (Exception e) {
			throw new Exception("Mail did not sent(sendmailPassword)" + e);
		}
	}

	@Autowired
	private Environment env;

	public ResponseEntity<String> sendMail(MailBody body) throws Exception {
		try {
			logger.info("sendMail{}", body);
			String email = env.getProperty("EMAIL");
			SimpleMailMessage simpleMailMessage = new SimpleMailMessage();
			simpleMailMessage.setFrom(email);
			simpleMailMessage.setTo(body.to());
			simpleMailMessage.setSubject(body.subject());
			simpleMailMessage.setText(body.text());
			mailSender.send(simpleMailMessage);
			return new ResponseEntity<String>("mail sent Successfully", HttpStatus.OK);
		} catch (Exception e) {
			throw new Exception("Mail did not sent (sendMail)" + e);
		}

	}
}
