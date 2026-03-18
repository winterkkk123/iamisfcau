package cn.edu.hdu.iamisfcaubackend.controller;

import cn.edu.hdu.iamisfcaubackend.dto.*;
import cn.edu.hdu.iamisfcaubackend.service.AiChatService;
import cn.edu.hdu.iamisfcaubackend.service.AiConversationService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/ai")
public class AiController {

    private final AiConversationService aiConversationService;
    private final AiChatService aiChatService;

    public AiController(AiConversationService aiConversationService, AiChatService aiChatService) {
        this.aiConversationService = aiConversationService;
        this.aiChatService = aiChatService;
    }

    @GetMapping("/conversations")
    public List<AiConversationSummaryDto> conversations() {
        return aiConversationService.listConversations();
    }

    @GetMapping("/conversations/{id}/messages")
    public List<AiMessageDto> messages(@PathVariable String id) {
        return aiConversationService.listMessages(id);
    }

    @PostMapping("/conversations")
    public AiConversationDto createConversation() {
        return aiConversationService.createConversation();
    }

    @PostMapping("/chat")
    public AiChatResponse chat(@RequestBody AiChatRequest request) {
        return aiChatService.chat(request);
    }
}