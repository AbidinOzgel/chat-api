package com.AbidinOzgel.chat_api.service;

import com.AbidinOzgel.chat_api.dto.MessageDto;
import com.AbidinOzgel.chat_api.model.Message;
import com.AbidinOzgel.chat_api.model.User;
import com.AbidinOzgel.chat_api.repository.MessageRepository;
import com.AbidinOzgel.chat_api.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class MessageService {

    @Autowired
    private MessageRepository messageRepository;

    @Autowired
    private UserRepository userRepository;

    @Transactional
    public MessageDto saveMessage(Long senderId, Long receiverId, String content) {
        User sender = userRepository.findById(senderId)
                .orElseThrow(() -> new RuntimeException("Gönderen kullanıcı bulunamadı"));
        User receiver = userRepository.findById(receiverId)
                .orElseThrow(() -> new RuntimeException("Alıcı kullanıcı bulunamadı"));

        Message message = new Message();
        message.setSender(sender);
        message.setReceiver(receiver);
        message.setContent(content);
        message.setTimestamp(LocalDateTime.now());

        message = messageRepository.save(message);
        
        return convertToDto(message);
    }

    public List<MessageDto> getChatMessages(Long userId1, Long userId2) {
        User user1 = userRepository.findById(userId1)
                .orElseThrow(() -> new RuntimeException("Kullanıcı bulunamadı"));
        User user2 = userRepository.findById(userId2)
                .orElseThrow(() -> new RuntimeException("Kullanıcı bulunamadı"));

        List<Message> messages = messageRepository.findChatMessages(user1, user2);
        
        return messages.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    private MessageDto convertToDto(Message message) {
        MessageDto dto = new MessageDto();
        dto.setId(message.getId());
        dto.setContent(message.getContent());
        dto.setSenderId(message.getSender().getId());
        dto.setSenderUsername(message.getSender().getUsername());
        dto.setReceiverId(message.getReceiver().getId());
        dto.setReceiverUsername(message.getReceiver().getUsername());
        dto.setTimestamp(message.getTimestamp());
        return dto;
    }
}
