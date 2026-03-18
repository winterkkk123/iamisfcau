package cn.edu.hdu.iamisfcaubackend.dto;

import java.time.OffsetDateTime;

public record MessageDto(
        String role,          // system / other / self
        String content,
        OffsetDateTime time
) {}