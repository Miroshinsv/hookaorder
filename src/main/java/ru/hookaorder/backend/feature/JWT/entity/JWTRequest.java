package ru.hookaorder.backend.feature.JWT.entity;

import lombok.Data;

@Data
public class JWTRequest {
    private String login;
    private String password;
}
