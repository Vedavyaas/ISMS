package com.adl.isms.controller;

import com.adl.isms.service.ChatBotService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ChatBotController {
    private final ChatBotService chatBotService;

    public ChatBotController(ChatBotService chatBotService) {
        this.chatBotService = chatBotService;
    }

    @PreAuthorize("hasAuthority('SCOPE_ROLE_STUDENT')")
    @GetMapping("/get/suggestion")
    public ResponseEntity<String> getSuggestion(@RequestParam String question, @AuthenticationPrincipal Jwt jwt) {
        return chatBotService.getSuggestion(question, jwt.getSubject());
    }
}
