package org.example.secondhandweb.dto;

import lombok.Data;
import org.example.secondhandweb.model.Advertisement;

import java.util.List;

@Data
public class AdSearchDTO {
    private String id;
    private String title;
    private String description;
    private double price;
    private String city;
    private String categoryId;
    private String ownerUsername;
    private List<String> imageUrls;
    private String status;
    private String rejectReason;

    public AdSearchDTO(Advertisement ad, String ownerUsername) {
        this.id = ad.getId();
        this.title = ad.getTitle();
        this.description = ad.getDescription();
        this.price = ad.getPrice();
        this.city = ad.getCity();
        this.categoryId = ad.getCategoryId();
        this.ownerUsername = ownerUsername;
        this.imageUrls = ad.getImageUrls();
        this.status = ad.getStatus() != null ? ad.getStatus().name() : null;
        // If your Advertisement entity uses getRejectionReasons() (List), change to:
        // this.rejectReason = ad.getRejectionReasons() != null && !ad.getRejectionReasons().isEmpty()
        //     ? ad.getRejectionReasons().get(0) : null;
        this.rejectReason = ad.getRejectionReason();
    }
}