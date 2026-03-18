package cn.edu.hdu.iamisfcaubackend.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class UserActivityRelId implements Serializable {

    @Column(name = "user_id", nullable = false, length = 20)
    private String userId;

    @Column(name = "activity_id", nullable = false)
    private Integer activityId;

    @Column(name = "relation_type", nullable = false, length = 20)
    private String relationType;

    public UserActivityRelId() {
    }

    public UserActivityRelId(String userId, Integer activityId, String relationType) {
        this.userId = userId;
        this.activityId = activityId;
        this.relationType = relationType;
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

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public void setActivityId(Integer activityId) {
        this.activityId = activityId;
    }

    public void setRelationType(String relationType) {
        this.relationType = relationType;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof UserActivityRelId)) return false;
        UserActivityRelId that = (UserActivityRelId) o;
        return Objects.equals(userId, that.userId)
                && Objects.equals(activityId, that.activityId)
                && Objects.equals(relationType, that.relationType);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userId, activityId, relationType);
    }
}