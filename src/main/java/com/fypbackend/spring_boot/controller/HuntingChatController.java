package com.fypbackend.spring_boot.controller;

import com.fypbackend.spring_boot.service.HuntingChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/chat")
@RequiredArgsConstructor
public class HuntingChatController {

    private final HuntingChatService chatService;

    @PostMapping
    public Map<String, String> chat(@RequestBody Map<String, String> body) {
        return Map.of(
            "reply",
            chatService.ask(body.get("message"))
        );
    }
}
