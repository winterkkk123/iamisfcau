package cn.edu.hdu.iamisfcaubackend.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "user_activity_rel")
@IdClass(UserActivityRelId.class)
public class UserActivityRel {

    @Id
    @Column(name = "user_id", nullable = false, length = 20)
    private String userId;

    @Id
    @Column(name = "activity_id", nullable = false)
    private Integer activityId;

    @Id
    @Column(name = "relation_type", nullable = false, length = 20)
    private String relationType;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    public UserActivityRel() {
    }

    public UserActivityRel(String userId, Integer activityId, String relationType, LocalDateTime createdAt) {
        this.userId = userId;
        this.activityId = activityId;
        this.relationType = relationType;
        this.createdAt = createdAt;
    }

    public String getUserId() {
        return userId;
    }

    public Integer getActivityId() {
        return activityId;
    }

    public String getRelationType() {
        return relationType;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public void setActivityId(Integer activityId) {
        this.activityId = activityId;
    }

    public void setRelationType(String relationType) {
        this.relationType = relationType;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}