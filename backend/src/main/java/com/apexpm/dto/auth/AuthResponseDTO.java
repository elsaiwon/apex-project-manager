package com.apexpm.dto.auth;

import lombok.Data;

@Data
public class AuthResponseDTO {
    private String token;
    private String username;
    private String message;
}