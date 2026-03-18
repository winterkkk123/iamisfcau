package cn.edu.hdu.iamisfcaubackend.entity;

import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

import java.time.LocalDateTime;

@Entity
@Table(name = "user_activity_rel")
public class UserActivityRelEntity {

    @EmbeddedId
    private UserActivityRelId id;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    public UserActivityRelEntity() {
    }

    public UserActivityRelId getId() {
        return id;
    }

    public void setId(UserActivityRelId id) {
        this.id = id;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}