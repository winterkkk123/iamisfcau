package cn.edu.hdu.iamisfcaubackend.dto;

import java.time.OffsetDateTime;

public record MessageConversationDto(
        String id,
        String name,
        String avatar,
        OffsetDateTime updatedAt,
        String lastMessage
) {}