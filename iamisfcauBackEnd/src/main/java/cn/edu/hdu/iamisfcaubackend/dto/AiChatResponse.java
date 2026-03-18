package cn.edu.hdu.iamisfcaubackend.dto;

public record AiChatResponse(
        String conversationId,
        String title,
        String answer
) {
}