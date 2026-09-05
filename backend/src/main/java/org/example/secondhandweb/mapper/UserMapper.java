package org.example.secondhandweb.mapper;

import org.example.secondhandweb.dto.UserDTO;
import org.example.secondhandweb.model.User;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {

    public UserDTO toDTO(User user) {
        return new UserDTO(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getPhoneNum(),
                user.getFullName(),
                user.getRole(),
                user.isEnabled(),
                user.getFavoriteAdIds(),
                user.getAverageRating(),
                user.getTotalRatingsCount()
        );
    }
}