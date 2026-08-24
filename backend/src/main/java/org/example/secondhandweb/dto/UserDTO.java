package org.example.secondhandweb.dto;

import java.util.List;

public class UserDTO {

    private String id;
    private String username;
    private String email;
    private String phoneNum;
    private String fullName;
    private String role;
    private boolean enabled;
    private List<String> favoriteAdIds;
    private double averageRating;
    private int totalRatingsCount;

    // Constructors
    public UserDTO() {}

    public UserDTO(String id, String username, String email, String phoneNum,
                   String fullName, String role, boolean enabled,
                   List<String> favoriteAdIds, double averageRating, int totalRatingsCount) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.phoneNum = phoneNum;
        this.fullName = fullName;
        this.role = role;
        this.enabled = enabled;
        this.favoriteAdIds = favoriteAdIds;
        this.averageRating = averageRating;
        this.totalRatingsCount = totalRatingsCount;
    }

    // Getters & Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPhoneNum() { return phoneNum; }
    public void setPhoneNum(String phoneNum) { this.phoneNum = phoneNum; }

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    public boolean isEnabled() { return enabled; }
    public void setEnabled(boolean enabled) { this.enabled = enabled; }

    public List<String> getFavoriteAdIds() { return favoriteAdIds; }
    public void setFavoriteAdIds(List<String> favoriteAdIds) { this.favoriteAdIds = favoriteAdIds; }

    public double getAverageRating() { return averageRating; }
    public void setAverageRating(double averageRating) { this.averageRating = averageRating; }

    public int getTotalRatingsCount() { return totalRatingsCount; }
    public void setTotalRatingsCount(int totalRatingsCount) { this.totalRatingsCount = totalRatingsCount; }
}