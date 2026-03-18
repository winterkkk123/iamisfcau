package cn.edu.hdu.iamisfcaubackend.controller;

import cn.edu.hdu.iamisfcaubackend.dto.ActivityDto;
import cn.edu.hdu.iamisfcaubackend.dto.ActivityJoinRequest;
import cn.edu.hdu.iamisfcaubackend.dto.ActivityJoinResponse;
import cn.edu.hdu.iamisfcaubackend.dto.AttendanceDto;
import cn.edu.hdu.iamisfcaubackend.dto.AttendanceUpdateRequest;
import cn.edu.hdu.iamisfcaubackend.entity.ActivityAttendanceEntity;
import cn.edu.hdu.iamisfcaubackend.repo.ActivityRepository;
import cn.edu.hdu.iamisfcaubackend.repo.UserRepository;
import cn.edu.hdu.iamisfcaubackend.entity.ActivityEntity;
import cn.edu.hdu.iamisfcaubackend.entity.UserActivityRelEntity;
import cn.edu.hdu.iamisfcaubackend.entity.UserActivityRelId;
import cn.edu.hdu.iamisfcaubackend.repo.ActivityAttendanceRepository;
import cn.edu.hdu.iamisfcaubackend.repo.UserActivityRelRepository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/activities")
public class ActivityController {

    private final ActivityRepository repo;
    private final ActivityAttendanceRepository attendanceRepo;
    private final UserRepository userRepo;
    private final UserActivityRelRepository userActivityRelRepo;
    private final ZoneOffset offset = ZoneOffset.ofHours(8);

    public ActivityController(ActivityRepository repo,
                              ActivityAttendanceRepository attendanceRepo,
                              UserRepository userRepo,
                              UserActivityRelRepository userActivityRelRepo) {
        this.repo = repo;
        this.attendanceRepo = attendanceRepo;
        this.userRepo = userRepo;
        this.userActivityRelRepo = userActivityRelRepo;
    }

    @GetMapping
    public List<ActivityDto> list() {
        return repo.findAll().stream().map(this::toDto).collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    public ActivityDto detail(@PathVariable Integer id) {
        ActivityEntity e = repo.findById(id).orElseThrow(() -> new RuntimeException("活动不存在"));
        return toDto(e);
    }

    @GetMapping("/{id}/attendance")
    public List<AttendanceDto> attendance(@PathVariable Integer id) {
        return attendanceRepo.findByActivityIdOrderByIdAsc(id).stream().map(a ->
                new AttendanceDto(
                        a.getUserId(),
                        a.getUserName(),
                        a.getSignInAt() == null ? null : a.getSignInAt().atOffset(offset),
                        a.getAttendStatus(),
                        a.getLeaveApplyId(),
                        Boolean.TRUE.equals(a.getIsLate())
                )
        ).collect(Collectors.toList());
    }

    @PutMapping("/{id}/attendance/{userId}")
    @Transactional
    public AttendanceDto updateAttendance(@PathVariable Integer id,
                                          @PathVariable String userId,
                                          @RequestBody AttendanceUpdateRequest body) {
        repo.findById(id).orElseThrow(() -> new RuntimeException("活动不存在"));
        var user = userRepo.findById(userId).orElseThrow(() -> new RuntimeException("用户不存在"));

        ActivityAttendanceEntity row = attendanceRepo.findByActivityIdAndUserId(id, userId).orElseGet(() -> {
            ActivityAttendanceEntity x = new ActivityAttendanceEntity();
            x.setActivityId(id);
            x.setUserId(userId);
            x.setUserName(user.getName());
            x.setLeaveApplyId(null);
            x.setSignInAt(null);
            x.setAttendStatus("未出勤");
            x.setIsLate(false);
            return x;
        });

        row.setAttendStatus(body.getAttendStatus());
        boolean late = "已出勤".equals(body.getAttendStatus()) && Boolean.TRUE.equals(body.getIsLate());
        row.setIsLate(late);

        if ("已出勤".equals(body.getAttendStatus())) {
            if (row.getSignInAt() == null) {
                row.setSignInAt(LocalDateTime.now());
            }
        } else {
            row.setSignInAt(null);
        }

        if (!"请假".equals(body.getAttendStatus())) {
            row.setLeaveApplyId(null);
        }

        attendanceRepo.save(row);

        return new AttendanceDto(
                row.getUserId(),
                row.getUserName(),
                row.getSignInAt() == null ? null : row.getSignInAt().atOffset(offset),
                row.getAttendStatus(),
                row.getLeaveApplyId(),
                Boolean.TRUE.equals(row.getIsLate())
        );
    }

    @PostMapping("/{id}/join")
    @Transactional
    public ActivityJoinResponse join(@PathVariable Integer id,
                                     @RequestBody ActivityJoinRequest body) {
        String userId = body.getUserId();
        if (userId == null || userId.isBlank()) {
            throw new RuntimeException("userId不能为空");
        }

        ActivityEntity activity = repo.findById(id).orElseThrow(() -> new RuntimeException("活动不存在"));
        var user = userRepo.findById(userId).orElseThrow(() -> new RuntimeException("用户不存在"));

        UserActivityRelId relId = new UserActivityRelId(userId, id, "join");
        boolean exists = userActivityRelRepo.existsById(relId);
        if (exists) {
            return new ActivityJoinResponse(id, userId, true, "您已报名，无需重复报名");
        }

        UserActivityRelEntity rel = new UserActivityRelEntity();
        rel.setId(relId);
        rel.setCreatedAt(LocalDateTime.now());
        userActivityRelRepo.save(rel);

        ActivityAttendanceEntity attendance = attendanceRepo.findByActivityIdAndUserId(id, userId).orElseGet(() -> {
            ActivityAttendanceEntity x = new ActivityAttendanceEntity();
            x.setActivityId(id);
            x.setUserId(userId);
            x.setUserName(user.getName());
            x.setSignInAt(null);
            x.setAttendStatus("未出勤");
            x.setLeaveApplyId(null);
            x.setIsLate(false);
            return x;
        });

        attendance.setUserName(user.getName());
        if (attendance.getAttendStatus() == null || attendance.getAttendStatus().isBlank()) {
            attendance.setAttendStatus("未出勤");
        }
        if (attendance.getIsLate() == null) {
            attendance.setIsLate(false);
        }

        attendanceRepo.save(attendance);

        return new ActivityJoinResponse(activity.getId(), userId, true, "报名成功");
    }

    @DeleteMapping("/{id}/join")
    @Transactional
    public ActivityJoinResponse cancelJoin(@PathVariable Integer id,
                                           @RequestParam String userId) {
        if (userId == null || userId.isBlank()) {
            throw new RuntimeException("userId不能为空");
        }

        repo.findById(id).orElseThrow(() -> new RuntimeException("活动不存在"));
        userRepo.findById(userId).orElseThrow(() -> new RuntimeException("用户不存在"));

        UserActivityRelId relId = new UserActivityRelId(userId, id, "join");
        boolean exists = userActivityRelRepo.existsById(relId);
        if (!exists) {
            return new ActivityJoinResponse(id, userId, false, "当前未报名");
        }

        userActivityRelRepo.deleteById(relId);
        attendanceRepo.deleteByActivityIdAndUserId(id, userId);

        return new ActivityJoinResponse(id, userId, false, "取消报名成功");
    }

    @GetMapping("/joined")
    public List<Integer> joined(@RequestParam String userId) {
        if (userId == null || userId.isBlank()) {
            throw new RuntimeException("userId不能为空");
        }

        userRepo.findById(userId).orElseThrow(() -> new RuntimeException("用户不存在"));

        return userActivityRelRepo.findByIdUserIdAndIdRelationType(userId, "join")
                .stream()
                .map(x -> x.getId().getActivityId())
                .collect(Collectors.toList());
    }

    private ActivityDto toDto(ActivityEntity e) {
        ActivityDto dto = new ActivityDto();
        dto.setId(e.getId());
        dto.setTitle(e.getTitle());
        dto.setOrganizer(e.getOrganizer());
        dto.setLeaderId(e.getLeaderId());
        dto.setLeaderName(e.getLeaderName());
        dto.setDate(e.getActivityDate());
        dto.setTime(e.getActivityTime());
        dto.setStartAt(e.getStartAt());
        dto.setEndAt(e.getEndAt());
        dto.setLocation(e.getLocation());
        dto.setContent(e.getContent());
        dto.setSummary(e.getSummary());
        return dto;
    }
}