package cn.edu.hdu.iamisfcaubackend.dto;

import java.time.LocalDateTime;

public record AiMessageDto(
        String role,
        String content,
        LocalDateTime createdAt
) {
}