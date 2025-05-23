package com.AbidinOzgel.chat_api.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.SimpMessageType;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.config.annotation.web.messaging.MessageSecurityMetadataSourceRegistry;
import org.springframework.security.config.annotation.web.socket.AbstractSecurityWebSocketMessageBrokerConfigurer;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

import com.AbidinOzgel.chat_api.security.jwt.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import com.AbidinOzgel.chat_api.service.UserDetailsServiceImpl;

import java.util.List;

@Configuration
@Order(Ordered.HIGHEST_PRECEDENCE + 99)
public class WebSocketSecurityConfig implements WebSocketMessageBrokerConfigurer {

    @Autowired
    private JwtUtils jwtUtils;
    
    @Autowired
    private UserDetailsServiceImpl userDetailsService;

    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {
        registration.interceptors(new ChannelInterceptor() {
            @Override
            public Message<?> preSend(Message<?> message, MessageChannel channel) {
                StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);
                
                if (accessor != null && StompCommand.CONNECT.equals(accessor.getCommand())) {
                    // CONNECT komutunda, Authorization header'ından JWT token'ı çıkar
                    List<String> authorization = accessor.getNativeHeader("Authorization");
                    
                    if (authorization != null && !authorization.isEmpty()) {
                        String fullToken = authorization.get(0);
                        String token = fullToken;
                        
                        // "Bearer " ön ekini kaldır (eğer varsa)
                        if (fullToken.startsWith("Bearer ")) {
                            token = fullToken.substring(7);
                        }
                        
                        try {
                            if (jwtUtils.validateJwtToken(token)) {
                                String username = jwtUtils.getUserNameFromJwtToken(token);
                                UserDetails userDetails = userDetailsService.loadUserByUsername(username);
                                
                                UsernamePasswordAuthenticationToken auth = 
                                        new UsernamePasswordAuthenticationToken(
                                                userDetails, null, userDetails.getAuthorities());
                                
                                // Authentication'ı StompHeaderAccessor'a ekle
                                accessor.setUser(auth);
                                
                                // Mevcut SecurityContext'e de ekle
                                SecurityContextHolder.getContext().setAuthentication(auth);
                            }
                        } catch (Exception e) {
                            // Log token doğrulama hatası
                            System.err.println("WebSocket token doğrulama hatası: " + e.getMessage());
                        }
                    }
                }
                return message;
            }
        });
    }
}
