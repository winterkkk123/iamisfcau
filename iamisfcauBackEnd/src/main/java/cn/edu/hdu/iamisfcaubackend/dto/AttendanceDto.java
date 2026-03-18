package cn.edu.hdu.iamisfcaubackend.dto;

import java.time.OffsetDateTime;

public record AttendanceDto(
        String userId,
        String userName,
        OffsetDateTime signInAt,   // 没签到就是 null（前端可转成 ''）
        String attendStatus,       // 未出勤/已出勤/请假
        String leaveApplyId,       // 可为 null
        boolean isLate
) {}