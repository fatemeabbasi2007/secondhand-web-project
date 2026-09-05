package org.example.secondhandweb.dto;

public record RegisterRequestDTO(
        String username,
        String password,
        String email,
        String phoneNum,
        String fullName
) {}