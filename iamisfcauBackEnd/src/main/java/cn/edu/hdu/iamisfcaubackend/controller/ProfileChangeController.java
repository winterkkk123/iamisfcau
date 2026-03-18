package cn.edu.hdu.iamisfcaubackend.controller;

import cn.edu.hdu.iamisfcaubackend.dto.ApprovalRequest;
import cn.edu.hdu.iamisfcaubackend.dto.ProfileChangeCreateRequest;
import cn.edu.hdu.iamisfcaubackend.dto.ProfileChangeDto;
import cn.edu.hdu.iamisfcaubackend.entity.ProfileChangeApplicationEntity;
import cn.edu.hdu.iamisfcaubackend.repo.ProfileChangeApplicationRepository;
import cn.edu.hdu.iamisfcaubackend.repo.ActivityRepository;
import cn.edu.hdu.iamisfcaubackend.repo.UserRepository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/profile-change-applications")
public class ProfileChangeController {

    private final ProfileChangeApplicationRepository repo;
    private final UserRepository userRepo;
    private final ZoneOffset offset = ZoneOffset.ofHours(8);

    public ProfileChangeController(ProfileChangeApplicationRepository repo, UserRepository userRepo) {
        this.repo = repo;
        this.userRepo = userRepo;
    }

    @GetMapping
    public List<ProfileChangeDto> list() {
        return repo.findAllByOrderBySubmittedAtDesc().stream().map(this::toDto).toList();
    }

    /**
     * 新增资料变更申请
     */
    @PostMapping
    @Transactional
    public ProfileChangeDto create(@RequestBody ProfileChangeCreateRequest body) {
        var targetUser = userRepo.findById(body.getTargetUserId()).orElseThrow();
        var applicantUser = userRepo.findById(body.getApplicantId()).orElseThrow();

        ProfileChangeApplicationEntity app = new ProfileChangeApplicationEntity();
        app.setApplyId(generateApplyId());
        app.setTargetUserId(targetUser.getId());
        app.setTargetUserName(targetUser.getName());
        app.setApplicantId(applicantUser.getId());
        app.setApplicantName(applicantUser.getName());
        app.setChangeType(body.getChangeType());
        app.setBeforeValue(body.getBeforeValue());
        app.setAfterValue(body.getAfterValue());
        app.setSubmittedAt(LocalDateTime.now());
        app.setStatus("待审核");
        app.setApprovalComment("");

        repo.save(app);
        return toDto(app);
    }

    @PostMapping("/{applyId}/approve")
    @Transactional
    public ProfileChangeDto approve(@PathVariable String applyId,
                                    @RequestBody(required = false) ApprovalRequest body) {
        var app = repo.findById(applyId).orElseThrow();

        app.setStatus("已同意");
        app.setApprovalComment(body == null ? null : body.approvalComment());
        repo.save(app);

        // 同意后同步更新 users
        var user = userRepo.findById(app.getTargetUserId()).orElseThrow();
        String after = app.getAfterValue();
        String type = app.getChangeType();

        if (type != null && type.contains("电话")) {
            user.setPhone(after);
        } else if (type != null && type.contains("所属部门")) {
            user.setDepartment(after);
            if (user.getCollege() == null || user.getCollege().isBlank()) {
                user.setCollege(after);
            }
        } else if (type != null && type.contains("性别")) {
            user.setGender(after);
        } else if (type != null && type.contains("密码")) {
            user.setPassword(after);
        }

        userRepo.save(user);
        return toDto(app);
    }

    @PostMapping("/{applyId}/reject")
    @Transactional
    public ProfileChangeDto reject(@PathVariable String applyId,
                                   @RequestBody(required = false) ApprovalRequest body) {
        var app = repo.findById(applyId).orElseThrow();
        app.setStatus("已拒绝");
        app.setApprovalComment(body == null ? null : body.approvalComment());
        repo.save(app);
        return toDto(app);
    }

    private String generateApplyId() {
        LocalDateTime now = LocalDateTime.now();
        String datePart = String.format("%04d%02d%02d",
                now.getYear(),
                now.getMonthValue(),
                now.getDayOfMonth());
        String randomPart = UUID.randomUUID()
                .toString()
                .replace("-", "")
                .substring(0, 4)
                .toUpperCase();
        return "AR-" + datePart + "-" + randomPart;
    }

    private ProfileChangeDto toDto(ProfileChangeApplicationEntity e) {
        return new ProfileChangeDto(
                e.getApplyId(),
                e.getTargetUserName(),
                e.getTargetUserId(),
                e.getApplicantName(),
                e.getApplicantId(),
                e.getChangeType(),
                e.getBeforeValue(),
                e.getAfterValue(),
                e.getSubmittedAt() == null ? null : e.getSubmittedAt().atOffset(offset),
                e.getStatus(),
                e.getApprovalComment()
        );
    }
}