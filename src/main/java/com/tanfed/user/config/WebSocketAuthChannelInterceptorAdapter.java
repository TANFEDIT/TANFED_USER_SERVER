package com.tanfed.user.config;

import java.security.Principal;
import java.util.List;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

@Configuration
public class WebSocketAuthChannelInterceptorAdapter implements ChannelInterceptor {

	@Override
	public Message<?> preSend(Message<?> message, MessageChannel channel) {
		StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);
		if (accessor != null) {
			if (StompCommand.CONNECT.equals(accessor.getCommand())) {
				String token = accessor.getFirstNativeHeader("Authorization");
				if (token != null) {
					String username = JwtProvider.getEmailFromJwtToken(token);
					String roles = JwtProvider.getRolesFromJwt(token);
					Principal userPrincipal = new UsernamePasswordAuthenticationToken(username, null,
							List.of(new SimpleGrantedAuthority(roles)));
					accessor.setUser(userPrincipal);
					System.out.println("✅ WebSocket connected with empId: " + username);

				}
			}
		}
		return message;
	}

}
