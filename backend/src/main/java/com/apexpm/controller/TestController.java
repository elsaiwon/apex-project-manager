package com.apexpm.controller;

import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/test")
public class TestController {

    @GetMapping("/public")
    public String publicEndpoint() {
        return "Questo è pubblico";
    }

    @GetMapping("/protected")
    public String protectedEndpoint(Authentication auth) {
        return "Benvenuto " + auth.getName() + "! Ruoli: " + auth.getAuthorities();
    }
}