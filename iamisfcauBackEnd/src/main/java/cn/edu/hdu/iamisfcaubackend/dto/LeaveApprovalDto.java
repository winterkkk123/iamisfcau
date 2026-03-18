package cn.edu.hdu.iamisfcaubackend.dto;

import java.time.OffsetDateTime;

public record LeaveApprovalDto(
        String applyId,
        Integer activityId,
        String activityTitle,
        String teacherId,
        String teacherName,
        String applicantId,
        String applicantName,
        String reason,
        OffsetDateTime submittedAt,
        String status,
        String approvalComment
) {}