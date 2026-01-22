package com.apexpm.dto.auth;

import lombok.Data;

import java.util.Set;

@Data
public class UserInfoDTO {
    private Long id;
    private String username;
    private String email;
    private String fullName;
    private Set<String> roles;
}