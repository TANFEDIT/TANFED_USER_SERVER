package com.tanfed.user.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/ws")
public class WebSocketHandler {

	@Autowired
	private SimpMessagingTemplate messagingTemplate;

	@GetMapping("/logout/{empId}")
	public ResponseEntity<String> forceLogout(@PathVariable String empId) {
		messagingTemplate.convertAndSendToUser(empId, "/queue/force-logout", "logout");
		return ResponseEntity.ok("Logout message sent");
	}
}
