package cn.edu.hdu.iamisfcaubackend.controller;

import cn.edu.hdu.iamisfcaubackend.dto.ActivityCreateApplyRequest;
import cn.edu.hdu.iamisfcaubackend.dto.ActivityCreateApprovalDto;
import cn.edu.hdu.iamisfcaubackend.dto.ApprovalRequest;
import cn.edu.hdu.iamisfcaubackend.entity.ActivityCreateApplicationEntity;
import cn.edu.hdu.iamisfcaubackend.repo.ActivityRepository;
import cn.edu.hdu.iamisfcaubackend.repo.UserRepository;
import cn.edu.hdu.iamisfcaubackend.entity.ActivityEntity;
import cn.edu.hdu.iamisfcaubackend.entity.UserActivityRelEntity;
import cn.edu.hdu.iamisfcaubackend.entity.UserActivityRelId;
import cn.edu.hdu.iamisfcaubackend.repo.ActivityCreateApplicationRepository;
import cn.edu.hdu.iamisfcaubackend.repo.UserActivityRelRepository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/activity-create-approvals")
public class ActivityCreateApprovalController {

    private final ActivityCreateApplicationRepository repo;
    private final UserRepository userRepo;
    private final ActivityRepository activityRepo;
    private final UserActivityRelRepository userActivityRelRepo;
    private final ZoneOffset offset = ZoneOffset.ofHours(8);

    public ActivityCreateApprovalController(
            ActivityCreateApplicationRepository repo,
            UserRepository userRepo,
            ActivityRepository activityRepo,
            UserActivityRelRepository userActivityRelRepo
    ) {
        this.repo = repo;
        this.userRepo = userRepo;
        this.activityRepo = activityRepo;
        this.userActivityRelRepo = userActivityRelRepo;
    }

    @GetMapping
    public List<ActivityCreateApprovalDto> list() {
        return repo.findAllByOrderBySubmittedAtDesc().stream().map(this::toDto).toList();
    }

    @PostMapping
    @Transactional
    public ActivityCreateApprovalDto create(@RequestBody ActivityCreateApplyRequest body) {
        var leader = userRepo.findById(body.getLeaderId()).orElseThrow(() -> new RuntimeException("负责人不存在"));

        ActivityCreateApplicationEntity e = new ActivityCreateApplicationEntity();
        e.setApplyId(generateApplyId());
        e.setTitle(body.getTitle());
        e.setOrganizer(body.getOrganizer());
        e.setLeaderId(leader.getId());
        e.setLeaderName(leader.getName());
        e.setStartAt(LocalDateTime.parse(body.getStartAt().replace("Z", "")));
        e.setEndAt(LocalDateTime.parse(body.getEndAt().replace("Z", "")));
        e.setLocation(body.getLocation());
        e.setSummary(body.getSummary());
        e.setContent(body.getContent());
        e.setSubmittedAt(LocalDateTime.now());
        e.setStatus("待审核");
        e.setAuditorId(null);
        e.setAuditorName(null);
        e.setAuditedAt(null);
        e.setComment(null);

        repo.save(e);
        return toDto(e);
    }

    @DeleteMapping("/{applyId}")
    @Transactional
    public void withdraw(@PathVariable String applyId) {
        var app = repo.findById(applyId).orElseThrow(() -> new RuntimeException("申请不存在"));
        if (!"待审核".equals(app.getStatus())) {
            throw new RuntimeException("仅待审核申请可撤回");
        }
        repo.delete(app);
    }

    @PostMapping("/{applyId}/approve")
    @Transactional
    public ActivityCreateApprovalDto approve(@PathVariable String applyId,
                                             @RequestBody(required = false) ApprovalRequest body) {
        var app = repo.findById(applyId).orElseThrow(() -> new RuntimeException("申请不存在"));
        if (!"待审核".equals(app.getStatus())) {
            throw new RuntimeException("该申请已处理，不能重复审批");
        }

        app.setStatus("已同意");
        app.setComment(body == null ? null : body.approvalComment());
        app.setAuditedAt(LocalDateTime.now());
        repo.save(app);

        Integer nextId = activityRepo.findAll().stream()
                .map(ActivityEntity::getId)
                .max(Integer::compareTo)
                .orElse(0) + 1;

        ActivityEntity a = new ActivityEntity();
        a.setId(nextId);
        a.setTitle(app.getTitle());
        a.setOrganizer(app.getOrganizer());
        a.setLeaderId(app.getLeaderId());
        a.setLeaderName(app.getLeaderName());
        a.setActivityDate(app.getStartAt().toLocalDate());
        a.setActivityTime(app.getStartAt().toLocalTime().withSecond(0).withNano(0).toString());
        a.setStartAt(app.getStartAt());
        a.setEndAt(app.getEndAt());
        a.setLocation(app.getLocation());
        a.setSummary(app.getSummary());
        a.setContent(app.getContent());
        activityRepo.save(a);

        UserActivityRelId relId = new UserActivityRelId(app.getLeaderId(), nextId, "manage");
        if (!userActivityRelRepo.existsById(relId)) {
            UserActivityRelEntity rel = new UserActivityRelEntity();
            rel.setId(relId);
            rel.setCreatedAt(LocalDateTime.now());
            userActivityRelRepo.save(rel);
        }

        return toDto(app);
    }

    @PostMapping("/{applyId}/reject")
    @Transactional
    public ActivityCreateApprovalDto reject(@PathVariable String applyId,
                                            @RequestBody(required = false) ApprovalRequest body) {
        var app = repo.findById(applyId).orElseThrow(() -> new RuntimeException("申请不存在"));
        if (!"待审核".equals(app.getStatus())) {
            throw new RuntimeException("该申请已处理，不能重复审批");
        }

        app.setStatus("已拒绝");
        app.setComment(body == null ? null : body.approvalComment());
        app.setAuditedAt(LocalDateTime.now());
        repo.save(app);
        return toDto(app);
    }

    private String generateApplyId() {
        LocalDateTime now = LocalDateTime.now();
        String datePart = String.format("%04d%02d%02d", now.getYear(), now.getMonthValue(), now.getDayOfMonth());
        String randomPart = UUID.randomUUID().toString().replace("-", "").substring(0, 4).toUpperCase();
        return "AC-" + datePart + "-" + randomPart;
    }

    private ActivityCreateApprovalDto toDto(ActivityCreateApplicationEntity e) {
        return new ActivityCreateApprovalDto(
                e.getApplyId(),
                e.getTitle(),
                e.getOrganizer(),
                e.getLeaderId(),
                e.getLeaderName(),
                e.getStartAt() == null ? null : e.getStartAt().atOffset(offset),
                e.getEndAt() == null ? null : e.getEndAt().atOffset(offset),
                e.getLocation(),
                e.getSummary(),
                e.getContent(),
                e.getSubmittedAt() == null ? null : e.getSubmittedAt().atOffset(offset),
                e.getStatus(),
                e.getAuditorId(),
                e.getAuditorName(),
                e.getAuditedAt() == null ? null : e.getAuditedAt().atOffset(offset),
                e.getComment()
        );
    }
}