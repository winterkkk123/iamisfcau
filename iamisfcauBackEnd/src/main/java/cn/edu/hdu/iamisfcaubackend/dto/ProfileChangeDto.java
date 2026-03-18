package cn.edu.hdu.iamisfcaubackend.dto;

import java.time.OffsetDateTime;

public record ProfileChangeDto(
        String applyId,
        String userName,
        String userId,
        String applicantName,
        String applicantId,
        String changeType,
        String before,
        String after,
        OffsetDateTime submittedAt,
        String status,
        String approvalComment
) {}