package com.tanfed.user.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

	@Autowired
	private WebSocketAuthChannelInterceptorAdapter webSocketAuthChannelInterceptorAdapter;

	@Override
	public void registerStompEndpoints(StompEndpointRegistry registry) {
		registry.addEndpoint("/ws")
				.setAllowedOrigins("http://localhost:5173", "http://192.168.1.171:5173", "http://192.168.1.143:5173",
						"http://192.168.1.143:4173", "http://192.168.1.116:5173", "http://192.168.1.142:5173",
						"https://tanfedit.github.io/", "http://192.168.1.127:5173", "http://192.168.1.158:5173",
						"https://tanfed-user-server-bjc4hyf2fcfqb2c5.centralindia-01.azurewebsites.net")
				.withSockJS();
	}

	@Override
	public void configureMessageBroker(MessageBrokerRegistry registry) {
		registry.enableSimpleBroker("/topic", "/queue");
		registry.setApplicationDestinationPrefixes("/app");
		registry.setUserDestinationPrefix("/user");
	}

	@Override
	public void configureClientInboundChannel(ChannelRegistration registration) {
		registration.interceptors(webSocketAuthChannelInterceptorAdapter);
	}

}
