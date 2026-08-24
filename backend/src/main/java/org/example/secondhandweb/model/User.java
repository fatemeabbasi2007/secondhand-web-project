package org.example.secondhandweb.model;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "users")
public class User {
    @Id
    @Column(name = "id", updatable = false, nullable = false, length = 36)
    private String id;               // Unique UUID string
    @Column(name = "username", nullable = false, unique = true, length = 50)
    private String username;
    @Column(name = "password", nullable = false, length = 255)
    private String password;
    @Column(name = "email", unique = true, length = 100)
    private String email;

    @JsonProperty("phoneNum")
    @JsonAlias({"phone", "phoneNumber", "phone_num", "mobile"})
    @Column(name = "phone_num", length = 20)
    private String phoneNum;

    @JsonProperty("fullName")
    @JsonAlias({"name", "fullName", "full_name"})
    @Column(name = "full_name", length = 100)
    private String fullName;

    @Column(name = "role", nullable = false, length = 20)
    private String role= "USER";

    @Column(name = "enabled", nullable = false)
    private Boolean enabled = true;

    public boolean isEnabled(){
        if (enabled){
            return true;
        }
        else {
            return false;
        }
    }
    @ElementCollection
    @CollectionTable(
            name = "user_favorites",
            joinColumns = @JoinColumn(name = "user_id")
    )
    @Column(name = "ad_id", length = 36)
    private List<String> favoriteAdIds = new ArrayList<>();

    @Column(name = "average_rating", nullable = false)
    private Double averageRating = 0.0;
    @Column(name = "total_ratings_count", nullable = false)
    private Integer totalRatingsCount = 0;
    public void addFavoriteToList(String id){
        favoriteAdIds.add(id);
    }

    @PrePersist
    public void prePersist() {
        if (this.id == null) {
            this.id = java.util.UUID.randomUUID().toString();
        }
        if (this.averageRating == null) {
            this.averageRating = 0.0;
        }
        if (this.totalRatingsCount == null) {
            this.totalRatingsCount = 0;
        }
        if (this.enabled == null) {
            this.enabled = true;
        }
        if (this.role == null) {
            this.role = "USER";
        }
    }

}
