package cn.edu.hdu.iamisfcaubackend.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "community_posts")
public class CommunityPostEntity {

    @Id
    private String id;

    @Column(name = "created_by_user_id")
    private String createdByUserId;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    private String type;

    @Column(name = "related_activity_id")
    private Integer relatedActivityId;

    @Column(columnDefinition = "TEXT")
    private String content;

    @Column(columnDefinition = "JSON")
    private String images; // JSON 数组字符串

    @Column(name = "views_count")
    private Integer viewsCount;

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getCreatedByUserId() { return createdByUserId; }
    public void setCreatedByUserId(String createdByUserId) { this.createdByUserId = createdByUserId; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public Integer getRelatedActivityId() { return relatedActivityId; }
    public void setRelatedActivityId(Integer relatedActivityId) { this.relatedActivityId = relatedActivityId; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public String getImages() { return images; }
    public void setImages(String images) { this.images = images; }

    public Integer getViewsCount() { return viewsCount; }
    public void setViewsCount(Integer viewsCount) { this.viewsCount = viewsCount; }
}