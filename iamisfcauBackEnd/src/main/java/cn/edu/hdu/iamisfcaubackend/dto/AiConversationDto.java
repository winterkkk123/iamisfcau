package cn.edu.hdu.iamisfcaubackend.dto;

import java.time.LocalDateTime;
import java.util.List;

public record AiConversationDto(
        String id,
        String title,
        LocalDateTime createdAt,
        List<AiMessageDto> messages
) {
}