package org.example.secondhandweb.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Embeddable

public class Message {
    @Column(name = "sender_id", nullable = false)
    private String senderId;       // User ID of who wrote the message
    @Column(name = "content", nullable = false, length = 2000)
    private String content;        // The actual text payload
    @Column(name = "sent_at", nullable = false)
    private LocalDateTime sentAt = LocalDateTime.now(); //
}
