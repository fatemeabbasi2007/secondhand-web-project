package org.example.secondhandweb.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import org.example.secondhandweb.model.Advertisement;

import java.util.List;

@Data
public class AdminPendingAdDTO {
    private String id;
    private String title;
    private String description;
    private double price;

    @JsonProperty("city")
    private String cityName;

    @JsonProperty("category")
    private String categoryId;

    private String ownerId;
    private String ownerUsername;
    private String status;

    @JsonProperty("imageUrl")
    private List<String> imageUrls;

    public AdminPendingAdDTO(Advertisement ad, String ownerUsername) {
        this.id = ad.getId();
        this.title = ad.getTitle();
        this.description = ad.getDescription();
        this.price = ad.getPrice();
        this.imageUrls = ad.getImageUrls();
        this.cityName = ad.getCity();
        this.categoryId = ad.getCategoryId();
        this.ownerId = ad.getOwnerId();
        this.ownerUsername = ownerUsername;
        this.status = ad.getStatus() != null ? ad.getStatus().name() : null;
    }
}