package com.apexpm.dto.auth;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class LoginRequestDTO {
    @NotBlank(message = "Username obbligatorio")
    private String username;

    @NotBlank(message = "Password obbligatoria")
    private String password;
}