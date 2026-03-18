package cn.edu.hdu.iamisfcaubackend.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(
        name = "activity_attendance",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_attendance", columnNames = {"activity_id", "user_id"})
        }
)
public class ActivityAttendance {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "activity_id", nullable = false)
    private Integer activityId;

    @Column(name = "user_id", nullable = false, length = 20)
    private String userId;

    @Column(name = "user_name", nullable = false, length = 50)
    private String userName;

    @Column(name = "sign_in_at")
    private LocalDateTime signInAt;

    @Column(name = "attend_status", nullable = false, length = 20)
    private String attendStatus;

    @Column(name = "leave_apply_id", length = 30)
    private String leaveApplyId;

    @Column(name = "is_late", nullable = false)
    private Boolean isLate;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    public ActivityAttendance() {
    }

    public Long getId() {
        return id;
    }

    public Integer getActivityId() {
        return activityId;
    }

    public String getUserId() {
        return userId;
    }

    public String getUserName() {
        return userName;
    }

    public LocalDateTime getSignInAt() {
        return signInAt;
    }

    public String getAttendStatus() {
        return attendStatus;
    }

    public String getLeaveApplyId() {
        return leaveApplyId;
    }

    public Boolean getIsLate() {
        return isLate;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setActivityId(Integer activityId) {
        this.activityId = activityId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public void setSignInAt(LocalDateTime signInAt) {
        this.signInAt = signInAt;
    }

    public void setAttendStatus(String attendStatus) {
        this.attendStatus = attendStatus;
    }

    public void setLeaveApplyId(String leaveApplyId) {
        this.leaveApplyId = leaveApplyId;
    }

    public void setIsLate(Boolean late) {
        isLate = late;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}