package com.adl.isms.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class AuthPageController {
    @GetMapping("/")
    public String index() {
        return "login";
    }
    @GetMapping("/login")
    public String loginPage() {
        return "login";
    }

    @GetMapping("/forgot-password")
    public String forgotPasswordPage() {
        return "forgot-password";
    }
}
