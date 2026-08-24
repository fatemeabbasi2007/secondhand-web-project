package org.example.secondhandweb.model;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.Data;
import org.example.secondhandweb.myEnum.AdStatus;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
@JsonIgnoreProperties(ignoreUnknown = true) // 👈 این آنوتیشن را اضافه کنید
@Entity
@Table(name = "advertisements")
public class Advertisement {
    @Id
    @Column(name = "id", length = 36, nullable = false)
    private String id;               // Unique UUID string
    @Column(nullable = false)
    private String title;            // Searched by keyword
    @Column(nullable = false, length = 2000)
    private String description;      // Searched by keyword
    @Column(nullable = false)
    private double price;            // Filtered by min/max price, sortable
    @Column(nullable = false)
    private String city;             // Filtered by city

    @JsonAlias("category") // 👈 این خط را اضافه کنید
    @Column(name = "category_id", nullable = false)
    private String categoryId;       // e.g., "ELECTRONICS", "VEHICLES"
    @Column(name = "owner_id", nullable = false)
    private String ownerId;          // Link to the User who created i// t
    @Column(name = "rejection_reason")
    private String rejectionReason;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AdStatus status = AdStatus.PENDING_REVIEW;

    @ElementCollection
    @CollectionTable(name = "ad_images", joinColumns = @JoinColumn(name = "ad_id"))
    @Column(name = "image_url" , length = 1000)
    private List<String> imageUrls = new ArrayList<>();
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    // MUST match a valid subcategory ID from your Category list (e.g., "LAPTOPS")
    @ElementCollection
    @CollectionTable(name = "ad_attributes", joinColumns = @JoinColumn(name = "ad_id"))
    @MapKeyColumn(name = "attr_key")
    @Column(name = "attr_value")
    private Map<String, String> specificAttributes = new HashMap<>();
    public void addRejectionReason(String s){
        this.rejectionReason = s;
    }

}
