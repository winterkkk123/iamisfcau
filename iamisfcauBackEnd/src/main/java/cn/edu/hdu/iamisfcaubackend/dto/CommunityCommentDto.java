package cn.edu.hdu.iamisfcaubackend.dto;

import java.time.OffsetDateTime;
import java.util.List;

public record CommunityCommentDto(
        String id,
        CommunityCommentUserDto user,
        OffsetDateTime createdAt,
        String content,
        List<CommunityCommentDto> replies
) {}