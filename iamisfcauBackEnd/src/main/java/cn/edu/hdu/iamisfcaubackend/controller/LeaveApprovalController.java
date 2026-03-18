package cn.edu.hdu.iamisfcaubackend.controller;

import cn.edu.hdu.iamisfcaubackend.dto.ApprovalRequest;
import cn.edu.hdu.iamisfcaubackend.dto.LeaveApprovalDto;
import cn.edu.hdu.iamisfcaubackend.dto.LeaveApplyCreateRequest;
import cn.edu.hdu.iamisfcaubackend.entity.ActivityAttendanceEntity;
import cn.edu.hdu.iamisfcaubackend.entity.ActivityLeaveApplicationEntity;
import cn.edu.hdu.iamisfcaubackend.repo.ActivityAttendanceRepository;
import cn.edu.hdu.iamisfcaubackend.repo.ActivityLeaveApplicationRepository;
import cn.edu.hdu.iamisfcaubackend.repo.ActivityRepository;
import cn.edu.hdu.iamisfcaubackend.repo.UserRepository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;

@RestController
@RequestMapping("/api/leave-approvals")
public class LeaveApprovalController {

    private final ActivityLeaveApplicationRepository repo;
    private final ActivityAttendanceRepository attendanceRepo;
    private final ActivityRepository activityRepo;
    private final UserRepository userRepo;

    private final ZoneOffset offset = ZoneOffset.ofHours(8);

    public LeaveApprovalController(ActivityLeaveApplicationRepository repo,
                                   ActivityAttendanceRepository attendanceRepo,
                                   ActivityRepository activityRepo,
                                   UserRepository userRepo) {
        this.repo = repo;
        this.attendanceRepo = attendanceRepo;
        this.activityRepo = activityRepo;
        this.userRepo = userRepo;
    }

    @GetMapping
    public List<LeaveApprovalDto> list() {
        return repo.findAllByOrderBySubmittedAtDesc().stream().map(this::toDto).toList();
    }

    @PostMapping
    @Transactional
    public LeaveApprovalDto create(@RequestBody LeaveApplyCreateRequest body) {
        if (body == null || body.activityId() == null || body.applicantId() == null || body.reason() == null || body.reason().isBlank()) {
            throw new IllegalArgumentException("参数不完整");
        }

        var activity = activityRepo.findById(body.activityId())
                .orElseThrow(() -> new IllegalArgumentException("活动不存在"));

        var applicant = userRepo.findById(body.applicantId())
                .orElseThrow(() -> new IllegalArgumentException("申请人不存在"));

        boolean existsPending = repo.existsByActivityIdAndApplicantIdAndStatus(
                body.activityId(),
                body.applicantId(),
                "待审核"
        );
        if (existsPending) {
            throw new IllegalArgumentException("该活动已有待审核请假申请，请勿重复提交");
        }

        ActivityAttendanceEntity att = attendanceRepo
                .findByActivityIdAndUserId(body.activityId(), body.applicantId())
                .orElseThrow(() -> new IllegalArgumentException("当前用户不在该活动签到名单中，不能请假"));

        ActivityLeaveApplicationEntity entity = new ActivityLeaveApplicationEntity();
        entity.setApplyId(generateApplyId());
        entity.setActivityId(activity.getId());
        entity.setActivityTitle(activity.getTitle());
        entity.setTeacherId(activity.getLeaderId());
        entity.setTeacherName(activity.getLeaderName());
        entity.setApplicantId(applicant.getId());
        entity.setApplicantName(applicant.getName());
        entity.setReason(body.reason().trim());
        entity.setSubmittedAt(LocalDateTime.now());
        entity.setStatus("待审核");
        entity.setApprovalComment("");

        repo.save(entity);

        return toDto(entity);
    }

    @DeleteMapping("/{applyId}")
    @Transactional
    public void withdraw(@PathVariable String applyId,
                         @RequestParam String applicantId) {
        var app = repo.findById(applyId)
                .orElseThrow(() -> new IllegalArgumentException("请假申请不存在"));

        if (!"待审核".equals(app.getStatus())) {
            throw new IllegalArgumentException("仅待审核状态可撤回");
        }

        if (!app.getApplicantId().equals(applicantId)) {
            throw new IllegalArgumentException("只能撤回自己的请假申请");
        }

        repo.delete(app);
    }

    @PostMapping("/{applyId}/approve")
    @Transactional
    public LeaveApprovalDto approve(@PathVariable String applyId,
                                    @RequestBody(required = false) ApprovalRequest body) {
        var app = repo.findById(applyId)
                .orElseThrow(() -> new IllegalArgumentException("请假申请不存在"));

        if (!"待审核".equals(app.getStatus())) {
            throw new IllegalArgumentException("当前申请状态不可审批");
        }

        app.setStatus("已同意");
        app.setApprovalComment(body == null || body.approvalComment() == null ? "" : body.approvalComment());
        repo.save(app);

        ActivityAttendanceEntity att = attendanceRepo
                .findByActivityIdAndUserId(app.getActivityId(), app.getApplicantId())
                .orElseThrow(() -> new IllegalArgumentException("未找到对应出勤记录"));

        att.setAttendStatus("请假");
        att.setLeaveApplyId(app.getApplyId());
        att.setIsLate(false);
        attendanceRepo.save(att);

        return toDto(app);
    }

    @PostMapping("/{applyId}/reject")
    @Transactional
    public LeaveApprovalDto reject(@PathVariable String applyId,
                                   @RequestBody(required = false) ApprovalRequest body) {
        var app = repo.findById(applyId)
                .orElseThrow(() -> new IllegalArgumentException("请假申请不存在"));

        if (!"待审核".equals(app.getStatus())) {
            throw new IllegalArgumentException("当前申请状态不可审批");
        }

        app.setStatus("已拒绝");
        app.setApprovalComment(body == null || body.approvalComment() == null ? "" : body.approvalComment());
        repo.save(app);

        ActivityAttendanceEntity att = attendanceRepo
                .findByActivityIdAndUserId(app.getActivityId(), app.getApplicantId())
                .orElseThrow(() -> new IllegalArgumentException("未找到对应出勤记录"));

        att.setAttendStatus("未出勤");
        att.setLeaveApplyId(null);
        att.setIsLate(false);
        attendanceRepo.save(att);

        return toDto(app);
    }

    private LeaveApprovalDto toDto(ActivityLeaveApplicationEntity e) {
        return new LeaveApprovalDto(
                e.getApplyId(),
                e.getActivityId(),
                e.getActivityTitle(),
                e.getTeacherId(),
                e.getTeacherName(),
                e.getApplicantId(),
                e.getApplicantName(),
                e.getReason(),
                e.getSubmittedAt().atOffset(offset),
                e.getStatus(),
                e.getApprovalComment()
        );
    }

    private String generateApplyId() {
        return "LA-" + System.currentTimeMillis();
    }
}