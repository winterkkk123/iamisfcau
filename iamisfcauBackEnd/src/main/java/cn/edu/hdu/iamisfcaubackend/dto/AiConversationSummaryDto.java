package cn.edu.hdu.iamisfcaubackend.dto;

import java.time.LocalDateTime;

public record AiConversationSummaryDto(
        String id,
        String title,
        LocalDateTime createdAt
) {
}