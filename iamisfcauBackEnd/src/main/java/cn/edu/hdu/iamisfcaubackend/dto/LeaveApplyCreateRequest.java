package cn.edu.hdu.iamisfcaubackend.dto;

public record LeaveApplyCreateRequest(
        Integer activityId,
        String applicantId,
        String reason
) {
}