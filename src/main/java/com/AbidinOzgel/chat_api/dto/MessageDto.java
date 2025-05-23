package com.AbidinOzgel.chat_api.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MessageDto {
    private Long id;
    private String content;
    private Long senderId;
    private String senderUsername;
    private Long receiverId;
    private String receiverUsername;
    private LocalDateTime timestamp;
}
