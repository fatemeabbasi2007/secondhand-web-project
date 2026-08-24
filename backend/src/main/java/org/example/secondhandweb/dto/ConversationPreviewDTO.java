package org.example.secondhandweb.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ConversationPreviewDTO {
    private String conversationId;
    private String advertisementId;
    private String otherPartyName; // نام طرف مقابل (خریدار یا فروشنده)
    private String title;
    private String lastMessagePreview;
    private LocalDateTime lastMessageAt;
}
