package cn.edu.hdu.iamisfcaubackend.controller;

import cn.edu.hdu.iamisfcaubackend.dto.NoticeDto;
import cn.edu.hdu.iamisfcaubackend.entity.NoticeEntity;
import cn.edu.hdu.iamisfcaubackend.repo.NoticeRepository;
import org.springframework.web.bind.annotation.*;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.time.ZoneOffset;
import java.util.List;

@RestController
@RequestMapping("/api/notices")
public class NoticeController {

    private final NoticeRepository repo;
    private final ObjectMapper mapper;
    private final ZoneOffset offset = ZoneOffset.ofHours(8);

    public NoticeController(NoticeRepository repo, ObjectMapper mapper) {
        this.repo = repo;
        this.mapper = mapper;
    }

    @GetMapping
    public List<NoticeDto> list() {
        return repo.findAll().stream()
                .map(this::toDto)
                .toList();
    }

    @GetMapping("/{id}")
    public NoticeDto detail(@PathVariable Integer id) {
        NoticeEntity e = repo.findById(id)
                .orElseThrow(() -> new RuntimeException("通知不存在"));
        return toDto(e);
    }

    private NoticeDto toDto(NoticeEntity e) {
        List<String> tags;
        try {
            tags = mapper.readValue(e.getTags(), new TypeReference<List<String>>() {});
        } catch (Exception ex) {
            tags = List.of();
        }

        return new NoticeDto(
                e.getId(),
                e.getTitle(),
                e.getContent(),
                tags,
                e.getNoticeTime().atOffset(offset)
        );
    }
}