package org.example.secondhandweb.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
//@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "conversations")
public class Conversation {
    @Id
    @Column(name = "id", length = 255, nullable = false)
    private String id; //  "buyerId_advertisementId"
    @ElementCollection
    @CollectionTable(
            name = "conversation_messages",
            joinColumns = @JoinColumn(name = "conversation_id")
    )
    @OrderColumn(name = "msg_order")
    private List<Message> messages = new ArrayList<>();

    @Column(name = "advertisement_id", nullable = false)
    private String advertisementId;
    @Column(name = "seller_id", nullable = false)
    private String sellerId;
    @Column(name = "buyer_id", nullable = false)
    private String buyerId;

    public Conversation(String id , String advertisementId , String sellerId , String buyerId) {
        this.id = id;
        this.advertisementId = advertisementId;
        this.sellerId = sellerId;
        this.buyerId = buyerId;
    }

    @Column(name = "last_message_at")
    private LocalDateTime lastMessageAt = LocalDateTime.now();
    @Column(name = "last_message_preview")
    private String lastMessagePreview = "";
    public void addMessageToList(Message m) {
        if (this.messages == null) {
            this.messages = new ArrayList<>();
        }
        messages.add(m);
    }

}
