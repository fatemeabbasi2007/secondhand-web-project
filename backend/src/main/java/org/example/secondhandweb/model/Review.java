package org.example.secondhandweb.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "reviews")
public class Review {
    @Id
    @Column(name = "id", length = 72, nullable = false)
    private String id;

    @Column(name = "reviewer_id", nullable = false)
    private String reviewerId;

    @Column(name = "seller_id", nullable = false)
    private String sellerId;

    @Column(name = "advertisement_id", nullable = false)
    private String advertisementId;

    @Column(nullable = false)
    private Integer score;

    @Column(length = 2000)
    private String comment;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();
}