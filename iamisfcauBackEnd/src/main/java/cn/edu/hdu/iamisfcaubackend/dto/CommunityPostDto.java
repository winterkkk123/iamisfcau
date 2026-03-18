package cn.edu.hdu.iamisfcaubackend.dto;

import java.time.OffsetDateTime;
import java.util.List;

public record CommunityPostDto(
        String id,
        CommunityAuthorDto author,
        String createdByUserId,
        OffsetDateTime createdAt,
        String type,
        String relatedActivityId,
        String content,
        List<String> images,
        CommunityStatsDto stats,
        CommunityUserStateDto userState,
        List<CommunityCommentDto> comments
) {}