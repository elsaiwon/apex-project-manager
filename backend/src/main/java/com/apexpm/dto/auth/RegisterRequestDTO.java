package com.apexpm.dto.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class RegisterRequestDTO {
    @NotBlank(message = "Username obbligatorio")
    @Size(min = 3, max = 50)
    private String username;

    @NotBlank(message = "Password obbligatoria")
    @Size(min = 6, max = 100)
    private String password;

    @NotBlank(message = "Email obbligatoria")
    @Email(message = "Email non valida")
    private String email;

    private String fullName;
}