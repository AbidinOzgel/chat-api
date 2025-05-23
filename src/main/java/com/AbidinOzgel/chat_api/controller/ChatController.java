package com.AbidinOzgel.chat_api.controller;

import com.AbidinOzgel.chat_api.dto.MessageDto;
import com.AbidinOzgel.chat_api.dto.UserDto;
import com.AbidinOzgel.chat_api.model.User;
import com.AbidinOzgel.chat_api.service.MessageService;
import com.AbidinOzgel.chat_api.service.UserService;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;

import java.security.Principal;
import java.util.List;

@Controller
public class ChatController {

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @Autowired
    private MessageService messageService;

    @Autowired
    private UserService userService;

    @MessageMapping("/chat")
    public void processMessage(@Payload ChatMessage chatMessage, Principal principal) {
        if (principal == null) {
            throw new RuntimeException("Kimlik doğrulaması gerekiyor");
        }
        
        try {
            User sender = userService.findByUsername(principal.getName())
                    .orElseThrow(() -> new RuntimeException("Kullanıcı bulunamadı"));
            
            MessageDto savedMessage = messageService.saveMessage(
                    sender.getId(),
                    chatMessage.getReceiverId(),
                    chatMessage.getContent()
            );
            

            messagingTemplate.convertAndSendToUser(
                    chatMessage.getReceiverName(),
                    "/queue/messages",
                    savedMessage
            );
            

            messagingTemplate.convertAndSendToUser(
                    sender.getUsername(),
                    "/queue/messages",
                    savedMessage
            );
            

            System.out.println("Mesaj gönderildi: " + sender.getUsername() + " -> " + chatMessage.getReceiverName());
        } catch (Exception e) {
            System.err.println("Mesaj işleme hatası: " + e.getMessage());
            throw e;
        }
    }

    @GetMapping("/api/users")
    @ResponseBody
    public ResponseEntity<List<UserDto>> getUsers(Authentication authentication) {
        List<UserDto> users = userService.getAllUsersExcept(authentication.getName());
        return ResponseEntity.ok(users);
    }

    @GetMapping("/api/messages/{userId}")
    @ResponseBody
    public ResponseEntity<List<MessageDto>> getMessages(@PathVariable Long userId, Authentication authentication) {
        User currentUser = userService.findByUsername(authentication.getName())
                .orElseThrow(() -> new RuntimeException("Kullanıcı bulunamadı"));
        
        List<MessageDto> messages = messageService.getChatMessages(currentUser.getId(), userId);
        return ResponseEntity.ok(messages);
    }

    @Data
    private static class ChatMessage {
        private Long receiverId;
        private String receiverName;
        private String content;
        private String senderName;
    }
}
