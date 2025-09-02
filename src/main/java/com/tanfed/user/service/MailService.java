package com.tanfed.user.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
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
			MailBody body = MailBody.builder().to(to).subject("Tanfed user password")
					.text("Your EmployeeID is : " + empId + "\n Your Password is : " + password).build();

			sendMail(body);
			return new ResponseEntity<String>("mail sent Successfully", HttpStatus.OK);
		} catch (Exception e) {
			throw new Exception("Mail did not sent(sendmailPassword)" + e);
		}
	}

	public ResponseEntity<String> sendMail(MailBody body) throws Exception {
		try {
			logger.info("sendMail{}", body);
			SimpleMailMessage simpleMailMessage = new SimpleMailMessage();
			simpleMailMessage.setFrom("tanfeditteam@gmail.com");
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
