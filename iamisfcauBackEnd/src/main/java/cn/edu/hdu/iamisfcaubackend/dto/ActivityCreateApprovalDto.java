package cn.edu.hdu.iamisfcaubackend.dto;

import java.time.OffsetDateTime;

public record ActivityCreateApprovalDto(
        String applyId,
        String title,
        String organizer,
        String leaderId,
        String leaderName,
        OffsetDateTime startAt,
        OffsetDateTime endAt,
        String location,
        String summary,
        String content,
        OffsetDateTime submittedAt,
        String status,
        String auditorId,
        String auditorName,
        OffsetDateTime auditedAt,
        String comment
) {}