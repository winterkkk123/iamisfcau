package cn.edu.hdu.iamisfcaubackend.dto;

import java.time.OffsetDateTime;
import java.util.List;

public record NoticeDto(
        Integer id,
        String title,
        String content,
        List<String> tags,
        OffsetDateTime time
) {}