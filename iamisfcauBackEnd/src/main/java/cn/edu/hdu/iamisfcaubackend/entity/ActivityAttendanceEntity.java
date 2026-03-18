package cn.edu.hdu.iamisfcaubackend.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "activity_attendance")
public class ActivityAttendanceEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "activity_id")
    private Integer activityId;

    @Column(name = "user_id")
    private String userId;

    @Column(name = "user_name")
    private String userName;

    @Column(name = "sign_in_at")
    private LocalDateTime signInAt;

    @Column(name = "attend_status")
    private String attendStatus;

    @Column(name = "leave_apply_id")
    private String leaveApplyId;

    @Column(name = "is_late")
    private Boolean isLate;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Integer getActivityId() { return activityId; }
    public void setActivityId(Integer activityId) { this.activityId = activityId; }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public String getUserName() { return userName; }
    public void setUserName(String userName) { this.userName = userName; }

    public LocalDateTime getSignInAt() { return signInAt; }
    public void setSignInAt(LocalDateTime signInAt) { this.signInAt = signInAt; }

    public String getAttendStatus() { return attendStatus; }
    public void setAttendStatus(String attendStatus) { this.attendStatus = attendStatus; }

    public String getLeaveApplyId() { return leaveApplyId; }
    public void setLeaveApplyId(String leaveApplyId) { this.leaveApplyId = leaveApplyId; }

    public Boolean getIsLate() { return isLate; }
    public void setIsLate(Boolean isLate) { this.isLate = isLate; }
}