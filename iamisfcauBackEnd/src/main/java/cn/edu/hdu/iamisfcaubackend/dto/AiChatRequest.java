package cn.edu.hdu.iamisfcaubackend.dto;

public record AiChatRequest(
        String conversationId,
        String message,
        String userId,
        String userName
) {
}